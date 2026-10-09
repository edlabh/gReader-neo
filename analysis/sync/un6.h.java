    public final String h() throws gw6, IOException {
        Context context = this.mContext;
        int i = cn6.b;
        this.c = iw6.F(context, "google_auth");
        long jA = iw6.A(this.mContext, "google_auth_time", 0L);
        if (this.c != null && System.currentTimeMillis() - jA < 259200000) {
            return this.c;
        }
        if (this.a == null || this.b == null) {
            throw new gw6.b("no login data");
        }
        BufferedReader bufferedReader = new BufferedReader(c(f("/accounts/ClientLogin"), new FormBody.Builder().add("accountType", "GOOGLE").add("Email", this.a).add("Passwd", this.b).add("service", "reader").build()));
        while (true) {
            try {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.indexOf("Auth=") == 0) {
                        this.c = line.substring(5);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Throwable th) {
                bufferedReader.close();
                throw th;
            }
        }
        bufferedReader.close();
        String str = this.c;
        if (str == null) {
            throw new gw6("Login Failure");
        }
        cn6.f0(this.mContext, str);
        return this.c;
    }