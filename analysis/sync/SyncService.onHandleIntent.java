    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r15v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v10 */
    /* JADX WARN: Type inference failed for: r15v2 */
    /* JADX WARN: Type inference failed for: r15v3 */
    /* JADX WARN: Type inference failed for: r15v4 */
    /* JADX WARN: Type inference failed for: r15v5 */
    /* JADX WARN: Type inference failed for: r15v7 */
    /* JADX WARN: Type inference failed for: r15v8 */
    /* JADX WARN: Type inference failed for: r15v9 */
    /* JADX WARN: Type inference failed for: r5v10 */
    /* JADX WARN: Type inference failed for: r5v11 */
    /* JADX WARN: Type inference failed for: r5v12 */
    /* JADX WARN: Type inference failed for: r5v13, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v14, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v15 */
    /* JADX WARN: Type inference failed for: r5v16, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v17, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v18 */
    /* JADX WARN: Type inference failed for: r5v19 */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v20 */
    /* JADX WARN: Type inference failed for: r5v21 */
    /* JADX WARN: Type inference failed for: r5v23 */
    /* JADX WARN: Type inference failed for: r5v24 */
    /* JADX WARN: Type inference failed for: r5v25, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v26 */
    /* JADX WARN: Type inference failed for: r5v27 */
    /* JADX WARN: Type inference failed for: r5v28 */
    /* JADX WARN: Type inference failed for: r5v29 */
    /* JADX WARN: Type inference failed for: r5v3, types: [lp6] */
    /* JADX WARN: Type inference failed for: r5v30 */
    /* JADX WARN: Type inference failed for: r5v34 */
    /* JADX WARN: Type inference failed for: r5v35 */
    /* JADX WARN: Type inference failed for: r5v36 */
    /* JADX WARN: Type inference failed for: r5v37 */
    /* JADX WARN: Type inference failed for: r5v38 */
    /* JADX WARN: Type inference failed for: r5v39 */
    /* JADX WARN: Type inference failed for: r5v4 */
    /* JADX WARN: Type inference failed for: r5v5 */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    /* JADX WARN: Type inference failed for: r5v8, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v9 */
    @Override // android.app.IntentService
    public void onHandleIntent(Intent intent) {
        ip6 ip6VarD;
        yd ydVarA;
        Intent intent2;
        String stringExtra;
        if (intent == null) {
            return;
        }
        Context applicationContext = getApplicationContext();
        CharSequence text = applicationContext.getText(R.string.title_synchronization);
        CharSequence text2 = applicationContext.getText(R.string.msg_sync_started);
        long jCurrentTimeMillis = System.currentTimeMillis();
        m7 m7Var = new m7(applicationContext, a());
        m7Var.e(text);
        m7Var.d(text2);
        m7Var.j(text2);
        Notification notification = m7Var.s;
        notification.icon = R.drawable.ic_notification_app;
        notification.when = jCurrentTimeMillis;
        int iW = 1;
        m7Var.f(16, true);
        startForeground(R.id.notification_sync_started, m7Var.b());
        Context applicationContext2 = getApplicationContext();
        long longExtra = intent.getLongExtra("subId", 0L);
        String stringExtra2 = intent.getStringExtra("subUid");
        if (longExtra > 0) {
            ip6VarD = ip6.c(applicationContext2, longExtra, true);
        } else {
            ip6VarD = stringExtra2 != null ? ip6.d(applicationContext2, stringExtra2, true) : null;
        }
        ?? A = (ip6VarD != null || (stringExtra = intent.getStringExtra("tagUid")) == null) ? 0 : lp6.a(applicationContext2, "uid", stringExtra, true);
        ep6 ep6VarI = on6.i(applicationContext2);
        boolean zB = false;
        try {
            ep6VarI.m().setRequestStop(false);
        } catch (Exception unused) {
        }
        long jCurrentTimeMillis2 = System.currentTimeMillis();
        ?? r15 = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
        try {
            try {
                if (ip6VarD != null) {
                    iw6.I(applicationContext2);
                    iW = 1 + ep6VarI.T(ip6VarD, jCurrentTimeMillis2, false);
                    try {
                        c();
                        A = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
                    } catch (gw6.a unused2) {
                        A = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                    } catch (gw6.b e) {
                        e = e;
                        A = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
                        yd.a(this).c(new Intent("com.noinnion.android.greader.reader.action.STOP_SYNC_ERROR_LOGIN"));
                        n56.X1(getApplicationContext(), e.getLocalizedMessage());
                        e.printStackTrace();
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                        A = A;
                    } catch (IOException e2) {
                        e = e2;
                        A = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
                        b(e);
                        e.printStackTrace();
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                        A = A;
                    } catch (Throwable th) {
                        th = th;
                        A = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
                        yd.a(this).c(new Intent("com.noinnion.android.greader.reader.action.STOP_SYNC_ERROR"));
                        n56.X1(getApplicationContext(), th.getLocalizedMessage());
                        th.printStackTrace();
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                        A = A;
                    }
                } else {
                    try {
                        try {
                            if (A != 0) {
                                String str = A.f;
                                A = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
                                int I = iw6.I(applicationContext2);
                                iW = 1 + ep6VarI.W(str, jCurrentTimeMillis2, false, I);
                                c();
                                r15 = I;
                            } else {
                                Object obj = "com.noinnion.android.greader.reader.action.SYNC_FINISHED";
                                if (intent.getBooleanExtra("manual_sync", false) || iw6.M(applicationContext2)) {
                                    on6.b = true;
                                    e();
                                    cw6 cw6VarI = ep6VarI.I(jCurrentTimeMillis2);
                                    iW = cw6VarI.a;
                                    ep6VarI.d();
                                    ep6VarI.G(jCurrentTimeMillis2);
                                    if (iW > 0) {
                                        iw6.U(applicationContext2, "last_sync_time", jCurrentTimeMillis2);
                                    }
                                    d(cw6VarI);
                                    yo6.c(applicationContext2);
                                    A = obj;
                                    r15 = r15;
                                } else {
                                    iW = 0;
                                    A = obj;
                                    r15 = r15;
                                }
                            }
                        } catch (gw6.a unused3) {
                            on6.b = false;
                            ydVarA = yd.a(this);
                            intent2 = new Intent((String) A);
                        } catch (gw6.b e3) {
                            e = e3;
                            yd.a(this).c(new Intent("com.noinnion.android.greader.reader.action.STOP_SYNC_ERROR_LOGIN"));
                            n56.X1(getApplicationContext(), e.getLocalizedMessage());
                            e.printStackTrace();
                            on6.b = false;
                            ydVarA = yd.a(this);
                            intent2 = new Intent((String) A);
                            A = A;
                        } catch (IOException e4) {
                            e = e4;
                            b(e);
                            e.printStackTrace();
                            on6.b = false;
                            ydVarA = yd.a(this);
                            intent2 = new Intent((String) A);
                            A = A;
                        } catch (Throwable th2) {
                            th = th2;
                            yd.a(this).c(new Intent("com.noinnion.android.greader.reader.action.STOP_SYNC_ERROR"));
                            n56.X1(getApplicationContext(), th.getLocalizedMessage());
                            th.printStackTrace();
                            on6.b = false;
                            ydVarA = yd.a(this);
                            intent2 = new Intent((String) A);
                            A = A;
                        }
                    } catch (gw6.a unused4) {
                        iW = 0;
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                    } catch (gw6.b e5) {
                        e = e5;
                        iW = 0;
                        yd.a(this).c(new Intent("com.noinnion.android.greader.reader.action.STOP_SYNC_ERROR_LOGIN"));
                        n56.X1(getApplicationContext(), e.getLocalizedMessage());
                        e.printStackTrace();
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                        A = A;
                    } catch (IOException e6) {
                        e = e6;
                        iW = 0;
                        b(e);
                        e.printStackTrace();
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                        A = A;
                    } catch (Throwable th3) {
                        th = th3;
                        iW = 0;
                        yd.a(this).c(new Intent("com.noinnion.android.greader.reader.action.STOP_SYNC_ERROR"));
                        n56.X1(getApplicationContext(), th.getLocalizedMessage());
                        th.printStackTrace();
                        on6.b = false;
                        ydVarA = yd.a(this);
                        intent2 = new Intent((String) A);
                        A = A;
                    }
                }
                on6.b = false;
                ydVarA = yd.a(this);
                intent2 = new Intent((String) A);
            } catch (Throwable th4) {
                on6.b = false;
                yd.a(this).c(new Intent((String) A));
                throw th4;
            }
        } catch (gw6.a unused5) {
            A = r15;
        } catch (gw6.b e7) {
            e = e7;
            A = r15;
        } catch (IOException e8) {
            e = e8;
            A = r15;
        } catch (Throwable th5) {
            th = th5;
            A = r15;
        }
        ydVarA.c(intent2);
        if (!intent.getBooleanExtra("sync_offline", false)) {
            String strF = iw6.F(applicationContext2, "offline_usage");
            if (strF != null && strF.length() != 0) {
                zB = iw6.b(applicationContext2, Integer.parseInt(strF));
            }
            if (!zB) {
                return;
            }
        }
        if (iw6.D(applicationContext2) == -1) {
            n56.s0(applicationContext2, null, 0L);
        } else if (iW > 0) {
            n56.s0(applicationContext2, null, jCurrentTimeMillis2);
        }
    }