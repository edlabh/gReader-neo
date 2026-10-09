#!/usr/bin/env python3
"""Constrói inventário de preferências do gReader a partir dos dumps do aapt2."""
import json, re, os, glob

BASE = os.path.dirname(os.path.abspath(__file__))

# ---------- 1. resources ----------
id_to_name = {}
name_to_value = {}

res_path = os.path.join(BASE, 'resources.txt')
cur_id = cur_name = cur_type = None
cur_vals = []
array_buf = None

def flush():
    global cur_id, cur_name, cur_type, cur_vals
    if cur_name:
        id_to_name[cur_id] = f'{cur_type}/{cur_name}'
        name_to_value[f'{cur_type}/{cur_name}'] = cur_vals
    cur_id = cur_name = cur_type = None
    cur_vals = []

with open(res_path, encoding='utf-8', errors='replace') as fh:
    for line in fh:
        m = re.match(r'\s*resource 0x([0-9a-f]+) ([a-z]+)/(\S+)\s*$', line)
        if m:
            flush()
            cur_id, cur_type, cur_name = m.group(1), m.group(2), m.group(3)
            array_buf = None
            continue
        if cur_name is None:
            continue
        s = line.strip()
        if array_buf is not None:
            array_buf += ' ' + s
            if s.endswith(']'):
                items = array_buf[array_buf.index('[') + 1:array_buf.rindex(']')].split(',')
                items = [i.strip() for i in items if i.strip()]
                cur_vals.append([i[1:-1] if i.startswith('"') and i.endswith('"') else i for i in items])
                array_buf = None
            continue
        if s.startswith('['):
            array_buf = s
            if s.endswith(']'):
                items = s[1:-1].split(',')
                items = [i.strip() for i in items if i.strip()]
                cur_vals.append([i[1:-1] if i.startswith('"') and i.endswith('"') else i for i in items])
                array_buf = None
            continue
        if s.startswith('() (array)') or s.startswith('() (plurals)'):
            continue
        m3 = re.match(r'^\(([^)]*)\) (.*)$', s)
        if m3:
            v = m3.group(2).strip()
            if v.startswith('(') and ') ' in v:
                v = v.split(') ', 1)[1].strip()
            if v.startswith('"') and v.endswith('"'):
                v = v[1:-1].replace('\\"', '"').replace('\\n', '\n').replace("\\'", "'")
            cur_vals.append(v)
flush()

def resolve_name(name):
    vals = name_to_value.get(name)
    if not vals:
        return None
    v = vals[0]
    if isinstance(v, list):
        out = []
        for item in v:
            if item.startswith('@'):
                ref = item[1:]
                if ref.startswith('string/'):
                    rv = name_to_value.get(ref)
                    out.append(rv[0] if rv else item)
                else:
                    out.append(item)
            else:
                out.append(item)
        return out
    return v

def resolve_ref(val):
    if val is None:
        return None
    m = re.match(r'^@0x([0-9a-f]+)$', val)
    if not m:
        return val
    name = id_to_name.get(m.group(1))
    if not name:
        return val
    rv = resolve_name(name)
    return rv if rv is not None else f'@{name}'

# ---------- 2. xmltree ----------
SCREENS = ['pref_article_controls', 'pref_backup', 'pref_cache', 'pref_look_and_feel',
           'pref_mark_read', 'pref_menu', 'pref_notification', 'pref_other',
           'pref_quick_actions', 'pref_reading', 'pref_rich_list', 'pref_text_to_speech']

def parse_xmltree(path):
    nodes = []
    stack = []
    with open(path, encoding='utf-8', errors='replace') as fh:
        for line in fh:
            stripped = line.rstrip('\n')
            ind = len(stripped) - len(stripped.lstrip(' '))
            s = stripped.strip()
            if s.startswith('E: '):
                name = s[3:].split(' (line=')[0].strip()
                # pop deeper/equal levels
                while stack and stack[-1][0] >= ind:
                    stack.pop()
                node = {'type': name, 'attrs': {}, 'depth': ind,
                        'context': [n['type'] for _, n in stack if n['type'] not in ('PreferenceScreen',) or True][-2:]}
                stack.append((ind, node))
                nodes.append(node)
            elif s.startswith('A: ') and stack:
                m = re.match(r'^A: .*?:([A-Za-z]+)\(0x[0-9a-f]+\)=(.*)$', s)
                if m:
                    attr, val = m.group(1), m.group(2)
                    rm = re.match(r'^(.*?) \(Raw: (.*)\)$', val)
                    if rm:
                        val = rm.group(1)
                    if val.startswith('"') and val.endswith('"'):
                        val = val[1:-1].replace('\\"', '"')
                    stack[-1][1]['attrs'][attr] = val
    return nodes

PREF_TYPES = {'Preference', 'CheckBoxPreference', 'SwitchPreference', 'SwitchPreferenceCompat',
              'ListPreference', 'EditTextPreference', 'RingtonePreference', 'MultiSelectListPreference',
              'SeekBarPreference', 'PreferenceCategory', 'PreferenceScreen'}

inventory = {}
for scr in SCREENS:
    path = os.path.join(BASE, 'xmltree', scr + '.txt')
    if not os.path.exists(path):
        continue
    nodes = parse_xmltree(path)
    prefs = []
    for n in nodes:
        a = n['attrs']
        key = resolve_ref(a.get('key'))
        if not key:
            continue
        entry = {
            'key': key,
            'widget': n['type'],
            'title': resolve_ref(a.get('title')),
            'summary': resolve_ref(a.get('summary')),
            'default': resolve_ref(a.get('defaultValue')),
            'dependency': resolve_ref(a.get('dependency')),
        }
        if 'entries' in a:
            entry['entries'] = resolve_ref(a.get('entries'))
        if 'entryValues' in a:
            entry['entryValues'] = resolve_ref(a.get('entryValues'))
        if 'ringtoneType' in a:
            entry['ringtoneType'] = a.get('ringtoneType')
        prefs.append(entry)
    inventory[scr] = prefs

with open(os.path.join(BASE, 'settings-inventory.json'), 'w', encoding='utf-8') as fh:
    json.dump(inventory, fh, ensure_ascii=False, indent=1)

# ---------- 3. markdown ----------
lines = []
for scr in SCREENS:
    prefs = inventory.get(scr)
    if not prefs:
        continue
    lines.append(f'\n### {scr}.xml\n')
    lines.append('| key | widget | default | opções (values) | título |')
    lines.append('| --- | --- | --- | --- | --- |')
    for p in prefs:
        if p['widget'] in ('PreferenceScreen', 'PreferenceCategory'):
            continue
        d = p.get('default')
        if isinstance(d, list):
            d = ', '.join(str(x) for x in d)
        ev = p.get('entryValues')
        if isinstance(ev, list):
            ev = ', '.join(str(x) for x in ev)
        t = p.get('title') or ''
        if isinstance(t, str):
            t = t.replace('|', '/')
        lines.append(f"| `{p['key']}` | {p['widget'].replace('Preference','')} | {d if d is not None else ''} | {ev if ev else ''} | {t} |")

out = os.path.join(BASE, 'settings-tables.md')
with open(out, 'w', encoding='utf-8') as fh:
    fh.write('# Inventário de preferências (gerado)\n' + '\n'.join(lines) + '\n')

print('\n'.join(lines))
print(f'\n[saved] {out}')
print(f'[saved] {os.path.join(BASE, "settings-inventory.json")}')
print('total prefs:', sum(len(v) for v in inventory.values()))
