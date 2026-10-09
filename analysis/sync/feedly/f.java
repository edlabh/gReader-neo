    public String f(String str) throws gw6, IOException {
        if (str == null || str.equals("user/-/category/global.all")) {
            return "user/-/category/global.all".replace("-", g(this.mContext));
        }
        if (str.equals(rn6.STATE_STARRED)) {
            return "user/-/tag/global.saved".replace("-", g(this.mContext));
        }
        return str.equals("user/-/tag/global.read") ? "user/-/tag/global.read".replace("-", g(this.mContext)) : str;
    }