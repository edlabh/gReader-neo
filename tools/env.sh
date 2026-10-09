#!/usr/bin/env bash
# Uso: source tools/env.sh
# Exporta o engine JADX do provider Android do REA e o caminho do APK alvo.
_project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export REA_JADX_MCP_JAR="$_project_root/tools/jadx-headless-mcp-0.7.1-all.jar"
export GREADER_APK="$_project_root/com.noinnion.android.greader.reader_5.2.2-424-2100522_minAPI21(arm64-v8a,armeabi,armeabi-v7a,mips,mips64,x86,x86_64)(nodpi)_apkmirror.com.apk"
unset _project_root
