    public boolean a(String str) throws gw6, IOException {
        i();
        Request.Builder builderUrl = new Request.Builder().url(str);
        StringBuilder sbZ = jo.z("OAuth ");
        sbZ.append(this.a);
        Response responseExecute = this.mClient.newCall(builderUrl.header("Authorization", sbZ.toString()).delete().build()).execute();
        int iCode = responseExecute.code();
        if (iCode == 403) {
            throw new gw6.b("Login failure");
        }
        if (iCode == 401) {
            throw new gw6.b("Authentication fails (" + iCode + "): " + str);
        }
        if (iCode == 200) {
            ResponseBody responseBodyBody = responseExecute.body();
            if (responseBodyBody == null) {
                throw new gw6("null response body");
            }
            responseBodyBody.close();
            return true;
        }
        throw new gw6("Invalid http status " + iCode + ": " + str);
    }