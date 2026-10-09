    @Override // com.noinnion.android.reader.ui.BaseDialogActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        A(bundle, false);
        setResult(0);
        setContentView(R.layout.login_feedly);
        View viewFindViewById = findViewById(R.id.toolbar);
        if (!(viewFindViewById instanceof Toolbar)) {
            viewFindViewById = null;
        }
        Toolbar toolbar = (Toolbar) viewFindViewById;
        if (toolbar != null) {
            s().x(toolbar);
        }
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.website).setOnClickListener(this);
        n56.y2(this, R.string.ga_screen_login_feedly);
    }