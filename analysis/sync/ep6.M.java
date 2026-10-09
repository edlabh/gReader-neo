    public boolean M() throws gw6, IOException {
        ContentResolver contentResolver = this.a.getContentResolver();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("item.");
        sb.append("sync_time");
        sb.append(" < ");
        sb.append("item.");
        sb.append("read_time");
        jo.J(sb, " AND ", "item.", "read", " = 1");
        sb.append(" AND ");
        sb.append("subscription.");
        sb.append("uid");
        sb.append(" IS NOT NULL");
        StringBuilder sb2 = new StringBuilder();
        int i = 1;
        String string = null;
        int i2 = 1;
        while (i > 0) {
            arrayList.clear();
            arrayList2.clear();
            sb2.setLength(0);
            Cursor cursorQuery = contentResolver.query(ap6.B, ap6.G, new String(sb), null, " limit 250");
            int count = cursorQuery == null ? 0 : cursorQuery.getCount();
            if (count == 0) {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return false;
            }
            boolean z = true;
            while (cursorQuery.moveToNext()) {
                try {
                    try {
                        if (!z) {
                            sb2.append(",");
                        } else {
                            if (string != null && string.equals(cursorQuery.getString(i2))) {
                                cursorQuery.close();
                                return false;
                            }
                            string = cursorQuery.getString(i2);
                            z = false;
                        }
                        sb2.append(cursorQuery.getLong(0));
                        arrayList.add(cursorQuery.getString(i2));
                        arrayList2.add(cursorQuery.getString(2));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return false;
                    }
                } catch (Throwable th) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    throw th;
                }
            }
            cursorQuery.close();
            if (arrayList.size() == 0 || !m().markAsRead((String[]) arrayList.toArray(new String[arrayList.size()]), (String[]) arrayList2.toArray(new String[arrayList2.size()]))) {
                return false;
            }
            int i3 = count;
            contentResolver.query(ReaderProvider.h, null, null, null, null);
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("ids", sb2.toString());
                contentResolver.update(ReaderProvider.l, contentValues, null, null);
                contentResolver.query(ReaderProvider.i, null, null, null, null);
                if (i3 < 250) {
                    return true;
                }
                contentResolver.query(ReaderProvider.j, null, null, null, null);
                i2 = 1;
                i = i3;
            } finally {
                contentResolver.query(ReaderProvider.j, null, null, null, null);
            }
        }
        return true;
    }