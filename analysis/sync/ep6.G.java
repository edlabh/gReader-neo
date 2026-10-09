    public void G(long j) {
        String strF = iw6.F(this.a, "cache_auto_clean_read");
        int i = (strF == null || strF.length() == 0) ? 1 : Integer.parseInt(strF);
        String strF2 = iw6.F(this.a, "cache_auto_clean_unread");
        int i2 = (strF2 == null || strF2.length() == 0) ? -1 : Integer.parseInt(strF2);
        if (i >= 0 || i2 >= 0) {
            ContentResolver contentResolver = this.a.getContentResolver();
            contentResolver.query(ReaderProvider.h, null, null, null, null);
            try {
                long jCurrentTimeMillis = System.currentTimeMillis();
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                if (i >= 0) {
                    long j2 = jCurrentTimeMillis - (((long) i) * DateUtils.MILLIS_PER_DAY);
                    sb.append("(item.read = 1 AND ((item.read_time <> 0 AND item.read_time < ");
                    sb.append(j2);
                    sb.append(") OR (item.read_time = 0 AND item.sync_time < ");
                    sb.append(j2);
                    sb.append(")))");
                }
                if (i2 > 0) {
                    long j3 = jCurrentTimeMillis - (((long) i2) * DateUtils.MILLIS_PER_DAY);
                    sb.append(i >= 0 ? " OR " : "");
                    sb.append("(item.read = 0 AND item.sync_time < ");
                    sb.append(j3);
                    sb.append(")");
                }
                sb.append(")");
                if (iw6.f(this.a, "cache_keep_starred", true)) {
                    sb.append(" AND item.starred = 0");
                }
                if (iw6.f(this.a, "cache_keep_cached", false)) {
                    sb.append(" AND item.cached = 0");
                }
                if (n() == 7) {
                    sb.append(" AND item.sync_time < ");
                    sb.append(j);
                }
                if (contentResolver.delete(ap6.B, sb.toString(), null) > 0) {
                    contentResolver.delete(ReaderProvider.r, null, null);
                    Y();
                }
                contentResolver.query(ReaderProvider.i, null, null, null, null);
            } finally {
                contentResolver.query(ReaderProvider.j, null, null, null, null);
            }
        }
    }