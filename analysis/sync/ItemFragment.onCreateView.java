    /* JADX WARN: Code duplicated, block: B:139:0x0286  */
    /* JADX WARN: Code duplicated, block: B:144:0x02b2  */
    /* JADX WARN: Code duplicated, block: B:146:0x02b5  */
    /* JADX WARN: Code duplicated, block: B:148:0x02d2  */
    /* JADX WARN: Code duplicated, block: B:151:0x02e6  */
    /* JADX WARN: Code duplicated, block: B:153:0x02f2  */
    /* JADX WARN: Code duplicated, block: B:154:0x02fc  */
    /* JADX WARN: Code duplicated, block: B:156:0x0308  */
    /* JADX WARN: Code duplicated, block: B:157:0x0312  */
    /* JADX WARN: Code duplicated, block: B:159:0x031a  */
    /* JADX WARN: Code duplicated, block: B:161:0x0326  */
    /* JADX WARN: Code duplicated, block: B:162:0x0328  */
    /* JADX WARN: Code duplicated, block: B:165:0x0335  */
    /* JADX WARN: Code duplicated, block: B:168:0x033d  */
    /* JADX WARN: Code duplicated, block: B:169:0x0392  */
    /* JADX WARN: Code duplicated, block: B:171:0x03d9 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:175:0x03df  */
    /* JADX WARN: Code duplicated, block: B:177:0x03f9  */
    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context applicationContext;
        boolean z;
        fc6 fc6Var;
        String strD;
        String strD2;
        boolean z2;
        boolean z3;
        AdRequest.Builder builder;
        ql6 ql6Var;
        AdRequest.Builder builder2;
        ql6 ql6Var2;
        boolean z4;
        im7.e(layoutInflater, "inflater");
        View viewInflate = layoutInflater.inflate(this.E == kx6.PHONE ? R.layout.item_fragment : R.layout.item_tablet_fragment, viewGroup, false);
        im7.d(viewInflate, "this");
        FragmentActivity activity = getActivity();
        if (activity == null || (applicationContext = activity.getApplicationContext()) == null) {
            viewInflate = viewInflate;
        } else {
            new WebView(applicationContext).resumeTimers();
            kw6 kw6VarH = on6.h(applicationContext);
            im7.d(kw6VarH, "ReaderVar.getReadingOptions(context)");
            this.C = kw6VarH;
            this.D = iw6.b(applicationContext, kw6VarH.c);
            this.F = iw6.C(applicationContext);
            View viewFindViewById = viewInflate.findViewById(R.id.frame);
            kw6 kw6Var = this.C;
            ImageView imageView = null;
            if (kw6Var == null) {
                im7.k("mReadingOptions");
                throw null;
            }
            if (!kw6Var.w) {
                viewFindViewById.setLayerType(1, null);
            }
            if (!lw6.k()) {
                kw6 kw6Var2 = this.C;
                if (kw6Var2 == null) {
                    im7.k("mReadingOptions");
                    throw null;
                }
                if (kw6Var2.b) {
                    viewFindViewById.setBackgroundColor(-14737633);
                }
            }
            WebViewPager webViewPager = (WebViewPager) viewInflate.findViewById(R.id.web_view_pager);
            if (webViewPager != null) {
                kw6 kw6Var3 = this.C;
                if (kw6Var3 == null) {
                    im7.k("mReadingOptions");
                    throw null;
                }
                webViewPager.setPagingDisabled(kw6Var3.p == 0);
                c cVar = new c();
                if (webViewPager.V == null) {
                    webViewPager.V = new ArrayList();
                }
                webViewPager.V.add(cVar);
            } else {
                webViewPager = null;
            }
            this.f = webViewPager;
            this.N = new GestureDetector(applicationContext, new e());
            View viewFindViewById2 = viewInflate.findViewById(R.id.item_controls);
            if (viewFindViewById2 != null) {
                kw6 kw6Var4 = this.C;
                if (kw6Var4 == null) {
                    im7.k("mReadingOptions");
                    throw null;
                }
                if (!kw6Var4.t) {
                    viewFindViewById2.setVisibility(8);
                }
            } else {
                viewFindViewById2 = null;
            }
            this.z = viewFindViewById2;
            View viewFindViewById3 = viewInflate.findViewById(R.id.zoom_controls);
            if (viewFindViewById3 != null) {
                kw6 kw6Var5 = this.C;
                if (kw6Var5 == null) {
                    im7.k("mReadingOptions");
                    throw null;
                }
                if (!kw6Var5.u) {
                    viewFindViewById3.setVisibility(8);
                }
            } else {
                viewFindViewById3 = null;
            }
            this.A = viewFindViewById3;
            View viewFindViewById4 = viewInflate.findViewById(R.id.action_controls);
            if (viewFindViewById4 != null) {
                kw6 kw6Var6 = this.C;
                if (kw6Var6 == null) {
                    im7.k("mReadingOptions");
                    throw null;
                }
                if (!kw6Var6.s) {
                    viewFindViewById4.setVisibility(8);
                }
            } else {
                viewFindViewById4 = null;
            }
            this.B = viewFindViewById4;
            this.v = (ProgressBar) viewInflate.findViewById(R.id.progress);
            View viewFindViewById5 = viewInflate.findViewById(R.id.go_back);
            if (viewFindViewById5 != null) {
                viewFindViewById5.setOnClickListener(this);
            } else {
                viewFindViewById5 = null;
            }
            this.w = viewFindViewById5;
            View viewFindViewById6 = viewInflate.findViewById(R.id.next);
            if (viewFindViewById6 != null) {
                viewFindViewById6.setOnClickListener(this);
            } else {
                viewFindViewById6 = null;
            }
            this.s = viewFindViewById6;
            View viewFindViewById7 = viewInflate.findViewById(R.id.previous);
            if (viewFindViewById7 != null) {
                viewFindViewById7.setOnClickListener(this);
            } else {
                viewFindViewById7 = null;
            }
            this.r = viewFindViewById7;
            ImageView imageView2 = (ImageView) viewInflate.findViewById(R.id.fullscreen);
            if (imageView2 != null) {
                imageView2.setOnClickListener(this);
                imageView2.setImageResource(on6.g(applicationContext).m ? R.drawable.ic_contract : R.drawable.ic_expand);
            }
            View viewFindViewById8 = viewInflate.findViewById(R.id.zoom_in);
            if (viewFindViewById8 != null) {
                viewFindViewById8.setOnClickListener(this);
            }
            View viewFindViewById9 = viewInflate.findViewById(R.id.zoom_out);
            if (viewFindViewById9 != null) {
                viewFindViewById9.setOnClickListener(this);
            }
            View viewFindViewById10 = viewInflate.findViewById(R.id.readability);
            if (viewFindViewById10 != null) {
                viewFindViewById10.setOnClickListener(this);
            }
            View viewFindViewById11 = viewInflate.findViewById(R.id.icon_media);
            if (viewFindViewById11 != null) {
                viewFindViewById11.setOnClickListener(this);
            }
            View viewFindViewById12 = viewInflate.findViewById(R.id.browser);
            if (viewFindViewById12 != null) {
                viewFindViewById12.setOnClickListener(this);
            }
            View viewFindViewById13 = viewInflate.findViewById(R.id.browser);
            if (viewFindViewById13 != null) {
                viewFindViewById13.setOnLongClickListener(this);
            }
            View viewFindViewById14 = viewInflate.findViewById(R.id.share);
            if (viewFindViewById14 != null) {
                viewFindViewById14.setOnClickListener(this);
            }
            View viewFindViewById15 = viewInflate.findViewById(R.id.share);
            if (viewFindViewById15 != null) {
                viewFindViewById15.setOnLongClickListener(this);
            }
            View viewFindViewById16 = viewInflate.findViewById(R.id.service);
            if (viewFindViewById16 != null) {
                viewFindViewById16.setOnClickListener(this);
            }
            kw6 kw6Var7 = this.C;
            if (kw6Var7 == null) {
                im7.k("mReadingOptions");
                throw null;
            }
            if (kw6Var7.m > 0) {
                viewInflate.findViewById(R.id.voice_reading).setOnClickListener(this);
            } else {
                View viewFindViewById17 = viewInflate.findViewById(R.id.voice_reading);
                im7.d(viewFindViewById17, "view.findViewById<View>(R.id.voice_reading)");
                viewFindViewById17.setVisibility(8);
            }
            ImageView imageView3 = (ImageView) viewInflate.findViewById(R.id.icon_star);
            if (imageView3 != null) {
                imageView3.setOnClickListener(this);
            } else {
                imageView3 = null;
            }
            this.t = imageView3;
            ImageView imageView4 = (ImageView) viewInflate.findViewById(R.id.keep_unread);
            if (imageView4 != null) {
                imageView4.setOnClickListener(this);
                imageView = imageView4;
            }
            this.u = imageView;
            if (cn6.d0(applicationContext)) {
                viewInflate = viewInflate;
            } else {
                this.G = (ViewGroup) viewInflate.findViewById(R.id.ad_banner);
                ql6 ql6Var3 = ql6.AUTOMATIC_PERSONAL_CONSENT;
                ql6 ql6Var4 = ql6.PERSONAL_CONSENT;
                int i2 = cn6.b;
                long jA = iw6.A(applicationContext, "next_interstitial_time", 0L);
                if (jA == 0) {
                    iw6.U(applicationContext, "next_interstitial_time", (jb6.a().b("ad_interstitial_free_trial_in_seconds") * 1000) + System.currentTimeMillis());
                } else {
                    if (jA < System.currentTimeMillis()) {
                        iw6.U(applicationContext, "next_interstitial_time", (jb6.a().b("ad_interstitial_interval_in_seconds") * 1000) + System.currentTimeMillis());
                        z = true;
                    }
                    if (z) {
                        oq6 oq6Var = new oq6(this, applicationContext);
                        im7.e(applicationContext, "context");
                        im7.e(oq6Var, "onAdLoaded");
                        builder2 = new AdRequest.Builder();
                        pl6 pl6VarB = pl6.b();
                        im7.d(pl6VarB, "GDPR.getInstance()");
                        rl6 rl6VarA = pl6VarB.a();
                        im7.d(rl6VarA, "GDPR.getInstance().consentState");
                        ql6Var2 = rl6VarA.a;
                        im7.d(ql6Var2, "GDPR.getInstance().consentState.consent");
                        if (ql6Var2 != ql6Var4 || ql6Var2 == ql6Var3) {
                            z4 = true;
                        } else {
                            z4 = false;
                        }
                        if (!z4) {
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("npa", "1");
                            builder2.addNetworkExtrasBundle(AdMobAdapter.class, bundle2);
                        }
                        AdRequest adRequestBuild = builder2.build();
                        im7.d(adRequestBuild, "adRequestBuilder.build()");
                        RewardedInterstitialAd.load(applicationContext, "ca-app-pub-6875289151086704/6336833518", adRequestBuild, new aw6(oq6Var));
                    }
                    fc6Var = jb6.a().g;
                    strD = fc6.d(fc6Var.c, "ad_banner_format_native");
                    if (strD == null) {
                        strD2 = fc6.d(fc6Var.d, "ad_banner_format_native");
                        if (strD2 == null) {
                            fc6.e("ad_banner_format_native", "Boolean");
                        } else {
                            if (fc6.e.matcher(strD2).matches()) {
                                z2 = true;
                            } else if (!fc6.f.matcher(strD2).matches()) {
                                fc6.e("ad_banner_format_native", "Boolean");
                            }
                            if (z2) {
                                pq6 pq6Var = new pq6(this);
                                im7.e(applicationContext, "context");
                                im7.e(pq6Var, "onAdLoaded");
                                new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                            } else {
                                z3 = true;
                                AdSize adSize = AdSize.BANNER;
                                im7.d(adSize, "bannerAdSize");
                                qq6 qq6Var = new qq6(this);
                                im7.e(applicationContext, "context");
                                im7.e(adSize, "size");
                                im7.e(qq6Var, "onAdLoaded");
                                AdView adView = new AdView(applicationContext);
                                adView.setAdSize(adSize);
                                adView.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                                adView.setAdListener(new wv6(adView, adSize, qq6Var));
                                builder = new AdRequest.Builder();
                                pl6 pl6VarB2 = pl6.b();
                                im7.d(pl6VarB2, "GDPR.getInstance()");
                                rl6 rl6VarA2 = pl6VarB2.a();
                                im7.d(rl6VarA2, "GDPR.getInstance().consentState");
                                ql6Var = rl6VarA2.a;
                                im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                                if (ql6Var != ql6Var4 && ql6Var != ql6Var3) {
                                    z3 = false;
                                }
                                if (!z3) {
                                    Bundle bundle3 = new Bundle();
                                    bundle3.putString("npa", "1");
                                    builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle3);
                                }
                                AdRequest adRequestBuild2 = builder.build();
                                im7.d(adRequestBuild2, "adRequestBuilder.build()");
                                adView.loadAd(adRequestBuild2);
                                this.H = adView;
                            }
                        }
                        z2 = false;
                        if (z2) {
                            pq6 pq6Var2 = new pq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(pq6Var2, "onAdLoaded");
                            new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var2)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                        } else {
                            z3 = true;
                            AdSize adSize2 = AdSize.BANNER;
                            im7.d(adSize2, "bannerAdSize");
                            qq6 qq6Var2 = new qq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(adSize2, "size");
                            im7.e(qq6Var2, "onAdLoaded");
                            AdView adView2 = new AdView(applicationContext);
                            adView2.setAdSize(adSize2);
                            adView2.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                            adView2.setAdListener(new wv6(adView2, adSize2, qq6Var2));
                            builder = new AdRequest.Builder();
                            pl6 pl6VarB3 = pl6.b();
                            im7.d(pl6VarB3, "GDPR.getInstance()");
                            rl6 rl6VarA3 = pl6VarB3.a();
                            im7.d(rl6VarA3, "GDPR.getInstance().consentState");
                            ql6Var = rl6VarA3.a;
                            im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                            if (ql6Var != ql6Var4) {
                                z3 = false;
                            }
                            if (!z3) {
                                Bundle bundle4 = new Bundle();
                                bundle4.putString("npa", "1");
                                builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle4);
                            }
                            AdRequest adRequestBuild3 = builder.build();
                            im7.d(adRequestBuild3, "adRequestBuilder.build()");
                            adView2.loadAd(adRequestBuild3);
                            this.H = adView2;
                        }
                    } else {
                        if (fc6.e.matcher(strD).matches()) {
                            fc6Var.a("ad_banner_format_native", fc6.b(fc6Var.c));
                        } else {
                            if (fc6.f.matcher(strD).matches()) {
                                fc6Var.a("ad_banner_format_native", fc6.b(fc6Var.c));
                            } else {
                                strD2 = fc6.d(fc6Var.d, "ad_banner_format_native");
                                if (strD2 == null) {
                                    fc6.e("ad_banner_format_native", "Boolean");
                                } else {
                                    if (fc6.e.matcher(strD2).matches()) {
                                        if (!fc6.f.matcher(strD2).matches()) {
                                            fc6.e("ad_banner_format_native", "Boolean");
                                        }
                                    }
                                    if (z2) {
                                        pq6 pq6Var3 = new pq6(this);
                                        im7.e(applicationContext, "context");
                                        im7.e(pq6Var3, "onAdLoaded");
                                        new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var3)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                                    } else {
                                        z3 = true;
                                        AdSize adSize3 = AdSize.BANNER;
                                        im7.d(adSize3, "bannerAdSize");
                                        qq6 qq6Var3 = new qq6(this);
                                        im7.e(applicationContext, "context");
                                        im7.e(adSize3, "size");
                                        im7.e(qq6Var3, "onAdLoaded");
                                        AdView adView3 = new AdView(applicationContext);
                                        adView3.setAdSize(adSize3);
                                        adView3.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                                        adView3.setAdListener(new wv6(adView3, adSize3, qq6Var3));
                                        builder = new AdRequest.Builder();
                                        pl6 pl6VarB4 = pl6.b();
                                        im7.d(pl6VarB4, "GDPR.getInstance()");
                                        rl6 rl6VarA4 = pl6VarB4.a();
                                        im7.d(rl6VarA4, "GDPR.getInstance().consentState");
                                        ql6Var = rl6VarA4.a;
                                        im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                                        if (ql6Var != ql6Var4) {
                                            z3 = false;
                                        }
                                        if (!z3) {
                                            Bundle bundle5 = new Bundle();
                                            bundle5.putString("npa", "1");
                                            builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle5);
                                        }
                                        AdRequest adRequestBuild4 = builder.build();
                                        im7.d(adRequestBuild4, "adRequestBuilder.build()");
                                        adView3.loadAd(adRequestBuild4);
                                        this.H = adView3;
                                    }
                                }
                            }
                            z2 = false;
                            if (z2) {
                                pq6 pq6Var4 = new pq6(this);
                                im7.e(applicationContext, "context");
                                im7.e(pq6Var4, "onAdLoaded");
                                new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var4)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                            } else {
                                z3 = true;
                                AdSize adSize4 = AdSize.BANNER;
                                im7.d(adSize4, "bannerAdSize");
                                qq6 qq6Var4 = new qq6(this);
                                im7.e(applicationContext, "context");
                                im7.e(adSize4, "size");
                                im7.e(qq6Var4, "onAdLoaded");
                                AdView adView4 = new AdView(applicationContext);
                                adView4.setAdSize(adSize4);
                                adView4.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                                adView4.setAdListener(new wv6(adView4, adSize4, qq6Var4));
                                builder = new AdRequest.Builder();
                                pl6 pl6VarB5 = pl6.b();
                                im7.d(pl6VarB5, "GDPR.getInstance()");
                                rl6 rl6VarA5 = pl6VarB5.a();
                                im7.d(rl6VarA5, "GDPR.getInstance().consentState");
                                ql6Var = rl6VarA5.a;
                                im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                                if (ql6Var != ql6Var4) {
                                    z3 = false;
                                }
                                if (!z3) {
                                    Bundle bundle6 = new Bundle();
                                    bundle6.putString("npa", "1");
                                    builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle6);
                                }
                                AdRequest adRequestBuild5 = builder.build();
                                im7.d(adRequestBuild5, "adRequestBuilder.build()");
                                adView4.loadAd(adRequestBuild5);
                                this.H = adView4;
                            }
                        }
                        z2 = true;
                        if (z2) {
                            pq6 pq6Var5 = new pq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(pq6Var5, "onAdLoaded");
                            new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var5)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                        } else {
                            z3 = true;
                            AdSize adSize5 = AdSize.BANNER;
                            im7.d(adSize5, "bannerAdSize");
                            qq6 qq6Var5 = new qq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(adSize5, "size");
                            im7.e(qq6Var5, "onAdLoaded");
                            AdView adView5 = new AdView(applicationContext);
                            adView5.setAdSize(adSize5);
                            adView5.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                            adView5.setAdListener(new wv6(adView5, adSize5, qq6Var5));
                            builder = new AdRequest.Builder();
                            pl6 pl6VarB6 = pl6.b();
                            im7.d(pl6VarB6, "GDPR.getInstance()");
                            rl6 rl6VarA6 = pl6VarB6.a();
                            im7.d(rl6VarA6, "GDPR.getInstance().consentState");
                            ql6Var = rl6VarA6.a;
                            im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                            if (ql6Var != ql6Var4) {
                                z3 = false;
                            }
                            if (!z3) {
                                Bundle bundle7 = new Bundle();
                                bundle7.putString("npa", "1");
                                builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle7);
                            }
                            AdRequest adRequestBuild6 = builder.build();
                            im7.d(adRequestBuild6, "adRequestBuilder.build()");
                            adView5.loadAd(adRequestBuild6);
                            this.H = adView5;
                        }
                    }
                }
                z = false;
                if (z) {
                    oq6 oq6Var2 = new oq6(this, applicationContext);
                    im7.e(applicationContext, "context");
                    im7.e(oq6Var2, "onAdLoaded");
                    builder2 = new AdRequest.Builder();
                    pl6 pl6VarB7 = pl6.b();
                    im7.d(pl6VarB7, "GDPR.getInstance()");
                    rl6 rl6VarA7 = pl6VarB7.a();
                    im7.d(rl6VarA7, "GDPR.getInstance().consentState");
                    ql6Var2 = rl6VarA7.a;
                    im7.d(ql6Var2, "GDPR.getInstance().consentState.consent");
                    if (ql6Var2 != ql6Var4) {
                        z4 = true;
                    } else {
                        z4 = true;
                    }
                    if (!z4) {
                        Bundle bundle8 = new Bundle();
                        bundle8.putString("npa", "1");
                        builder2.addNetworkExtrasBundle(AdMobAdapter.class, bundle8);
                    }
                    AdRequest adRequestBuild7 = builder2.build();
                    im7.d(adRequestBuild7, "adRequestBuilder.build()");
                    RewardedInterstitialAd.load(applicationContext, "ca-app-pub-6875289151086704/6336833518", adRequestBuild7, new aw6(oq6Var2));
                }
                fc6Var = jb6.a().g;
                strD = fc6.d(fc6Var.c, "ad_banner_format_native");
                if (strD == null) {
                    strD2 = fc6.d(fc6Var.d, "ad_banner_format_native");
                    if (strD2 == null) {
                        fc6.e("ad_banner_format_native", "Boolean");
                    } else {
                        if (fc6.e.matcher(strD2).matches()) {
                            z2 = true;
                        } else if (!fc6.f.matcher(strD2).matches()) {
                            fc6.e("ad_banner_format_native", "Boolean");
                        }
                        if (z2) {
                            pq6 pq6Var6 = new pq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(pq6Var6, "onAdLoaded");
                            new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var6)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                        } else {
                            z3 = true;
                            AdSize adSize6 = AdSize.BANNER;
                            im7.d(adSize6, "bannerAdSize");
                            qq6 qq6Var6 = new qq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(adSize6, "size");
                            im7.e(qq6Var6, "onAdLoaded");
                            AdView adView6 = new AdView(applicationContext);
                            adView6.setAdSize(adSize6);
                            adView6.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                            adView6.setAdListener(new wv6(adView6, adSize6, qq6Var6));
                            builder = new AdRequest.Builder();
                            pl6 pl6VarB8 = pl6.b();
                            im7.d(pl6VarB8, "GDPR.getInstance()");
                            rl6 rl6VarA8 = pl6VarB8.a();
                            im7.d(rl6VarA8, "GDPR.getInstance().consentState");
                            ql6Var = rl6VarA8.a;
                            im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                            if (ql6Var != ql6Var4) {
                                z3 = false;
                            }
                            if (!z3) {
                                Bundle bundle9 = new Bundle();
                                bundle9.putString("npa", "1");
                                builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle9);
                            }
                            AdRequest adRequestBuild8 = builder.build();
                            im7.d(adRequestBuild8, "adRequestBuilder.build()");
                            adView6.loadAd(adRequestBuild8);
                            this.H = adView6;
                        }
                    }
                    z2 = false;
                    if (z2) {
                        pq6 pq6Var7 = new pq6(this);
                        im7.e(applicationContext, "context");
                        im7.e(pq6Var7, "onAdLoaded");
                        new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var7)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                    } else {
                        z3 = true;
                        AdSize adSize7 = AdSize.BANNER;
                        im7.d(adSize7, "bannerAdSize");
                        qq6 qq6Var7 = new qq6(this);
                        im7.e(applicationContext, "context");
                        im7.e(adSize7, "size");
                        im7.e(qq6Var7, "onAdLoaded");
                        AdView adView7 = new AdView(applicationContext);
                        adView7.setAdSize(adSize7);
                        adView7.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                        adView7.setAdListener(new wv6(adView7, adSize7, qq6Var7));
                        builder = new AdRequest.Builder();
                        pl6 pl6VarB9 = pl6.b();
                        im7.d(pl6VarB9, "GDPR.getInstance()");
                        rl6 rl6VarA9 = pl6VarB9.a();
                        im7.d(rl6VarA9, "GDPR.getInstance().consentState");
                        ql6Var = rl6VarA9.a;
                        im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                        if (ql6Var != ql6Var4) {
                            z3 = false;
                        }
                        if (!z3) {
                            Bundle bundle10 = new Bundle();
                            bundle10.putString("npa", "1");
                            builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle10);
                        }
                        AdRequest adRequestBuild9 = builder.build();
                        im7.d(adRequestBuild9, "adRequestBuilder.build()");
                        adView7.loadAd(adRequestBuild9);
                        this.H = adView7;
                    }
                } else {
                    if (fc6.e.matcher(strD).matches()) {
                        fc6Var.a("ad_banner_format_native", fc6.b(fc6Var.c));
                    } else {
                        if (fc6.f.matcher(strD).matches()) {
                            fc6Var.a("ad_banner_format_native", fc6.b(fc6Var.c));
                        } else {
                            strD2 = fc6.d(fc6Var.d, "ad_banner_format_native");
                            if (strD2 == null) {
                                fc6.e("ad_banner_format_native", "Boolean");
                            } else {
                                if (fc6.e.matcher(strD2).matches()) {
                                    if (!fc6.f.matcher(strD2).matches()) {
                                        fc6.e("ad_banner_format_native", "Boolean");
                                    }
                                }
                                if (z2) {
                                    pq6 pq6Var8 = new pq6(this);
                                    im7.e(applicationContext, "context");
                                    im7.e(pq6Var8, "onAdLoaded");
                                    new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var8)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                                } else {
                                    z3 = true;
                                    AdSize adSize8 = AdSize.BANNER;
                                    im7.d(adSize8, "bannerAdSize");
                                    qq6 qq6Var8 = new qq6(this);
                                    im7.e(applicationContext, "context");
                                    im7.e(adSize8, "size");
                                    im7.e(qq6Var8, "onAdLoaded");
                                    AdView adView8 = new AdView(applicationContext);
                                    adView8.setAdSize(adSize8);
                                    adView8.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                                    adView8.setAdListener(new wv6(adView8, adSize8, qq6Var8));
                                    builder = new AdRequest.Builder();
                                    pl6 pl6VarB10 = pl6.b();
                                    im7.d(pl6VarB10, "GDPR.getInstance()");
                                    rl6 rl6VarA10 = pl6VarB10.a();
                                    im7.d(rl6VarA10, "GDPR.getInstance().consentState");
                                    ql6Var = rl6VarA10.a;
                                    im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                                    if (ql6Var != ql6Var4) {
                                        z3 = false;
                                    }
                                    if (!z3) {
                                        Bundle bundle11 = new Bundle();
                                        bundle11.putString("npa", "1");
                                        builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle11);
                                    }
                                    AdRequest adRequestBuild10 = builder.build();
                                    im7.d(adRequestBuild10, "adRequestBuilder.build()");
                                    adView8.loadAd(adRequestBuild10);
                                    this.H = adView8;
                                }
                            }
                        }
                        z2 = false;
                        if (z2) {
                            pq6 pq6Var9 = new pq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(pq6Var9, "onAdLoaded");
                            new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var9)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                        } else {
                            z3 = true;
                            AdSize adSize9 = AdSize.BANNER;
                            im7.d(adSize9, "bannerAdSize");
                            qq6 qq6Var9 = new qq6(this);
                            im7.e(applicationContext, "context");
                            im7.e(adSize9, "size");
                            im7.e(qq6Var9, "onAdLoaded");
                            AdView adView9 = new AdView(applicationContext);
                            adView9.setAdSize(adSize9);
                            adView9.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                            adView9.setAdListener(new wv6(adView9, adSize9, qq6Var9));
                            builder = new AdRequest.Builder();
                            pl6 pl6VarB11 = pl6.b();
                            im7.d(pl6VarB11, "GDPR.getInstance()");
                            rl6 rl6VarA11 = pl6VarB11.a();
                            im7.d(rl6VarA11, "GDPR.getInstance().consentState");
                            ql6Var = rl6VarA11.a;
                            im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                            if (ql6Var != ql6Var4) {
                                z3 = false;
                            }
                            if (!z3) {
                                Bundle bundle12 = new Bundle();
                                bundle12.putString("npa", "1");
                                builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle12);
                            }
                            AdRequest adRequestBuild11 = builder.build();
                            im7.d(adRequestBuild11, "adRequestBuilder.build()");
                            adView9.loadAd(adRequestBuild11);
                            this.H = adView9;
                        }
                    }
                    z2 = true;
                    if (z2) {
                        pq6 pq6Var10 = new pq6(this);
                        im7.e(applicationContext, "context");
                        im7.e(pq6Var10, "onAdLoaded");
                        new AdLoader.Builder(applicationContext, "ca-app-pub-6875289151086704/2840529951").forNativeAd(new xv6(pq6Var10)).withAdListener(new yv6()).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(new AdRequest.Builder().build());
                    } else {
                        z3 = true;
                        AdSize adSize10 = AdSize.BANNER;
                        im7.d(adSize10, "bannerAdSize");
                        qq6 qq6Var10 = new qq6(this);
                        im7.e(applicationContext, "context");
                        im7.e(adSize10, "size");
                        im7.e(qq6Var10, "onAdLoaded");
                        AdView adView10 = new AdView(applicationContext);
                        adView10.setAdSize(adSize10);
                        adView10.setAdUnitId("ca-app-pub-6875289151086704/8488581109");
                        adView10.setAdListener(new wv6(adView10, adSize10, qq6Var10));
                        builder = new AdRequest.Builder();
                        pl6 pl6VarB12 = pl6.b();
                        im7.d(pl6VarB12, "GDPR.getInstance()");
                        rl6 rl6VarA12 = pl6VarB12.a();
                        im7.d(rl6VarA12, "GDPR.getInstance().consentState");
                        ql6Var = rl6VarA12.a;
                        im7.d(ql6Var, "GDPR.getInstance().consentState.consent");
                        if (ql6Var != ql6Var4) {
                            z3 = false;
                        }
                        if (!z3) {
                            Bundle bundle13 = new Bundle();
                            bundle13.putString("npa", "1");
                            builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle13);
                        }
                        AdRequest adRequestBuild12 = builder.build();
                        im7.d(adRequestBuild12, "adRequestBuilder.build()");
                        adView10.loadAd(adRequestBuild12);
                        this.H = adView10;
                    }
                }
            }
        }
        View view = viewInflate;
        this.q = view;
        return view;
    }