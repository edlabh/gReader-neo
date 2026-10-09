    public final String i() throws gw6, IOException {
        h();
        long jCurrentTimeMillis = System.currentTimeMillis();
        String str = this.d;
        if (str != null && jCurrentTimeMillis < this.e) {
            return str;
        }
        Reader readerB = b(f("/reader/api/0/token"));
        try {
            char[] cArr = new char[64];
            this.d = new String(cArr, 0, readerB.read(cArr));
            this.e = jCurrentTimeMillis + 900000;
            return this.d;
        } finally {
            readerB.close();
        }
    }