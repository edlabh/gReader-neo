    @Override // android.app.IntentService
    public void onHandleIntent(Intent intent) {
        Context applicationContext = getApplicationContext();
        CharSequence text = applicationContext.getText(R.string.app_name);
        CharSequence text2 = applicationContext.getText(R.string.msg_cleanup_running);
        long jCurrentTimeMillis = System.currentTimeMillis();
        m7 m7Var = new m7(applicationContext, vx6.a(applicationContext, "clean_service_v2", "Clean Service"));
        m7Var.e(text);
        m7Var.d(text2);
        m7Var.j(text2);
        Notification notification = m7Var.s;
        notification.icon = R.drawable.ic_notification_app;
        notification.when = jCurrentTimeMillis;
        m7Var.f(16, true);
        startForeground(R.id.notification_clean_up, m7Var.b());
        try {
            a();
            xo6.a(this);
            stopForeground(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            if (notificationManager != null) {
                notificationManager.cancel(R.id.notification_clean_up);
            }
        } catch (Throwable th) {
            xo6.a(this);
            throw th;
        }
    }