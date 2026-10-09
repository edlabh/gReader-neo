    public final void a() {
        File[] fileArrListFiles;
        File[] fileArrListFiles2;
        int length;
        if (((PowerManager) getSystemService("power")).isInteractive()) {
            return;
        }
        Context applicationContext = getApplicationContext();
        String strF = iw6.F(applicationContext, "cache_auto_cleanup_images");
        int i = (strF == null || strF.length() == 0) ? 1000 : Integer.parseInt(strF);
        if (i >= 0) {
            String strC = iw6.C(applicationContext);
            if (strC == null) {
                strC = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            File file = new File(jo.n(strC, "/gReader/.image-cache/"));
            if (file.exists() && (fileArrListFiles2 = file.listFiles()) != null && (length = fileArrListFiles2.length) > i) {
                mx6[] mx6VarArr = new mx6[fileArrListFiles2.length];
                for (int i2 = 0; i2 < fileArrListFiles2.length; i2++) {
                    mx6VarArr[i2] = new mx6(fileArrListFiles2[i2]);
                }
                Arrays.sort(mx6VarArr);
                int i3 = length - i;
                String[] strArr = new String[i3];
                String[] strArr2 = new String[i3];
                for (int i4 = 0; i4 < i3; i4++) {
                    strArr2[i4] = mx6VarArr[i4].f.getAbsolutePath();
                    strArr[i4] = mx6VarArr[i4].f.getName();
                }
                hy6.d(strArr2);
            }
        }
        Context applicationContext2 = getApplicationContext();
        String strC2 = iw6.C(applicationContext2);
        if (strC2 == null) {
            strC2 = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        File file2 = new File(jo.n(strC2, "/gReader/cache/"));
        if (!file2.exists() || (fileArrListFiles = file2.listFiles()) == null || fileArrListFiles.length == 0) {
            return;
        }
        int length2 = fileArrListFiles.length;
        HashSet hashSet = new HashSet();
        int count = -1;
        int i5 = 0;
        while (true) {
            if (count != -1 && count < 1000) {
                break;
            }
            ContentResolver contentResolver = applicationContext2.getContentResolver();
            Uri uri = ap6.B;
            String[] strArr3 = ap6.J;
            StringBuilder sbZ = jo.z("sync_time DESC limit ");
            sbZ.append(i5 * 1000);
            sbZ.append(", 1000");
            Cursor cursorQuery = contentResolver.query(uri, strArr3, "cached > 0", null, sbZ.toString());
            if (cursorQuery == null) {
                hashSet = null;
                break;
            }
            try {
                if (cursorQuery.moveToFirst()) {
                    do {
                        hashSet.add(String.valueOf(cursorQuery.getString(0).hashCode()));
                    } while (cursorQuery.moveToNext());
                }
                cursorQuery.close();
                count = cursorQuery.getCount();
                i5++;
            } catch (Throwable th) {
                cursorQuery.close();
                throw th;
            }
        }
        if (hashSet == null || hashSet.size() == 0) {
            n56.n0(applicationContext2);
            return;
        }
        if (length2 <= hashSet.size()) {
            return;
        }
        HashSet<String> hashSet2 = new HashSet();
        for (File file3 : fileArrListFiles) {
            hashSet2.add(file3.getName());
        }
        hashSet2.removeAll(hashSet);
        String absolutePath = file2.getAbsolutePath();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (String str : hashSet2) {
            arrayList2.add(absolutePath + "/" + str);
            arrayList.add(str);
        }
        if (arrayList2.size() > 0) {
            hy6.d((String[]) arrayList2.toArray(new String[arrayList2.size()]));
        }
    }