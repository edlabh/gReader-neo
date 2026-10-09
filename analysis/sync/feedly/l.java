    public final boolean l(String[] strArr) throws gw6, IOException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("action", "keepUnread");
            jSONObject.put(NSRSS20.ENC_TYPE, "entries");
            JSONArray jSONArray = new JSONArray();
            for (String str : strArr) {
                jSONArray.put(str);
            }
            jSONObject.put("entryIds", jSONArray);
            e(3, "https://feedly.com/v3/markers", jSONObject.toString());
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }