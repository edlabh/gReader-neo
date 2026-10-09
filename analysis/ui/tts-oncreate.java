    @Override // com.noinnion.android.reader.ui.BaseDialogActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        A(bundle, true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.noinnion.android.greader.reader.action.INIT_LANGUAGE");
        intentFilter.addAction("com.noinnion.android.greader.reader.action.START_INIT");
        intentFilter.addAction("com.noinnion.android.greader.reader.action.INIT_ITEM");
        intentFilter.addAction("com.noinnion.android.greader.reader.action.STATUS_CHANGED");
        intentFilter.addAction("com.noinnion.android.greader.reader.action.STOP_SERVICE");
        yd.a(this).b(this.I, intentFilter);
        bindService(new Intent(this, (Class<?>) TtsService.class), this.H, 1);
        setContentView(com.noinnion.android.greader.reader.R.layout.tts_activity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        im7.d(intent, "intent");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.B = extras.getLongArray("itemIds");
            this.C = extras.getLong("itemId");
            this.D = extras.getString("ttsTitle");
            this.E = extras.getString("ttsUrl");
        }
        Toolbar toolbar = this.mToolbar;
        if (toolbar == null) {
            im7.k("mToolbar");
            throw null;
        }
        s().x(toolbar);
        u uVarT = t();
        boolean z = false;
        if (uVarT != null) {
            uVarT.o(true);
            uVarT.q(false);
        }
        ListView listView = this.mListView;
        if (listView == null) {
            im7.k("mListView");
            throw null;
        }
        View view = this.mEmptyView;
        if (view == null) {
            im7.k("mEmptyView");
            throw null;
        }
        listView.setEmptyView(view);
        View view2 = this.mPremiumUpgrade;
        if (view2 == null) {
            im7.k("mPremiumUpgrade");
            throw null;
        }
        n56.B2(view2, Boolean.valueOf(!cn6.d0(this)));
        TtsService ttsService = TtsService.B;
        if (ttsService != null && ttsService.e) {
            z = true;
        }
        if (!z) {
            try {
                Intent intent2 = new Intent();
                intent2.setAction("android.speech.tts.engine.CHECK_TTS_DATA");
                startActivityForResult(intent2, 1);
            } catch (Exception e) {
                n56.X1(getApplicationContext(), e.getLocalizedMessage());
                finish();
            }
        }
        n56.y2(this, com.noinnion.android.greader.reader.R.string.ga_screen_tts);
    }