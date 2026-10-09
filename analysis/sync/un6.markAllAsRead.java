    @Override // defpackage.rn6
    public boolean markAllAsRead(String str, String str2, long j) throws gw6, IOException {
        if (str == null) {
            str = "user/-/state/com.google/reading-list";
            str2 = "all";
        }
        Reader readerC = c(f("/reader/api/0/mark-all-as-read"), new FormBody.Builder().add("T", i()).add("s", str).add("t", str2).add("ts", j + "999").build());
        try {
            char[] cArr = new char[128];
            int i = readerC.read(cArr);
            if (i != -1) {
                return new String(cArr, 0, i).equals("OK");
            }
            return false;
        } finally {
            readerC.close();
        }
    }