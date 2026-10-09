    @Override // defpackage.rn6
    public boolean handleItemIdList(rn6.a aVar, long j) throws gw6, IOException {
        String[] strArrF = aVar.f();
        String strF = null;
        if (strArrF == null || strArrF.length == 0) {
            strF = f(null);
        } else if (strArrF.length > 0) {
            strF = f(strArrF[0]);
        }
        StringBuilder sb = new StringBuilder("https://feedly.com/v3/streams/-/ids".replace("-", n56.u0(strF)));
        sb.append("?ranked=");
        sb.append(aVar.e() ? "newest" : "oldest");
        if (aVar.d()) {
            sb.append("&unreadOnly=true");
        } else {
            sb.append("&unreadOnly=false");
        }
        int iC = aVar.c();
        if (iC > 0) {
            sb.append("&count=");
            sb.append(iC);
        }
        Reader readerC = c(sb.toString());
        try {
            try {
                int iM = m(readerC, aVar);
                readerC.close();
                return iM > 0;
            } catch (zo e) {
                e.printStackTrace();
                throw new gw6("data parse error", e);
            }
        } catch (Throwable th) {
            readerC.close();
            throw th;
        }
    }