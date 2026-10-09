    @Override // com.noinnion.android.reader.ui.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        boolean zB = false;
        y(bundle, false, false);
        Context applicationContext = getApplicationContext();
        if (getIntent().getBooleanExtra("auto_login", true) && cn6.c0(applicationContext) >= 5) {
            String strF = iw6.F(applicationContext, "sync_on_start");
            if (strF != null && strF.length() != 0) {
                zB = iw6.b(applicationContext, Integer.parseInt(strF));
            }
            A(zB);
            return;
        }
        setContentView(R.layout.login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            s().x(toolbar);
            u uVarT = t();
            if (uVarT != null) {
                uVarT.q(false);
            }
        }
        findViewById(R.id.getting_started).setOnClickListener(this);
        findViewById(R.id.client_feedly).setOnClickListener(this);
        findViewById(R.id.client_old_reader).setOnClickListener(this);
        findViewById(R.id.client_inoreader).setOnClickListener(this);
        findViewById(R.id.client_rss_reader).setOnClickListener(this);
        n56.y2(this, R.string.ga_screen_login);
    }