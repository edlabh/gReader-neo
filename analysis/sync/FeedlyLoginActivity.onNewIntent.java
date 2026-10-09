    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        String queryParameter;
        im7.e(intent, "intent");
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if (data == null || (queryParameter = data.getQueryParameter("code")) == null) {
            return;
        }
        View viewFindViewById = findViewById(R.id.loading);
        im7.d(viewFindViewById, "findViewById<View>(R.id.loading)");
        viewFindViewById.setVisibility(0);
        im7.d(queryParameter, "authCode");
        ae7 ae7Var = new ae7(new sr6(this, queryParameter));
        im7.d(ae7Var, "Completable.fromCallable…}\n            }\n        }");
        hb7 hb7VarG = ae7Var.j(ej7.b).g(fc7.a());
        im7.d(hb7VarG, "fetchApiToken(authCode)\n…dSchedulers.mainThread())");
        ic7 ic7VarD = bj7.d(hb7VarG, new b(), new a());
        im7.e(ic7VarD, "disposable");
        this.x.b(ic7VarD);
    }