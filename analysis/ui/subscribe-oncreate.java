    @Override // com.noinnion.android.reader.ui.BaseDialogActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        z(bundle, BaseDialogActivity.a.WHEN_LARGE);
        setContentView(R.layout.subscribe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            s().x(toolbar);
            t().t(R.string.sub_add_subscription);
            t().o(true);
        }
        Intent intent = getIntent();
        if ("android.intent.action.SEND".equals(intent.getAction())) {
            this.y = intent.getStringExtra("android.intent.extra.TEXT");
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        this.z = viewPager;
        viewPager.setOffscreenPageLimit(3);
        this.B = new ArrayList();
        Bundle bundle2 = new Bundle();
        bundle2.putString("input", this.y);
        this.B.add(new c(this, "search", getString(R.string.txt_search), SubscribeSearchFragment.class, bundle2));
        this.B.add(new c(this, "podcast", getString(R.string.label_podcast), SubscribePodcastFragment.class, null));
        Bundle bundle3 = new Bundle();
        bundle3.putInt("browse_type", 1);
        this.B.add(new c(this, "browse", getString(R.string.subscribe_browse), SubscribeBrowseFragment.class, bundle3));
        Bundle bundle4 = new Bundle();
        bundle4.putInt("browse_type", 2);
        this.B.add(new c(this, "news", getString(R.string.subscribe_news), SubscribeBrowseFragment.class, bundle4));
        d dVar = new d(o());
        this.C = dVar;
        this.z.setAdapter(dVar);
        this.z.b(new a());
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        this.A = slidingTabLayout;
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        int i = 0;
        this.A.setSelectedIndicatorColors(getResources().getColor(R.color.primary_default));
        this.A.setDistributeEvenly(true);
        this.A.setViewPager(this.z);
        this.A.setCustomTabColorizer(new b(this));
        if (bundle != null) {
            String string = bundle.getString("tab");
            ViewPager viewPager2 = this.z;
            Iterator<c> it = SubscribeActivity.this.B.iterator();
            while (it.hasNext()) {
                if (it.next().a.equals(string)) {
                    viewPager2.setCurrentItem(i);
                }
                i++;
            }
            i = -1;
            viewPager2.setCurrentItem(i);
        }
    }