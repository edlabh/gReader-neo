    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        int iMatch = u.match(uri);
        if (iMatch == 40) {
            return "vnd.android.cursor.item/vnd.com.noinnion.android.greader.reader.provider.item";
        }
        if (iMatch == 41) {
            return "vnd.android.cursor.dir/vnd.com.noinnion.android.greader.reader.provider.item";
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }