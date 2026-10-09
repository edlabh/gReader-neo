    @Override // defpackage.rn6
    public void handleReaderList(rn6.d dVar, rn6.c cVar, rn6.e eVar, long j) throws gw6, IOException {
        h();
        StringBuilder sb = new StringBuilder();
        sb.append(f("/reader/api/0/tag/list"));
        sb.append("&output=json&ck=");
        sb.append(j);
        InputStreamReader inputStreamReader = new InputStreamReader(a(new String(sb)), "UTF-8");
        try {
            try {
                o(inputStreamReader, dVar);
                inputStreamReader.close();
                h();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(f("/reader/api/0/subscription/list"));
                sb2.append("&output=json&ck=");
                sb2.append(j);
                InputStreamReader inputStreamReader2 = new InputStreamReader(a(new String(sb2)), "UTF-8");
                try {
                    try {
                        n(inputStreamReader2, cVar);
                        inputStreamReader2.close();
                    } catch (zo e) {
                        throw new gw6("data parse error: parseSubList", e);
                    }
                } catch (Throwable th) {
                    inputStreamReader2.close();
                    throw th;
                }
            } catch (zo e2) {
                throw new gw6("data parse error: parseTagList", e2);
            }
        } catch (Throwable th2) {
            inputStreamReader.close();
            throw th2;
        }
    }