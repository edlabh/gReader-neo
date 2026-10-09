    @Override // com.noinnion.android.reader.ui.BaseDialogActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        z(bundle, BaseDialogActivity.a.WHEN_LARGE);
        setContentView(R.layout.downloads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            s().x(toolbar);
            t().t(R.string.menu_downloads);
            t().o(true);
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        this.y = viewPager;
        viewPager.setOffscreenPageLimit(3);
        ArrayList arrayList = new ArrayList();
        this.A = arrayList;
        arrayList.add(new c(this, "history", getString(R.string.download_history), qp6.class, null));
        this.A.add(new c(this, "downloading", getString(R.string.download_downloading), rp6.class, null));
        d dVar = new d(o());
        this.B = dVar;
        this.y.setAdapter(dVar);
        this.y.b(new a());
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        this.z = slidingTabLayout;
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        this.z.setSelectedIndicatorColors(getResources().getColor(R.color.primary_default));
        this.z.setDistributeEvenly(true);
        this.z.setViewPager(this.y);
        this.z.setCustomTabColorizer(new b(this));
        if (bundle != null) {
            this.y.setCurrentItem(this.B.m(bundle.getString("tab")));
        } else {
            this.y.setCurrentItem(this.B.m(getIntent().getBooleanExtra("startDownloading", false) ? "downloading" : "history"));
        }
    }