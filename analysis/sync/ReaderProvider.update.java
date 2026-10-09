    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return d(uri, contentValues, str, strArr, true);
    }