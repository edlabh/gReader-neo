    public void d() {
        ContentResolver contentResolver = this.a.getContentResolver();
        contentResolver.query(ReaderProvider.h, null, null, null, null);
        try {
            contentResolver.delete(zo6.h, null, null);
            contentResolver.query(ReaderProvider.i, null, null, null, null);
        } finally {
            contentResolver.query(ReaderProvider.j, null, null, null, null);
        }
    }