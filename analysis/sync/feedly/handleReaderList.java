    @Override // defpackage.rn6
    public void handleReaderList(rn6.d dVar, rn6.c cVar, rn6.e eVar, long j) throws gw6, IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(b("https://feedly.com/v3/subscriptions"), "UTF-8");
        try {
            try {
                o(inputStreamReader, cVar, dVar);
                inputStreamReader.close();
                InputStreamReader inputStreamReader2 = new InputStreamReader(b("https://feedly.com/v3/tags"), "UTF-8");
                try {
                    try {
                        p(inputStreamReader2, dVar);
                        inputStreamReader2.close();
                    } catch (zo e) {
                        e.printStackTrace();
                        throw new gw6("data parse error", e);
                    }
                } catch (Throwable th) {
                    inputStreamReader2.close();
                    throw th;
                }
            } catch (zo e2) {
                e2.printStackTrace();
                throw new gw6("data parse error", e2);
            }
        } catch (Throwable th2) {
            inputStreamReader.close();
            throw th2;
        }
    }