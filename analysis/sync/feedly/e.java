    public boolean e(int i, String str, String str2) throws gw6, IOException {
        if (TextUtils.isEmpty(str2)) {
            throw new gw6("no data content");
        }
        i();
        RequestBody requestBodyCreate = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), str2);
        Request.Builder builderUrl = new Request.Builder().url(str);
        StringBuilder sbZ = jo.z("OAuth ");
        sbZ.append(this.a);
        Request.Builder builderHeader = builderUrl.header("Authorization", sbZ.toString());
        if (i == 3) {
            builderHeader.post(requestBodyCreate);
        } else {
            if (i != 4) {
                throw new gw6("no http action");
            }
            builderHeader.put(requestBodyCreate);
        }
        Response responseExecute = this.mClient.newCall(builderHeader.build()).execute();
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