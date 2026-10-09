    public final Reader q(long j, long j2, rn6.b bVar, String str) throws gw6, IOException {
        h();
        StringBuilder sb = new StringBuilder();
        sb.append(f("/reader/api/0/stream/contents"));
        sb.append("&output=json&ck=");
        sb.append(j);
        String strStream = bVar.stream();
        if (strStream == null) {
            strStream = "user/-/state/com.google/reading-list";
        } else if (strStream.equals(rn6.STATE_STARRED)) {
            strStream = "user/-/state/com.google/starred";
        }
        sb.append("&s=");
        sb.append(n56.u0(strStream));
        if (bVar.d()) {
            sb.append("&xt=");
            sb.append("user/-/state/com.google/read");
        }
        if (str != null && str.length() > 0) {
            sb.append("&c=");
            sb.append(str);
        }
        if (j2 > 0) {
            sb.append("&ot=");
            sb.append(j2);
        }
        sb.append("&r=");
        sb.append(bVar.e() ? "n" : "o");
        int iC = bVar.c();
        int iE = e();
        if (iC > iE || iC == 0) {
            iC = iE;
        }
        if (iC > 0) {
            sb.append("&n=");
            sb.append(Math.min(iC, iE));
        }
        return b(sb.toString());
    }