    public final Reader q(long j, rn6.b bVar, String str) throws gw6, IOException {
        StringBuilder sb = new StringBuilder("https://feedly.com/v3/streams/-/contents".replace("-", n56.u0(f(bVar.stream()))));
        sb.append("?ranked=");
        sb.append(bVar.e() ? "newest" : "oldest");
        if (bVar.d()) {
            sb.append("&unreadOnly=true");
        } else {
            sb.append("&unreadOnly=false");
        }
        if (str != null && str.length() > 0) {
            sb.append("&continuation=");
            sb.append(str);
        }
        if (j > 0) {
            sb.append("&newerThan=");
            sb.append(j * 1000);
        }
        int iC = bVar.c();
        if (iC > 1000 || iC == 0) {
            iC = 1000;
        }
        if (iC > 0) {
            sb.append("&count=");
            sb.append(iC <= 1000 ? iC : 1000);
        }
        return c(sb.toString());
    }