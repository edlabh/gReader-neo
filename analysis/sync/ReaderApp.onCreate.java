/* JADX INFO: loaded from: classes2.dex */
public final class ReaderApp extends Application {
    @Override // android.app.Application
    public void onCreate() {
        e45 e45VarX;
        Boolean boolA;
        super.onCreate();
        en6 en6Var = new en6(this);
        o08 o08Var = o08.b;
        im7.e(o08Var, "koinContext");
        im7.e(en6Var, "appDeclaration");
        im7.e(o08Var, "koinContext");
        im7.e(en6Var, "appDeclaration");
        synchronized (o08Var) {
            l08 l08VarB = l08.b();
            o08Var.a(l08VarB);
            en6Var.invoke(l08VarB);
            l08VarB.a();
        }
        lw6.e(getApplicationContext());
        jb6 jb6VarA = jb6.a();
        qn6[] qn6VarArrValues = qn6.values();
        ArrayList arrayList = new ArrayList(8);
        for (int i = 0; i < 8; i++) {
            qn6 qn6Var = qn6VarArrValues[i];
            arrayList.add(new rj7(qn6Var.k(), qn6Var.i()));
        }
        Map mapT = ek7.t(arrayList);
        Objects.requireNonNull(jb6VarA);
        HashMap map = new HashMap();
        for (Map.Entry entry : mapT.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof byte[]) {
                map.put((String) entry.getKey(), new String((byte[]) value));
            } else {
                map.put((String) entry.getKey(), value.toString());
            }
        }
        try {
            Date date = yb6.f;
            new JSONObject();
            e45VarX = jb6VarA.e.c(new yb6(new JSONObject(map), yb6.f, new JSONArray(), new JSONObject())).p(new d45() { // from class: ib6
                @Override // defpackage.d45
                public e45 a(Object obj) {
                    return c50.X(null);
                }
            });
        } catch (JSONException e) {
            Log.e("FirebaseRemoteConfig", "The provided defaults map could not be processed.", e);
            e45VarX = c50.X(null);
        }
        e45VarX.b(new pn6(jb6VarA));
        pl6 pl6VarB = pl6.b();
        Objects.requireNonNull(pl6VarB);
        pl6VarB.a = getApplicationContext();
        pl6VarB.b = getSharedPreferences(getString(com.michaelflisar.gdprdialog.R$string.gdpr_preference_file), 0);
        int i2 = com.michaelflisar.gdprdialog.R$string.gdpr_type_ads;
        GDPRNetwork gDPRNetwork = new GDPRNetwork(this, "AdMob", "https://policies.google.com/privacy", i2, true);
        gDPRNetwork.h = true;
        gDPRNetwork.k = "https://support.google.com/admob/answer/9012903";
        sl6.a = gDPRNetwork;
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(i2);
        new ArrayList();
        getString(com.michaelflisar.gdprdialog.R$string.gdpr_type_cloud_database);
        new ArrayList();
        int i3 = com.michaelflisar.gdprdialog.R$string.gdpr_type_crash;
        sl6.b = new GDPRNetwork(this, "Firebase", "https://firebase.google.com/support/privacy", i3, false);
        int i4 = com.michaelflisar.gdprdialog.R$string.gdpr_type_analytics;
        sl6.c = new GDPRNetwork(this, "Firebase", "https://firebase.google.com/support/privacy", i4, false);
        int i5 = com.michaelflisar.gdprdialog.R$string.gdpr_type_notifications;
        getString(i5);
        new ArrayList();
        getString(i4);
        new ArrayList();
        getString(i3);
        new ArrayList();
        getString(i3);
        new ArrayList();
        getString(i4);
        new ArrayList();
        getString(i4);
        new ArrayList();
        getString(i5);
        new ArrayList();
        getString(com.michaelflisar.gdprdialog.R$string.gdpr_type_authorization);
        new ArrayList();
        getString(i5);
        new ArrayList();
        ql6 ql6Var = pl6.b().a().a;
        Objects.requireNonNull(ql6Var);
        if (ql6Var == ql6.PERSONAL_CONSENT || ql6Var == ql6.AUTOMATIC_PERSONAL_CONSENT) {
            FirebaseAnalytics.getInstance(this).a.i(null, "allow_personalized_ads", "true", false);
        }
        pe5 pe5VarB = pe5.b();
        pe5VarB.a();
        yg5 yg5Var = (yg5) pe5VarB.d.a(yg5.class);
        Objects.requireNonNull(yg5Var, "FirebaseCrashlytics component is not present.");
        vi5 vi5Var = yg5Var.a;
        Boolean bool = Boolean.TRUE;
        bj5 bj5Var = vi5Var.c;
        synchronized (bj5Var) {
            if (bool != null) {
                try {
                    bj5Var.f = false;
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (bool != null) {
                boolA = bool;
            } else {
                pe5 pe5Var = bj5Var.b;
                pe5Var.a();
                boolA = bj5Var.a(pe5Var.a);
            }
            bj5Var.g = boolA;
            SharedPreferences.Editor editorEdit = bj5Var.a.edit();
            if (bool != null) {
                editorEdit.putBoolean("firebase_crashlytics_collection_enabled", true);
            } else {
                editorEdit.remove("firebase_crashlytics_collection_enabled");
            }
            editorEdit.commit();
            synchronized (bj5Var.c) {
                if (bj5Var.b()) {
                    if (!bj5Var.e) {
                        bj5Var.d.b(null);
                        bj5Var.e = true;
                    }
                } else if (bj5Var.e) {
                    bj5Var.d = new f45<>();
                    bj5Var.e = false;
                }
            }
        }
        if (!kl6.a.getAndSet(true)) {
            ll6 ll6Var = new ll6(this, "org/threeten/bp/TZDB.dat");
            if (m78.a.get()) {
                throw new IllegalStateException("Already initialized");
            }
            if (!m78.b.compareAndSet(null, ll6Var)) {
                throw new IllegalStateException("Initializer was already set, possibly with a default during initialization");
            }
        }
        Context applicationContext = getApplicationContext();
        p07.b = false;
        gz6.b bVar = new gz6.b();
        bVar.g = true;
        bVar.h = true;
        bVar.i = true;
        bVar.a(Bitmap.Config.RGB_565);
        gz6 gz6VarB = bVar.b();
        iz6.b bVar2 = new iz6.b(applicationContext);
        bVar2.f(3);
        bVar2.e(4);
        bVar2.h = true;
        bVar2.c(1000);
        bVar2.p = gz6VarB;
        wz6 wz6Var = wz6.LIFO;
        bVar2.d(wz6Var);
        hz6.j().k(bVar2.a());
        iz6.b bVar3 = new iz6.b(applicationContext);
        bVar3.f(3);
        bVar3.e(4);
        bVar3.h = true;
        bVar3.b(new vy6(n56.M0(applicationContext, "uil-icons")));
        bVar3.c(1000);
        bVar3.p = gz6VarB;
        bVar3.d(wz6Var);
        sx6.n().k(bVar3.a());
        iz6.b bVar4 = new iz6.b(applicationContext);
        bVar4.f(3);
        bVar4.e(2);
        bVar4.h = true;
        bVar4.b(new vy6(n56.M0(applicationContext, "uil-temp")));
        bVar4.c(100);
        bVar4.p = gz6VarB;
        bVar4.d(wz6Var);
        ux6.n().k(bVar4.a());
        new fn6(this).start();
        n56.a = gn6.e;
    }