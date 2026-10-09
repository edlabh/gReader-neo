    @Override // defpackage.rn6
    public boolean editSubscription(String str, String str2, String str3, String[] strArr, int i) throws gw6, IOException {
        String strI = i();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("T", strI);
        if (str2 != null) {
            builder.add("t", str2);
        }
        if (i == 1) {
            builder.add("ac", "edit");
        } else if (i == 2) {
            builder.add("ac", "subscribe");
            str = RssReaderClient.USER_FEED + str3;
            if (strArr != null && strArr.length > 0 && strArr.length > 0) {
                builder.add("a", getCategoryUid(strArr[0]));
            }
        } else if (i == 3) {
            builder.add("ac", "unsubscribe");
        } else if (i == 4) {
            builder.add("ac", "edit");
            if (strArr.length > 0) {
                builder.add("a", strArr[0]);
            }
        } else if (i == 5) {
            builder.add("ac", "edit");
            if (strArr.length > 0) {
                builder.add("r", strArr[0]);
            }
        }
        if (str != null) {
            builder.add("s", str);
        }
        Reader readerC = c(f("/reader/api/0/subscription/edit"), builder.build());
        try {
            char[] cArr = new char[128];
            int i2 = readerC.read(cArr);
            if (i2 != -1) {
                return new String(cArr, 0, i2).equals("OK");
            }
            return false;
        } finally {
            readerC.close();
        }
    }