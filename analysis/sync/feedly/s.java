    public static String s(String str) {
        return str == null ? "" : str.replace("&amp;", "&").replace("&#39;", "'").replace("&quot;", "\"");
    }