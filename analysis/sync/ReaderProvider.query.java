    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String str3;
        String strSubstring;
        String[] strArr3;
        int iIndexOf;
        SQLiteDatabase readableDatabase = this.e.getReadableDatabase();
        if (readableDatabase == null) {
            return null;
        }
        int iMatch = u.match(uri);
        if (iMatch == 5) {
            Cursor cursorRawQuery = readableDatabase.rawQuery(str, strArr2);
            cursorRawQuery.setNotificationUri(getContext().getContentResolver(), uri);
            return cursorRawQuery;
        }
        if (str2 == null || (iIndexOf = str2.indexOf(" limit ")) == -1) {
            str3 = str2;
            strSubstring = null;
        } else {
            strSubstring = str2.substring(iIndexOf + 7);
            String strSubstring2 = str2.substring(0, iIndexOf);
            str3 = strSubstring2.length() == 0 ? null : strSubstring2;
        }
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        if (iMatch == 1) {
            try {
                readableDatabase.beginTransaction();
            } catch (Exception e) {
                n56.X1(getContext(), e.getLocalizedMessage() + ": Check your storage.");
            }
            return null;
        }
        if (iMatch == 2) {
            readableDatabase.setTransactionSuccessful();
            return null;
        }
        if (iMatch == 3) {
            try {
                readableDatabase.endTransaction();
            } catch (Exception e2) {
                n56.X1(getContext(), e2.getLocalizedMessage() + ": Check your storage.");
            }
            return null;
        }
        if (iMatch == 4) {
            this.e.close();
            this.e = new a(getContext());
            return null;
        }
        if (iMatch == 10) {
            strArr3 = strArr == null ? ip6.B : strArr;
            sQLiteQueryBuilder.setTables("subscription");
            sQLiteQueryBuilder.appendWhere("_id = " + uri.getPathSegments().get(1));
        } else if (iMatch != 11) {
            if (iMatch == 20) {
                sQLiteQueryBuilder.setTables("tag");
                sQLiteQueryBuilder.appendWhere("_id = " + uri.getPathSegments().get(1));
            } else if (iMatch == 21) {
                sQLiteQueryBuilder.setTables("tag");
            } else if (iMatch == 50) {
                sQLiteQueryBuilder.setTables("tag2item");
                sQLiteQueryBuilder.appendWhere("_id = " + uri.getPathSegments().get(1));
            } else if (iMatch != 51) {
                if (iMatch == 60) {
                    sQLiteQueryBuilder.setTables("history");
                    sQLiteQueryBuilder.appendWhere("_id = " + uri.getPathSegments().get(1));
                } else if (iMatch != 61) {
                    switch (iMatch) {
                        case 30:
                            sQLiteQueryBuilder.setTables("tag2sub");
                            sQLiteQueryBuilder.appendWhere("_id = " + uri.getPathSegments().get(1));
                            break;
                        case 31:
                            sQLiteQueryBuilder.setTables("tag2sub");
                            break;
                        case 32:
                            strArr3 = strArr == null ? kp6.g : strArr;
                            sQLiteQueryBuilder.setTables("(SELECT tag._id AS _id, uid, sort, 0 AS sort2, label AS title, null AS feed_url, null AS html_url, null AS icon_url, unread_count, hidden, 0 AS newest_item_time, sync_time, sync_excluded, type FROM tag UNION SELECT subscription._id, uid, sort, sort2, title, feed_url, html_url, icon_url, unread_count, hidden, newest_item_time, subscription.sync_time, sync_excluded, 100 FROM subscription LEFT JOIN tag2sub ON subscription._id = tag2sub.sub_id WHERE tag2sub.sub_id IS NULL)");
                            break;
                        default:
                            switch (iMatch) {
                                case 40:
                                    sQLiteQueryBuilder.setTables(NSRSS20.ITEM);
                                    sQLiteQueryBuilder.appendWhere("_id = " + uri.getPathSegments().get(1));
                                    break;
                                case 41:
                                    if (str != null && str.contains("tag2item.")) {
                                        strArr3 = strArr == null ? ap6.D : strArr;
                                        sQLiteQueryBuilder.setTables("item, tag2item");
                                    } else if (str != null && str.contains("subscription.")) {
                                        String[] strArr4 = strArr == null ? ap6.D : strArr;
                                        sQLiteQueryBuilder.setTables("item LEFT JOIN subscription ON item.sub_id = subscription._id");
                                        strArr3 = strArr4;
                                    } else {
                                        strArr3 = strArr == null ? ap6.C : strArr;
                                        sQLiteQueryBuilder.setTables(NSRSS20.ITEM);
                                    }
                                    break;
                                case 42:
                                    if (str == null || !str.contains("tag2item.")) {
                                        sQLiteQueryBuilder.setTables("item LEFT JOIN subscription ON item.sub_id = subscription._id");
                                    } else {
                                        sQLiteQueryBuilder.setTables("tag2item LEFT JOIN item ON tag2item.item_uid = item.uid LEFT JOIN subscription ON item.sub_id = subscription._id");
                                    }
                                    if (strArr == null) {
                                        strArr3 = ap6.H;
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException("Unknown URI " + uri);
                            }
                            break;
                    }
                } else {
                    strArr3 = strArr == null ? zo6.i : strArr;
                    sQLiteQueryBuilder.setTables("history");
                }
            } else if (str != null && str.contains("tag.")) {
                sQLiteQueryBuilder.setTables("tag2item, tag");
            } else if (str == null || !str.contains("item.")) {
                sQLiteQueryBuilder.setTables("tag2item");
            } else {
                sQLiteQueryBuilder.setTables("tag2item, item");
            }
            strArr3 = strArr;
        } else if (str != null && str.contains("tag2sub.")) {
            strArr3 = strArr == null ? ip6.C : strArr;
            sQLiteQueryBuilder.setTables("subscription LEFT JOIN tag2sub ON subscription._id = tag2sub.sub_id");
        } else if (str == null || !str.contains("tag.")) {
            strArr3 = strArr == null ? ip6.B : strArr;
            sQLiteQueryBuilder.setTables("subscription");
        } else {
            strArr3 = strArr == null ? ip6.C : strArr;
            sQLiteQueryBuilder.setTables("subscription LEFT JOIN tag2sub ON subscription._id = tag2sub.sub_id LEFT JOIN tag ON tag.uid = tag2sub.tag_uid");
        }
        Cursor cursorQuery = sQLiteQueryBuilder.query(readableDatabase, strArr3, str, strArr2, null, null, str3, strSubstring);
        cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
        return cursorQuery;
    }