    @Override // defpackage.rn6
    public void handleItemList(rn6.b bVar, long j) throws gw6, IOException {
        long jB = bVar.b();
        Reader readerQ = q(jB, bVar, null);
        try {
            try {
                Set<String> setG = bVar.g();
                String strN = n(readerQ, bVar, setG);
                int iC = bVar.c();
                int i = 1000;
                if (iC <= 1000) {
                    i = iC;
                }
                int i2 = 1;
                while (true) {
                    if ((iC != 0 && iC <= i * i2) || !bVar.f() || TextUtils.isEmpty(strN)) {
                        break;
                    }
                    readerQ.close();
                    readerQ = q(jB, bVar, strN);
                    strN = n(readerQ, bVar, setG);
                    i2++;
                }
                readerQ.close();
            } catch (zo e) {
                e.printStackTrace();
                throw new gw6("data parse error", e);
            }
        } catch (Throwable th) {
            readerQ.close();
            throw th;
        }
    }