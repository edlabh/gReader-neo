    public final boolean j(String str, long j) throws gw6, IOException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("action", "markAsRead");
            jSONObject.put(NSRSS20.ENC_TYPE, "categories");
            jSONObject.put("asOf", j + 999);
            JSONArray jSONArray = new JSONArray();
            jSONArray.put(str);
            jSONObject.put("categoryIds", jSONArray);
            e(3, "https://feedly.com/v3/markers", jSONObject.toString());
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }