    public final void a() {
        Context applicationContext = getApplicationContext();
        CharSequence text = applicationContext.getText(R.string.app_name);
        CharSequence text2 = applicationContext.getText(R.string.download_downloading_files);
        long jCurrentTimeMillis = System.currentTimeMillis();
        Intent intent = new Intent(applicationContext, (Class<?>) DownloadActivity.class);
        intent.putExtra("startDownloading", true);
        PendingIntent activity = PendingIntent.getActivity(applicationContext, 0, intent, 0);
        m7 m7Var = new m7(applicationContext, vx6.a(applicationContext, "download_media_v2", "Download Media"));
        m7Var.e(text);
        m7Var.d(text2);
        m7Var.j(text2);
        Notification notification = m7Var.s;
        notification.icon = android.R.drawable.stat_sys_download_done;
        notification.when = jCurrentTimeMillis;
        m7Var.f(16, true);
        m7Var.f = activity;
        startForeground(R.id.notification_download_media, m7Var.b());
    }