    public int e() {
        Cursor cursorQuery = this.a.getContentResolver().query(ap6.B, ap6.K, "read = 0", null, null);
        if (cursorQuery == null) {
            return 0;
        }
        try {
            return cursorQuery.moveToNext() ? cursorQuery.getInt(0) : 0;
        } finally {
            cursorQuery.close();
        }
    }