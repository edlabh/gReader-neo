#!/usr/bin/env python3
"""Extrai preferências construídas em código de SettingsActivity.b.onCreate.java."""
import json, re, os

BASE = os.path.dirname(os.path.abspath(__file__))

# ---------- resources (mesma lógica do build_inventory) ----------
id_to_name, name_to_value = {}, {}
cur_id = cur_name = cur_type = None
cur_vals, array_buf = [], None

def flush():
    global cur_id, cur_name, cur_type, cur_vals
    if cur_name:
        id_to_name[cur_id] = f'{cur_type}/{cur_name}'
        name_to_value[f'{cur_type}/{cur_name}'] = cur_vals
    cur_id = cur_name = cur_type = None
    cur_vals = []

with open(os.path.join(BASE, 'resources.txt'), encoding='utf-8', errors='replace') as fh:
    for line in fh:
        m = re.match(r'\s*resource 0x([0-9a-f]+) ([a-z]+)/(\S+)\s*$', line)
        if m:
            flush(); cur_id, cur_type, cur_name = m.group(1), m.group(2), m.group(3); array_buf = None
            continue
        if cur_name is None: continue
        s = line.strip()
        if array_buf is not None:
            array_buf += ' ' + s
            if s.endswith(']'):
                items = [i.strip() for i in array_buf[array_buf.index('[')+1:array_buf.rindex(']')].split(',') if i.strip()]
                cur_vals.append([i[1:-1] if i.startswith('"') and i.endswith('"') else i for i in items]); array_buf = None
            continue
        if s.startswith('['):
            array_buf = s
            if s.endswith(']'):
                items = [i.strip() for i in s[1:-1].split(',') if i.strip()]
                cur_vals.append([i[1:-1] if i.startswith('"') and i.endswith('"') else i for i in items]); array_buf = None
            continue
        if s.startswith('() (array)') or s.startswith('() (plurals)'): continue
        m3 = re.match(r'^\(([^)]*)\) (.*)$', s)
        if m3:
            v = m3.group(2).strip()
            if v.startswith('(') and ') ' in v: v = v.split(') ', 1)[1].strip()
            if v.startswith('"') and v.endswith('"'):
                v = v[1:-1].replace('\\"', '"').replace('\\n', '\n')
            cur_vals.append(v)
flush()

def res_array(name):
    vals = name_to_value.get('array/' + name)
    if not vals: return None
    v = vals[0]
    if not isinstance(v, list): return None
    out = []
    for item in v:
        if item.startswith('@string/'):
            rv = name_to_value.get(item[1:])
            out.append(rv[0] if rv else item)
        else:
            out.append(item)
    return out

def res_string(name):
    vals = name_to_value.get('string/' + name)
    return vals[0] if vals else None

# ---------- parse código ----------
src = open(os.path.join(BASE, 'SettingsActivity.b.onCreate.java'), encoding='utf-8', errors='replace').read()

CONSTRUCTORS = r'new (ListPreference|CheckBoxPreference|EditTextPreference|SwitchPreference|MultiSelectListPreference|RingtonePreference|SeekBarPreference|PreferenceCategory|Preference)\(([^)]*)\)'

sections = []
for m in re.finditer(r'if\s*\(\s*"?([a-z_0-9]+)"?\.equals\(string\)\s*\)', src):
    sections.append((m.group(1), m.start()))
# a seção services é construída após o bloco negado `if (!"services"...)`
svc = src.find('preference_category_services')
if svc > 0:
    sections.append(('services', svc))
sections.sort(key=lambda x: x[1])
sections.append(('end', len(src)))

result = {}
for i in range(len(sections) - 1):
    sec, start = sections[i]
    end = sections[i + 1][1]
    body = src[start:end]
    # variáveis booleanas locais da seção (ex.: Boolean bool = Boolean.TRUE;)
    local_vars = {}
    for vm in re.finditer(r'(?:final )?(?:boolean|Boolean)\s+(\w+)\s*=\s*(.+?);', body):
        expr = vm.group(2).strip()
        if expr in ('Boolean.TRUE', 'true'):
            local_vars[vm.group(1)] = 'true'
        elif expr in ('Boolean.FALSE', 'false'):
            local_vars[vm.group(1)] = 'false'
        else:
            local_vars[vm.group(1)] = 'dinâmico'
    cons = list(re.finditer(CONSTRUCTORS, body))
    prefs = []
    for j, cm in enumerate(cons):
        cend = cons[j + 1].start() if j + 1 < len(cons) else len(body)
        chunk = body[cm.end():cend]
        entry = {'widget': cm.group(1)}
        mk = re.search(r'setKey\("([^"]+)"\)', chunk)
        if mk: entry['key'] = mk.group(1)
        md = re.search(r'setDefaultValue\((.+?)\);', chunk)
        if md:
            dv = md.group(1).strip()
            if dv in local_vars:
                dv = local_vars[dv]
            elif dv == 'Boolean.TRUE':
                dv = 'true'
            elif dv == 'Boolean.FALSE':
                dv = 'false'
            msv = re.match(r'String\.valueOf\((-?\d+)\)', dv)
            if msv:
                dv = msv.group(1)
            entry['default'] = dv.strip('"')
        me = re.search(r'setEntries\(R\.array\.(\w+)\)', chunk)
        if me:
            arr = res_array(me.group(1))
            if arr: entry['entries'] = arr
        mv = re.search(r'setEntryValues\(R\.array\.(\w+)\)', chunk)
        if mv:
            arr = res_array(mv.group(1))
            if arr: entry['entryValues'] = arr
        mt = re.search(r'setTitle\(R\.string\.(\w+)\)', chunk)
        if mt:
            st = res_string(mt.group(1))
            if st: entry['title'] = st
        ms = re.search(r'setSummary\(R\.string\.(\w+)\)', chunk)
        if ms:
            st = res_string(ms.group(1))
            if st: entry['summary'] = st
        mdep = re.search(r'setDependency\("([^"]+)"\)', chunk)
        if mdep: entry['dependency'] = mdep.group(1)
        if entry.get('key') or entry['widget'] == 'PreferenceCategory':
            prefs.append(entry)
    result[sec] = prefs

with open(os.path.join(BASE, 'code-settings-inventory.json'), 'w', encoding='utf-8') as fh:
    json.dump(result, fh, ensure_ascii=False, indent=1)

lines = ['\n## Telas construídas em código (`SettingsActivity.b`)\n']
for sec, prefs in result.items():
    if sec == 'end': continue
    lines.append(f'\n### seção `{sec}`\n')
    lines.append('| key | widget | default | opções (values) | título |')
    lines.append('| --- | --- | --- | --- | --- |')
    for p in prefs:
        if not p.get('key'): continue
        ev = p.get('entryValues'); ev = ', '.join(map(str, ev)) if isinstance(ev, list) else ''
        lines.append(f"| `{p['key']}` | {p['widget'].replace('Preference','')} | {p.get('default','')} | {ev} | {p.get('title','')} |")

with open(os.path.join(BASE, 'code-settings-tables.md'), 'w', encoding='utf-8') as fh:
    fh.write('\n'.join(lines) + '\n')

print('\n'.join(lines))
print('[saved] code-settings-inventory.json / code-settings-tables.md')
print('total code prefs:', sum(1 for sec, ps in result.items() for p in ps if p.get('key')))
