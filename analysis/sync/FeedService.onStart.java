    @Override // android.app.Service
    public void onStart(Intent intent, int i) {
        if (intent == null) {
            j();
            stopSelf();
            return;
        }
        String stringExtra = intent.getStringExtra("item_ids");
        if (stringExtra != null) {
            f(stringExtra, intent.getBooleanExtra("save_page", false));
            Context applicationContext = getApplicationContext();
            if (this.g == null) {
                this.g = fw6.a(applicationContext);
            }
            fw6 fw6Var = this.g;
            String strF = iw6.F(applicationContext, "offline_full_content_format");
            int i2 = (strF == null || strF.length() == 0) ? 3 : Integer.parseInt(strF);
            this.o = i2;
            if (!fw6Var.a) {
                i2 = 0;
            }
            this.n = i2;
            int i3 = cn6.b;
            String strF2 = iw6.F(applicationContext, "item_user_agent");
            this.p = strF2 != null && strF2.equals(String.valueOf(1));
            this.q = iw6.f(applicationContext, "offline_save_data_traffic", true);
            String strC = iw6.C(applicationContext);
            this.r = strC;
            if (strC == null) {
                this.r = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            l(0);
            int i4 = this.l;
            if (i4 > 0) {
                if (i4 == 1) {
                    i(0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    return;
                }
                if (i4 == 2) {
                    i(0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    i(1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                } else {
                    i(0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    i(1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    i(2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                }
            }
        }
    }