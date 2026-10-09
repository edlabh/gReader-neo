    public cw6 I(long j) throws gw6, IOException {
        String strSubstring;
        String strSubstring2;
        int i;
        long j2;
        long j3;
        boolean z;
        SparseIntArray sparseIntArray;
        Cursor cursorK = new vd(this.a, zo6.h, new String[]{"history._id", "history.type", "history.sync_time", "history.params"}, "history.done = 0 AND history.type != 4", null, "history.sync_time DESC").k();
        int i2 = 3;
        int i3 = 2;
        int i4 = 0;
        int i5 = 1;
        if (cursorK != null) {
            try {
                try {
                    rn6 rn6VarM = m();
                    while (cursorK.moveToNext()) {
                        long j4 = cursorK.getLong(i4);
                        int i6 = cursorK.getInt(i5);
                        long j5 = cursorK.getLong(i3);
                        String string = cursorK.getString(i2);
                        if (i6 == i5 || i6 == i2 || i6 == 2 || i6 == 5) {
                            int iIndexOf = string.indexOf("&t=");
                            if (iIndexOf > 0) {
                                strSubstring2 = string.substring(2, iIndexOf);
                                int iIndexOf2 = string.indexOf("&", iIndexOf + 1);
                                int i7 = iIndexOf + 3;
                                if (iIndexOf2 <= 0) {
                                    iIndexOf2 = string.length();
                                }
                                strSubstring = string.substring(i7, iIndexOf2);
                            } else {
                                strSubstring = null;
                                strSubstring2 = null;
                            }
                            if (i6 == 5) {
                                int iIndexOf3 = string.indexOf("&a=");
                                int iIndexOf4 = string.indexOf("&r=");
                                if (iIndexOf3 > 0) {
                                    i = i6;
                                    j2 = j4;
                                    rn6VarM.editSubscription(strSubstring2, strSubstring, strSubstring2, string.substring(iIndexOf3 + 3, string.length()).split(";"), 4);
                                } else {
                                    i = i6;
                                    j2 = j4;
                                    if (iIndexOf4 > 0) {
                                        rn6VarM.editSubscription(strSubstring2, strSubstring, strSubstring2, string.substring(iIndexOf3 + 3, string.length()).split(";"), 5);
                                    } else {
                                        rn6VarM.editSubscription(strSubstring2, strSubstring, strSubstring2, null, 1);
                                    }
                                }
                            } else {
                                i = i6;
                                j2 = j4;
                                rn6VarM.markAllAsRead(strSubstring2, strSubstring, j5);
                            }
                        } else {
                            if (i6 == 6) {
                                int iIndexOf5 = string.indexOf("&t=");
                                int iIndexOf6 = string.indexOf("&dest=");
                                if (iIndexOf5 > 0 && iIndexOf6 > iIndexOf5) {
                                    rn6VarM.renameTag(string.substring(2, iIndexOf5), string.substring(iIndexOf5 + 3, iIndexOf6), string.substring(iIndexOf6 + 6, string.length()));
                                }
                            } else if (i6 == 7) {
                                int iIndexOf7 = string.indexOf("&t=");
                                if (iIndexOf7 > 0) {
                                    rn6VarM.disableTag(string.substring(2, iIndexOf7), string.substring(iIndexOf7 + 3, string.length()));
                                }
                            } else {
                                if (i6 == 8) {
                                    ArrayList arrayList = new ArrayList();
                                    ArrayList arrayList2 = new ArrayList();
                                    String strSubstring3 = null;
                                    String strSubstring4 = null;
                                    while (string.length() > 0) {
                                        int iIndexOf8 = string.indexOf("&");
                                        String strSubstring5 = iIndexOf8 > 0 ? string.substring(0, iIndexOf8) : string;
                                        long j6 = j4;
                                        if (strSubstring5.startsWith("s=")) {
                                            strSubstring4 = strSubstring5.substring(2);
                                        } else if (strSubstring5.startsWith("i=")) {
                                            strSubstring3 = strSubstring5.substring(2);
                                        } else if (strSubstring5.startsWith("a=")) {
                                            arrayList.add(strSubstring5.substring(2));
                                        } else if (strSubstring5.startsWith("r=")) {
                                            arrayList2.add(strSubstring5.substring(2));
                                        }
                                        string = iIndexOf8 > 0 ? string.substring(iIndexOf8 + 1) : "";
                                        j4 = j6;
                                    }
                                    j3 = j4;
                                    if (arrayList2.size() > 0) {
                                        rn6VarM.editItemTag(new String[]{strSubstring3}, new String[]{strSubstring4}, (String[]) arrayList2.toArray(new String[arrayList2.size()]), 2);
                                    }
                                    if (arrayList.size() > 0) {
                                        rn6VarM.editItemTag(new String[]{strSubstring3}, new String[]{strSubstring4}, (String[]) arrayList.toArray(new String[arrayList.size()]), 1);
                                    }
                                }
                                i = i6;
                                j2 = j3;
                            }
                            j3 = j4;
                            i = i6;
                            j2 = j3;
                        }
                        f(j2);
                        if (i == 1) {
                            break;
                        }
                        i4 = 0;
                        i5 = 1;
                        i2 = 3;
                        i3 = 2;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                cursorK.close();
            } catch (Throwable th) {
                cursorK.close();
                throw th;
            }
        }
        S(j);
        ContentResolver contentResolver = this.a.getContentResolver();
        Cursor cursorK2 = new vd(this.a, jp6.e, new String[]{"item.uid", "tag2item.tag_uid", "tag2item.action", "tag2item._id", "item.sub_id"}, "item.uid = tag2item.item_uid AND tag2item.action <> 0", null, null).k();
        if (cursorK2 == null) {
            z = true;
        } else {
            contentResolver.query(ReaderProvider.h, null, null, null, null);
            while (cursorK2.moveToNext()) {
                try {
                    String string2 = cursorK2.getString(0);
                    String string3 = cursorK2.getString(1);
                    int i8 = cursorK2.getInt(2);
                    long j7 = cursorK2.getLong(3);
                    ip6 ip6VarC = ip6.c(this.a, cursorK2.getLong(4), false);
                    if (ip6VarC != null) {
                        V(j7, string2, ip6VarC.f, string3, i8 == 1, j);
                    }
                } catch (Throwable th2) {
                    cursorK2.close();
                    contentResolver.query(ReaderProvider.j, null, null, null, null);
                    throw th2;
                }
            }
            z = true;
            contentResolver.query(ReaderProvider.i, null, null, null, null);
            cursorK2.close();
            contentResolver.query(ReaderProvider.j, null, null, null, null);
        }
        M();
        cw6 cw6Var = new cw6();
        if (iw6.f(this.a, "sync_notifiable_custom", z)) {
            Cursor cursorK3 = new vd(this.a, ip6.A, ip6.D, "notification = 1", null, null).k();
            if (cursorK3 == null) {
                sparseIntArray = null;
            } else {
                sparseIntArray = new SparseIntArray();
                while (cursorK3.moveToNext()) {
                    try {
                        sparseIntArray.put(cursorK3.getInt(0), 0);
                    } catch (Throwable th3) {
                        cursorK3.close();
                        throw th3;
                    }
                }
                cursorK3.close();
            }
            cw6Var.b = sparseIntArray;
        }
        try {
            m().handleItemList(new fp6(this, iw6.I(this.a), j, cw6Var), j);
            this.a.getContentResolver().update(ReaderProvider.n, null, null, null);
            if (iw6.f(this.a, "sync_server_reads", z) && m().handleItemIdList(new dp6(this), j)) {
                n56.I1(this.a, z, z);
            }
            if (iw6.f(this.a, "sync_include_starred", false)) {
                cw6Var.a = W(rn6.STATE_STARRED, j, true, iw6.I(this.a)) + cw6Var.a;
            }
            return cw6Var;
        } catch (gw6.b e3) {
            throw new gw6.b(e3);
        }
    }