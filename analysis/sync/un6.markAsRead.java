    @Override // defpackage.rn6
    public boolean markAsRead(String[] strArr, String[] strArr2) throws gw6, IOException {
        return editItemTag(strArr, strArr2, new String[]{"user/-/state/com.google/read"}, 1);
    }