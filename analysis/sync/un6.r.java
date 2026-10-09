    public final Reader r(rn6.a aVar, String str) throws gw6, IOException {
        h();
        StringBuilder sb = new StringBuilder();
        sb.append(f("/reader/api/0/stream/items/ids"));
        sb.append("&output=json");
        String[] strArrF = aVar.f();
        if (strArrF == null || strArrF.length == 0) {
            sb.append("&s=");
            sb.append(n56.u0("user/-/state/com.google/reading-list"));
        } else {
            for (String str2 : strArrF) {
                if (str2.equals(rn6.STATE_STARRED)) {
                    str2 = "user/-/state/com.google/starred";
                }
                sb.append("&s=");
                sb.append(n56.u0(str2));
            }
            if (strArrF.length > 1) {
                sb.append("&merge=true&includeAllDirectStreamIds=true");
            }
        }
        if (aVar.d()) {
            sb.append("&xt=");
            sb.append("user/-/state/com.google/read");
        }
        if (str != null && str.length() > 0) {
            sb.append("&c=");
            sb.append(str);
        }
        long jB = aVar.b();
        if (jB > 0) {
            sb.append("&ot=");
            sb.append(jB);
        }
        sb.append("&r=");
        sb.append(aVar.e() ? "n" : "o");
        int iC = aVar.c();
        if (iC > 0) {
            sb.append("&n=");
            sb.append(iC);
        }
        return b(sb.toString());
    }