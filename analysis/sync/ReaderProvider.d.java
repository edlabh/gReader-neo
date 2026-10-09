    /* JADX WARN: Code duplicated, block: B:94:0x02d7  */
    /* JADX WARN: Code duplicated, block: B:95:0x02dc  */
    public final int d(Uri uri, ContentValues contentValues, String str, String[] strArr, boolean z) {
        String asString;
        SQLiteDatabase writableDatabase = this.e.getWritableDatabase();
        int iMatch = u.match(uri);
        String str2 = "history";
        if (iMatch != 10) {
            if (iMatch != 11) {
                if (iMatch == 20) {
                    uri.getPathSegments().get(1);
                    str = b(str);
                    strArr = c(uri.getPathSegments().get(1), strArr);
                } else if (iMatch != 21) {
                    if (iMatch == 30) {
                        uri.getPathSegments().get(1);
                        str = b(str);
                        strArr = c(uri.getPathSegments().get(1), strArr);
                    } else if (iMatch != 31) {
                        if (iMatch == 40) {
                            uri.getPathSegments().get(1);
                            str = b(str);
                            strArr = c(uri.getPathSegments().get(1), strArr);
                        } else if (iMatch != 41) {
                            if (iMatch != 45) {
                                if (iMatch == 50) {
                                    uri.getPathSegments().get(1);
                                    str = b(str);
                                    strArr = c(uri.getPathSegments().get(1), strArr);
                                } else if (iMatch != 51) {
                                    if (iMatch == 60) {
                                        uri.getPathSegments().get(1);
                                        str = b(str);
                                        strArr = c(uri.getPathSegments().get(1), strArr);
                                    } else if (iMatch != 61) {
                                        switch (iMatch) {
                                            case 70:
                                                asString = contentValues != null ? contentValues.getAsString("subIds") : null;
                                                StringBuilder sbZ = jo.z("UPDATE subscription SET newest_item_time =  (SELECT CASE WHEN t2.sub_id IS NULL THEN 0 ELSE t2.newest_time END FROM subscription t1 LEFT JOIN (SELECT item.sub_id AS sub_id, MAX(item.updated_time) AS newest_time FROM item GROUP BY item.sub_id ORDER BY item.updated_time DESC) t2 ON t1._id = t2.sub_id  WHERE t1._id = subscription._id)");
                                                if (!TextUtils.isEmpty(asString)) {
                                                    jo.H(sbZ, " WHERE subscription._id IN (", asString, ")");
                                                }
                                                writableDatabase.execSQL(sbZ.toString());
                                                return 0;
                                            case 71:
                                                asString = contentValues != null ? contentValues.getAsString("subIds") : null;
                                                String[] strArr2 = new String[3];
                                                StringBuilder sbZ2 = jo.z("UPDATE subscription SET unread_count =  (SELECT CASE WHEN t2.sub_id IS NULL THEN 0 ELSE t2.count END FROM subscription t1 LEFT JOIN (SELECT item.sub_id AS sub_id, COUNT(item._id) AS count FROM item WHERE item.read = 0 GROUP BY item.sub_id) t2 ON t1._id = t2.sub_id  WHERE t1._id = subscription._id)");
                                                sbZ2.append(TextUtils.isEmpty(asString) ? "" : jo.o("WHERE subscription._id IN (", asString, ")"));
                                                strArr2[0] = sbZ2.toString();
                                                strArr2[1] = "UPDATE tag SET unread_count = (SELECT CASE WHEN s.unread_count IS NULL THEN 0 ELSE SUM(s.unread_count) END FROM subscription s, tag2sub t2s WHERE s._id = t2s.sub_id AND tag.uid = t2s.tag_uid) WHERE tag.type = 11";
                                                strArr2[2] = "UPDATE tag SET unread_count = (SELECT CASE WHEN i._id IS NULL THEN 0 ELSE COUNT(i._id) END FROM item i, tag2item t2i WHERE i.uid = t2i.item_uid AND tag.uid = t2i.tag_uid AND t2i.action <> -1 AND i.read = 0) WHERE tag.type <> 11";
                                                for (int i2 = 0; i2 < 3; i2++) {
                                                    writableDatabase.execSQL(strArr2[i2]);
                                                }
                                                return 0;
                                            case 72:
                                                if (contentValues == null) {
                                                    return 0;
                                                }
                                                String asString2 = contentValues.getAsString("ids");
                                                String[] strArr3 = {jo.o("UPDATE item SET read = 1 WHERE item.read = 0 AND item.keep_unread = 0 AND item.uid NOT IN ( ", asString2, " )"), jo.o("UPDATE item SET read = 0 WHERE item.read = 1 AND item.read_time < item.sync_time AND item.uid IN ( ", asString2, " )")};
                                                for (int i3 = 0; i3 < 2; i3++) {
                                                    writableDatabase.execSQL(strArr3[i3]);
                                                }
                                                return 0;
                                            case 73:
                                                String asString3 = contentValues.getAsString("tag_uid");
                                                long jLongValue = contentValues.getAsLong("sync_time").longValue();
                                                boolean zBooleanValue = contentValues.getAsBoolean("all").booleanValue();
                                                Uri uri2 = lp6.n;
                                                ArrayList arrayList = new ArrayList();
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("UPDATE item SET read = 1, read_time = ");
                                                sb.append(jLongValue);
                                                sb.append(", sync_time = ");
                                                sb.append(jLongValue);
                                                sb.append(" WHERE _id IN ( SELECT item._id FROM item LEFT JOIN tag2sub ON item.sub_id = tag2sub.sub_id WHERE item.read = 0 AND item.keep_unread = 0 AND tag2sub.tag_uid = \"");
                                                sb.append(asString3);
                                                sb.append("\")");
                                                if (!zBooleanValue) {
                                                    sb.append(" AND ");
                                                    sb.append("updated_time");
                                                    sb.append(" < ");
                                                    sb.append(String.valueOf(jLongValue / 1000));
                                                }
                                                arrayList.add(sb.toString());
                                                sb.setLength(0);
                                                sb.append("UPDATE item SET read = 1, read_time = ");
                                                sb.append(jLongValue);
                                                sb.append(", sync_time = ");
                                                sb.append(jLongValue);
                                                sb.append(" WHERE read = 0 AND keep_unread = 0 AND uid IN ( SELECT tag2item.item_uid FROM tag2item WHERE tag2item.tag_uid = \"");
                                                sb.append(asString3);
                                                sb.append("\")");
                                                if (!zBooleanValue) {
                                                    sb.append(" AND ");
                                                    sb.append("updated_time");
                                                    sb.append(" < ");
                                                    sb.append(String.valueOf(jLongValue / 1000));
                                                }
                                                arrayList.add(sb.toString());
                                                for (String str3 : (String[]) arrayList.toArray(new String[arrayList.size()])) {
                                                    writableDatabase.execSQL(str3);
                                                }
                                                return 0;
                                            case 74:
                                                if (contentValues == null) {
                                                    return 0;
                                                }
                                                String asString4 = contentValues.getAsString("ids");
                                                String[] strArr4 = {jo.o("UPDATE item SET starred = 0 WHERE item.starred = 1 AND item.uid NOT IN ( ", asString4, " )"), jo.o("UPDATE item SET starred = 1 WHERE item.starred = 0 AND item.uid IN ( ", asString4, " )"), "DELETE FROM tag2item WHERE tag2item.tag_uid = '" + contentValues.getAsString("tag_uid") + "' AND tag2item.item_uid NOT IN ( " + asString4 + " ) "};
                                                for (int i4 = 0; i4 < 3; i4++) {
                                                    writableDatabase.execSQL(strArr4[i4]);
                                                }
                                                return 0;
                                            case 75:
                                                asString = contentValues != null ? contentValues.getAsString("ids") : null;
                                                Uri uri3 = ap6.B;
                                                writableDatabase.execSQL("UPDATE item SET sync_time = read_time WHERE _id IN (" + asString + ")");
                                                return 0;
                                            case 76:
                                                writableDatabase.execSQL("DELETE FROM tag2item WHERE item_uid IN ( SELECT tag2item.item_uid FROM tag2item LEFT OUTER JOIN item ON tag2item.item_uid = item.uid WHERE item._id IS NULL )");
                                                return 0;
                                            default:
                                                throw new IllegalArgumentException("Unknown URI " + uri);
                                        }
                                    }
                                }
                                str2 = "tag2item";
                            } else {
                                str2 = "item_sync";
                            }
                        }
                        str2 = NSRSS20.ITEM;
                    }
                    str2 = "tag2sub";
                }
                str2 = "tag";
            }
            return z ? writableDatabase.update(str2, contentValues, str, strArr) : writableDatabase.delete(str2, str, strArr);
        }
        uri.getPathSegments().get(1);
        str = b(str);
        strArr = c(uri.getPathSegments().get(1), strArr);
        str2 = "subscription";
        if (z) {
        }
    }