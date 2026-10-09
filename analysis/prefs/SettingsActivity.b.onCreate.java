        @Override // defpackage.dx6, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FragmentActivity activity = getActivity();
            this.l = lw6.n(activity, iw6.a0(activity));
            this.m = activity.getResources().getBoolean(R$bool.isTablet);
            String string = getArguments().getString("settings");
            String str = "1";
            if ("sync".equals(string)) {
                final FragmentActivity activity2 = getActivity();
                j(R.string.title_synchronization);
                PreferenceScreen preferenceScreenCreatePreferenceScreen = this.e.createPreferenceScreen(activity2);
                PreferenceCategory preferenceCategory = new PreferenceCategory(activity2);
                preferenceCategory.setTitle(R.string.title_synchronization);
                preferenceScreenCreatePreferenceScreen.addPreference(preferenceCategory);
                ListPreference listPreference = new ListPreference(activity2);
                listPreference.setKey("sync_auto_update");
                listPreference.setEntries(R.array.connection_options_labels);
                listPreference.setEntryValues(R.array.connection_options_values);
                listPreference.setDialogTitle(R.string.preference_sync_auto_update_title);
                listPreference.setTitle(R.string.preference_sync_auto_update_title);
                listPreference.setSummary(R.string.preference_sync_auto_update_summary);
                listPreference.setDefaultValue("1");
                final ListPreference listPreference2 = new ListPreference(activity2);
                listPreference2.setKey("sync_interval_hours");
                listPreference2.setEntries(R.array.sync_interval_hours_labels);
                listPreference2.setEntryValues(R.array.sync_interval_hours_values);
                listPreference2.setDialogTitle(R.string.preference_sync_interval_title);
                listPreference2.setTitle(R.string.preference_sync_interval_title);
                listPreference2.setSummary(R.string.preference_sync_interval_summary);
                listPreference2.setDefaultValue("1");
                listPreference2.setEnabled(iw6.e(activity2) != 0);
                listPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: ws6
                    @Override // android.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        SettingsActivity.b bVar = this.a;
                        Objects.requireNonNull(bVar);
                        if (obj.toString().equals("0")) {
                            bVar.n.setEnabled(true);
                        } else {
                            bVar.n.setEnabled(false);
                        }
                        return true;
                    }
                });
                listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: ct6
                    @Override // android.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        SettingsActivity.b bVar = this.a;
                        ListPreference listPreference3 = listPreference2;
                        Context context = activity2;
                        Objects.requireNonNull(bVar);
                        if (obj.toString().equals("0")) {
                            listPreference3.setEnabled(false);
                            bVar.n.setEnabled(false);
                        } else {
                            listPreference3.setEnabled(true);
                            String strF = iw6.F(context, "sync_interval_hours");
                            if (strF == null || strF.length() == 0) {
                                strF = "1";
                            }
                            if (strF.equals("0")) {
                                bVar.n.setEnabled(true);
                            }
                        }
                        return true;
                    }
                });
                preferenceCategory.addPreference(listPreference);
                preferenceCategory.addPreference(listPreference2);
                SeekBarPreference seekBarPreference = new SeekBarPreference(activity2);
                this.n = seekBarPreference;
                seekBarPreference.setKey("sync_custom_interval");
                this.n.setTitle(R.string.preference_sync_custom_interval_title);
                this.n.setSummary(R.string.preference_sync_custom_interval_summary);
                String strF = iw6.F(activity2, "sync_interval_hours");
                if (strF != null && strF.length() != 0) {
                    str = strF;
                }
                if (!str.equals("0") || iw6.e(activity2) == 0) {
                    this.n.setEnabled(false);
                } else {
                    this.n.setEnabled(true);
                }
                preferenceCategory.addPreference(this.n);
                ListPreference listPreference3 = new ListPreference(activity2);
                listPreference3.setKey("sync_item_limit");
                listPreference3.setEntries(R.array.sync_item_limit_labels);
                listPreference3.setEntryValues(R.array.sync_item_limit_values);
                listPreference3.setDialogTitle(R.string.preference_sync_item_limit_title);
                listPreference3.setTitle(R.string.preference_sync_item_limit_title);
                listPreference3.setSummary(R.string.preference_sync_item_limit_summary);
                listPreference3.setDefaultValue("1000");
                preferenceCategory.addPreference(listPreference3);
                CheckBoxPreference checkBoxPreference = new CheckBoxPreference(activity2);
                checkBoxPreference.setKey("sync_include_starred");
                checkBoxPreference.setTitle(R.string.preference_sync_include_starred_title);
                checkBoxPreference.setSummary(R.string.preference_sync_include_starred_summary);
                checkBoxPreference.setDefaultValue(Boolean.FALSE);
                preferenceCategory.addPreference(checkBoxPreference);
                ListPreference listPreference4 = new ListPreference(activity2);
                listPreference4.setKey("sync_on_start");
                listPreference4.setEntries(R.array.connection_options_labels);
                listPreference4.setEntryValues(R.array.connection_options_values);
                listPreference4.setDialogTitle(R.string.preference_sync_on_startup_title);
                listPreference4.setTitle(R.string.preference_sync_on_startup_title);
                listPreference4.setSummary(R.string.preference_sync_on_startup_summary);
                listPreference4.setDefaultValue("0");
                preferenceCategory.addPreference(listPreference4);
                PreferenceCategory preferenceCategory2 = new PreferenceCategory(activity2);
                preferenceCategory2.setTitle(R.string.preference_category_sync_reads);
                preferenceScreenCreatePreferenceScreen.addPreference(preferenceCategory2);
                CheckBoxPreference checkBoxPreference2 = new CheckBoxPreference(activity2);
                checkBoxPreference2.setKey("sync_server_reads");
                checkBoxPreference2.setTitle(R.string.preference_sync_server_reads_title);
                checkBoxPreference2.setSummary(R.string.preference_sync_server_reads_summary);
                checkBoxPreference2.setDefaultValue(Boolean.TRUE);
                preferenceCategory2.addPreference(checkBoxPreference2);
                i(preferenceScreenCreatePreferenceScreen);
                n56.y2(getActivity(), R.string.ga_screen_setting_sync);
                return;
            }
            if ("offline".equals(string)) {
                final FragmentActivity activity3 = getActivity();
                j(R.string.preference_category_offline_reading);
                PreferenceScreen preferenceScreenCreatePreferenceScreen2 = this.e.createPreferenceScreen(activity3);
                PreferenceCategory preferenceCategory3 = new PreferenceCategory(activity3);
                preferenceCategory3.setTitle(R.string.preference_category_offline_reading);
                preferenceScreenCreatePreferenceScreen2.addPreference(preferenceCategory3);
                ListPreference listPreference5 = new ListPreference(activity3);
                listPreference5.setKey("offline_usage");
                listPreference5.setEntries(R.array.connection_options_labels);
                listPreference5.setEntryValues(R.array.connection_options_values);
                listPreference5.setDialogTitle(R.string.preference_offline_usage_title);
                listPreference5.setTitle(R.string.preference_offline_usage_title);
                listPreference5.setSummary(R.string.preference_offline_usage_summary);
                listPreference5.setDefaultValue("0");
                preferenceCategory3.addPreference(listPreference5);
                ListPreference listPreference6 = new ListPreference(activity3);
                listPreference6.setKey("offline_feed_items_limit");
                listPreference6.setEntries(R.array.offline_feed_items_limit_labels);
                listPreference6.setEntryValues(R.array.offline_feed_items_limit_values);
                listPreference6.setDialogTitle(R.string.preference_sync_item_limit_title);
                listPreference6.setTitle(R.string.preference_sync_item_limit_title);
                listPreference6.setSummary(R.string.preference_offline_feed_items_limit_summary);
                listPreference6.setDefaultValue("50");
                preferenceCategory3.addPreference(listPreference6);
                CheckBoxPreference checkBoxPreference3 = new CheckBoxPreference(activity3);
                checkBoxPreference3.setKey("offline_autoload_cache");
                checkBoxPreference3.setTitle(R.string.preference_offline_autoload_cache_title);
                checkBoxPreference3.setSummary(R.string.preference_offline_autoload_cache_summary);
                Boolean bool = Boolean.TRUE;
                checkBoxPreference3.setDefaultValue(bool);
                preferenceCategory3.addPreference(checkBoxPreference3);
                ListPreference listPreference7 = new ListPreference(activity3);
                listPreference7.setKey("offline_autoload_reading_mode");
                listPreference7.setEntries(R.array.offline_auto_readability_labels);
                listPreference7.setEntryValues(R.array.offline_auto_readability_values);
                listPreference7.setDialogTitle(R.string.preference_item_autoload_reading_mode_title);
                listPreference7.setTitle(R.string.preference_item_autoload_reading_mode_title);
                listPreference7.setSummary(R.string.preference_offline_autoload_reading_mode_summary);
                listPreference7.setDefaultValue("0");
                preferenceCategory3.addPreference(listPreference7);
                CheckBoxPreference checkBoxPreference4 = new CheckBoxPreference(activity3);
                checkBoxPreference4.setKey("offline_save_page_on_star");
                checkBoxPreference4.setTitle(R.string.preference_offline_save_page_on_star_title);
                checkBoxPreference4.setSummary(R.string.preference_offline_save_page_on_star_summary);
                Boolean bool2 = Boolean.FALSE;
                checkBoxPreference4.setDefaultValue(bool2);
                preferenceCategory3.addPreference(checkBoxPreference4);
                PreferenceCategory preferenceCategory4 = new PreferenceCategory(activity3);
                preferenceCategory4.setTitle(R.string.preference_category_offline_content);
                preferenceScreenCreatePreferenceScreen2.addPreference(preferenceCategory4);
                CheckBoxPreference checkBoxPreference5 = new CheckBoxPreference(activity3);
                checkBoxPreference5.setKey("offline_load_full_content");
                checkBoxPreference5.setTitle(R.string.preference_offline_load_full_content_title);
                checkBoxPreference5.setSummary(R.string.preference_offline_load_full_content_summary);
                checkBoxPreference5.setDefaultValue(bool);
                preferenceCategory4.addPreference(checkBoxPreference5);
                ListPreference listPreference8 = new ListPreference(activity3);
                listPreference8.setKey("offline_full_content_format");
                listPreference8.setEntries(R.array.offline_full_content_labels);
                listPreference8.setEntryValues(R.array.offline_full_content_values);
                listPreference8.setDialogTitle(R.string.preference_offline_full_content_format_title);
                listPreference8.setTitle(R.string.preference_offline_full_content_format_title);
                listPreference8.setSummary(R.string.preference_offline_full_content_format_summary);
                listPreference8.setDefaultValue(String.valueOf(3));
                preferenceCategory4.addPreference(listPreference8);
                CheckBoxPreference checkBoxPreference6 = new CheckBoxPreference(activity3);
                checkBoxPreference6.setKey("offline_load_images");
                checkBoxPreference6.setTitle(R.string.preference_offline_load_images_title);
                checkBoxPreference6.setSummary(R.string.preference_offline_load_images_summary);
                checkBoxPreference6.setDefaultValue(bool2);
                preferenceCategory4.addPreference(checkBoxPreference6);
                CheckBoxPreference checkBoxPreference7 = new CheckBoxPreference(activity3);
                checkBoxPreference7.setKey("offline_save_data_traffic");
                checkBoxPreference7.setTitle(R.string.preference_offline_save_data_traffic_title);
                checkBoxPreference7.setSummary(R.string.preference_offline_save_data_traffic_summary);
                checkBoxPreference7.setDefaultValue(bool);
                preferenceCategory4.addPreference(checkBoxPreference7);
                final CheckBoxPreference checkBoxPreference8 = new CheckBoxPreference(activity3);
                checkBoxPreference8.setKey("offline_load_audio");
                checkBoxPreference8.setTitle(R.string.preference_offline_load_audio_title);
                checkBoxPreference8.setSummary(R.string.preference_offline_load_audio_summary);
                checkBoxPreference8.setDefaultValue(bool2);
                preferenceCategory4.addPreference(checkBoxPreference8);
                checkBoxPreference8.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: gt6
                    @Override // android.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        SettingsActivity.b bVar = this.a;
                        Context context = activity3;
                        CheckBoxPreference checkBoxPreference9 = checkBoxPreference8;
                        Objects.requireNonNull(bVar);
                        if (!((Boolean) obj).booleanValue() || cn6.d0(context)) {
                            return true;
                        }
                        checkBoxPreference9.setChecked(false);
                        n56.Z1(bVar.getActivity());
                        n56.X1(context, context.getText(R.string.limit_podcast_download));
                        return false;
                    }
                });
                final CheckBoxPreference checkBoxPreference9 = new CheckBoxPreference(activity3);
                checkBoxPreference9.setKey("offline_load_video");
                checkBoxPreference9.setTitle(R.string.preference_offline_load_video_title);
                checkBoxPreference9.setSummary(R.string.preference_offline_load_video_summary);
                checkBoxPreference9.setDefaultValue(bool2);
                preferenceCategory4.addPreference(checkBoxPreference9);
                checkBoxPreference9.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: kt6
                    @Override // android.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        SettingsActivity.b bVar = this.a;
                        Context context = activity3;
                        CheckBoxPreference checkBoxPreference10 = checkBoxPreference9;
                        Objects.requireNonNull(bVar);
                        if (!((Boolean) obj).booleanValue() || cn6.d0(context)) {
                            return true;
                        }
                        checkBoxPreference10.setChecked(false);
                        n56.Z1(bVar.getActivity());
                        n56.X1(context, context.getText(R.string.limit_podcast_download));
                        return false;
                    }
                });
                i(preferenceScreenCreatePreferenceScreen2);
                n56.y2(getActivity(), R.string.ga_screen_setting_offline);
                return;
            }
            if ("notification".equals(string)) {
                j(R.string.preference_category_notification);
                d(R.xml.pref_notification);
                if (!cn6.d0(getActivity())) {
                    PreferenceManager preferenceManager = this.e;
                    CheckBoxPreference checkBoxPreference10 = (CheckBoxPreference) (preferenceManager != null ? preferenceManager.findPreference("sync_notifiable_custom") : null);
                    checkBoxPreference10.setChecked(false);
                    checkBoxPreference10.setEnabled(false);
                }
                n56.y2(getActivity(), R.string.ga_screen_setting_notification);
                return;
            }
            if ("cache".equals(string)) {
                j(R.string.preference_category_cache);
                d(R.xml.pref_cache);
                PreferenceManager preferenceManager2 = this.e;
                ((PreferenceScreen) (preferenceManager2 != null ? preferenceManager2.findPreference("preference_offline_storage") : null)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: mt6
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        wb fragmentManager = this.a.getFragmentManager();
                        try {
                            PrefOfflineStorageDialog prefOfflineStorageDialog = new PrefOfflineStorageDialog();
                            xb xbVar = (xb) fragmentManager;
                            Objects.requireNonNull(xbVar);
                            qb qbVar = new qb(xbVar);
                            qbVar.f(0, prefOfflineStorageDialog, "fragment_pref_offline_storage", 1);
                            qbVar.d();
                        } catch (Exception unused) {
                        }
                        return true;
                    }
                });
                n56.y2(getActivity(), R.string.ga_screen_setting_cache);
                return;
            }
            if ("reading".equals(string)) {
                j(R.string.preference_category_reading);
                d(R.xml.pref_reading);
                PreferenceManager preferenceManager3 = this.e;
                ((PreferenceScreen) (preferenceManager3 == null ? null : preferenceManager3.findPreference("preference_mark_read"))).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: bt6
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        MarkReadPreferenceFragment.j(this.a.getActivity(), null);
                        return true;
                    }
                });
                PreferenceManager preferenceManager4 = this.e;
                ((PreferenceScreen) (preferenceManager4 != null ? preferenceManager4.findPreference("preference_article_controls") : null)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: zs6
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        ArticleControlsPreferenceFragment.j(this.a.getActivity(), null);
                        return true;
                    }
                });
                n56.y2(getActivity(), R.string.ga_screen_setting_reading);
                return;
            }
            if ("look_feel".equals(string)) {
                j(R.string.preference_category_ui);
                d(R.xml.pref_look_and_feel);
                if (this.l) {
                    PreferenceManager preferenceManager5 = this.e;
                    PreferenceCategory preferenceCategory5 = (PreferenceCategory) (preferenceManager5 == null ? null : preferenceManager5.findPreference("preference_category_home_view"));
                    PreferenceManager preferenceManager6 = this.e;
                    preferenceCategory5.removePreference(preferenceManager6 == null ? null : preferenceManager6.findPreference("home_landscape_dual_pane"));
                    PreferenceManager preferenceManager7 = this.e;
                    preferenceCategory5.removePreference(preferenceManager7 == null ? null : preferenceManager7.findPreference("home_hide_article_list"));
                } else {
                    PreferenceManager preferenceManager8 = this.e;
                    PreferenceCategory preferenceCategory6 = (PreferenceCategory) (preferenceManager8 == null ? null : preferenceManager8.findPreference("preference_category_article_view"));
                    PreferenceManager preferenceManager9 = this.e;
                    preferenceCategory6.removePreference(preferenceManager9 == null ? null : preferenceManager9.findPreference("item_nav_bar"));
                }
                if (!this.m) {
                    PreferenceManager preferenceManager10 = this.e;
                    PreferenceCategory preferenceCategory7 = (PreferenceCategory) (preferenceManager10 == null ? null : preferenceManager10.findPreference("preference_category_home_view"));
                    PreferenceManager preferenceManager11 = this.e;
                    preferenceCategory7.removePreference(preferenceManager11 == null ? null : preferenceManager11.findPreference("use_phone_ui"));
                }
                PreferenceManager preferenceManager12 = this.e;
                ((CheckBoxPreference) (preferenceManager12 != null ? preferenceManager12.findPreference("fullscreen") : null)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: ht6
                    @Override // android.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        SettingsActivity.b bVar = this.a;
                        on6.g(bVar.getActivity()).m = !on6.g(bVar.getActivity()).m;
                        n56.x2(bVar.getActivity());
                        return true;
                    }
                });
                n56.y2(getActivity(), R.string.ga_screen_setting_look_feel);
                return;
            }
            if ("rich_list".equals(string)) {
                j(R.string.preference_rich_article_list);
                d(R.xml.pref_rich_list);
                return;
            }
            if ("quick_actions".equals(string)) {
                j(R.string.preference_item_list_action_title);
                d(R.xml.pref_quick_actions);
                return;
            }
            if (!"services".equals(string)) {
                if ("backup".equals(string)) {
                    j(R.string.preference_category_backup_restore);
                    d(R.xml.pref_backup);
                    PreferenceManager preferenceManager13 = this.e;
                    ((PreferenceScreen) (preferenceManager13 == null ? null : preferenceManager13.findPreference("preference_backup"))).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: ys6
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            wb fragmentManager = this.a.getFragmentManager();
                            try {
                                qt6 qt6Var = new qt6();
                                xb xbVar = (xb) fragmentManager;
                                Objects.requireNonNull(xbVar);
                                qb qbVar = new qb(xbVar);
                                qbVar.f(0, qt6Var, "fragment_pref_backup", 1);
                                qbVar.d();
                            } catch (Exception unused) {
                            }
                            return true;
                        }
                    });
                    PreferenceManager preferenceManager14 = this.e;
                    ((PreferenceScreen) (preferenceManager14 != null ? preferenceManager14.findPreference("preference_restore") : null)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: jt6
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            wb fragmentManager = this.a.getFragmentManager();
                            try {
                                PrefRestoreDialog prefRestoreDialog = new PrefRestoreDialog();
                                xb xbVar = (xb) fragmentManager;
                                Objects.requireNonNull(xbVar);
                                qb qbVar = new qb(xbVar);
                                qbVar.f(0, prefRestoreDialog, "fragment_pref_restore", 1);
                                qbVar.d();
                            } catch (Exception unused) {
                            }
                            return true;
                        }
                    });
                    return;
                }
                if ("other".equals(string)) {
                    j(R.string.preference_category_other);
                    d(R.xml.pref_other);
                    PreferenceManager preferenceManager15 = this.e;
                    ((PreferenceScreen) (preferenceManager15 == null ? null : preferenceManager15.findPreference("preference_feedback"))).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: ft6
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            FragmentActivity activity4 = this.a.getActivity();
                            t75 t75Var = new t75(activity4);
                            t75Var.a.e = activity4.getText(R.string.menu_feedback);
                            t75Var.f(activity4.getResources().getStringArray(R.array.feedback_labels), new in6(activity4));
                            t75Var.a().show();
                            return true;
                        }
                    });
                    PreferenceManager preferenceManager16 = this.e;
                    ((PreferenceScreen) (preferenceManager16 == null ? null : preferenceManager16.findPreference("preference_privacy_policy"))).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: it6
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            SettingsActivity.b bVar = this.a;
                            Objects.requireNonNull(bVar);
                            String strC = jb6.a().c("url_privacy_policy");
                            im7.d(strC, "FirebaseRemoteConfig.get…g.UrlPrivacyPolicy.key())");
                            jw6.a(bVar.getActivity(), strC);
                            return true;
                        }
                    });
                    PreferenceManager preferenceManager17 = this.e;
                    ((PreferenceScreen) (preferenceManager17 != null ? preferenceManager17.findPreference("preference_privacy_settings") : null)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: at6
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            SettingsActivity.b bVar = this.a;
                            if (!(bVar.getActivity() instanceof AppCompatActivity)) {
                                return true;
                            }
                            bw6.b((AppCompatActivity) bVar.getActivity());
                            return true;
                        }
                    });
                    return;
                }
                return;
            }
            final FragmentActivity activity4 = getActivity();
            j(R.string.preference_category_services);
            PreferenceScreen preferenceScreenCreatePreferenceScreen3 = this.e.createPreferenceScreen(activity4);
            PreferenceCategory preferenceCategory8 = new PreferenceCategory(activity4);
            preferenceCategory8.setTitle(R.string.preference_category_services);
            preferenceScreenCreatePreferenceScreen3.addPreference(preferenceCategory8);
            ListPreference listPreference9 = new ListPreference(activity4);
            listPreference9.setKey("service_tts");
            listPreference9.setEntries(R.array.service_tts_labels);
            listPreference9.setEntryValues(R.array.service_tts_values);
            listPreference9.setDialogTitle(R.string.tts_voice_reading);
            listPreference9.setTitle(R.string.tts_voice_reading);
            listPreference9.setSummary(R.string.preference_service_tts_summary);
            listPreference9.setDefaultValue(String.valueOf(2));
            preferenceCategory8.addPreference(listPreference9);
            new CheckBoxPreference(activity4);
            CheckBoxPreference checkBoxPreference11 = new CheckBoxPreference(activity4);
            checkBoxPreference11.setKey("service_translate");
            checkBoxPreference11.setTitle(R.string.menu_translate);
            checkBoxPreference11.setSummary(R.string.preference_service_translate_summary);
            Boolean bool3 = Boolean.TRUE;
            checkBoxPreference11.setDefaultValue(bool3);
            preferenceCategory8.addPreference(checkBoxPreference11);
            PreferenceCategory preferenceCategory9 = new PreferenceCategory(activity4);
            preferenceCategory9.setTitle(R.string.txt_share);
            preferenceScreenCreatePreferenceScreen3.addPreference(preferenceCategory9);
            ListPreference listPreference10 = new ListPreference(activity4);
            listPreference10.setKey("item_send_to");
            listPreference10.setEntries(R.array.item_send_to_labels);
            listPreference10.setEntryValues(R.array.item_send_to_values);
            listPreference10.setDialogTitle(R.string.preference_item_send_to_title);
            listPreference10.setTitle(R.string.preference_item_send_to_title);
            listPreference10.setSummary(R.string.preference_item_send_to_summary);
            listPreference10.setDefaultValue("1");
            preferenceCategory9.addPreference(listPreference10);
            CheckBoxPreference checkBoxPreference12 = new CheckBoxPreference(activity4);
            checkBoxPreference12.setKey("item_internal_share");
            checkBoxPreference12.setTitle(R.string.preference_service_internal_share);
            checkBoxPreference12.setSummary(R.string.preference_service_internal_share_summary);
            Boolean bool4 = Boolean.FALSE;
            checkBoxPreference12.setDefaultValue(bool4);
            preferenceCategory9.addPreference(checkBoxPreference12);
            PreferenceScreen preferenceScreenCreatePreferenceScreen4 = this.e.createPreferenceScreen(activity4);
            preferenceScreenCreatePreferenceScreen4.setTitle(R.string.preference_service_share_clear_default);
            preferenceScreenCreatePreferenceScreen4.setSummary(R.string.preference_service_share_clear_default_summary);
            preferenceScreenCreatePreferenceScreen4.setEnabled(iw6.G(activity4, "item_share_default", null) != null);
            preferenceScreenCreatePreferenceScreen4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: et6
                @Override // android.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    iw6.V(activity4, "item_share_default", null);
                    preference.setEnabled(false);
                    return false;
                }
            });
            preferenceCategory9.addPreference(preferenceScreenCreatePreferenceScreen4);
            CheckBoxPreference checkBoxPreference13 = new CheckBoxPreference(activity4);
            checkBoxPreference13.setKey("service_everclip");
            checkBoxPreference13.setTitle(R.string.service_everclip_title);
            checkBoxPreference13.setSummary(R.string.preference_service_everclip_summary);
            checkBoxPreference13.setDefaultValue(bool3);
            preferenceCategory9.addPreference(checkBoxPreference13);
            PreferenceCategory preferenceCategory10 = new PreferenceCategory(activity4);
            preferenceCategory10.setTitle(R.string.preference_service_social_network);
            preferenceScreenCreatePreferenceScreen3.addPreference(preferenceCategory10);
            CheckBoxPreference checkBoxPreference14 = new CheckBoxPreference(activity4);
            checkBoxPreference14.setKey("service_facebook");
            checkBoxPreference14.setTitle(R.string.service_facebook_title);
            checkBoxPreference14.setSummary(R.string.preference_service_facebook_summary);
            checkBoxPreference14.setDefaultValue(bool3);
            preferenceCategory10.addPreference(checkBoxPreference14);
            CheckBoxPreference checkBoxPreference15 = new CheckBoxPreference(activity4);
            checkBoxPreference15.setKey("service_twitter");
            checkBoxPreference15.setTitle(R.string.service_twitter_title);
            checkBoxPreference15.setSummary(R.string.preference_service_twitter_summary);
            checkBoxPreference15.setDefaultValue(bool3);
            preferenceCategory10.addPreference(checkBoxPreference15);
            PreferenceCategory preferenceCategory11 = new PreferenceCategory(activity4);
            preferenceCategory11.setTitle(R.string.preference_service_bookmarking);
            preferenceScreenCreatePreferenceScreen3.addPreference(preferenceCategory11);
            final CheckBoxPreference checkBoxPreference16 = new CheckBoxPreference(activity4);
            checkBoxPreference16.setKey("service_ril");
            checkBoxPreference16.setTitle(R.string.service_ril_title);
            checkBoxPreference16.setDefaultValue(bool3);
            checkBoxPreference16.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: xs6
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    CheckBoxPreference checkBoxPreference17 = checkBoxPreference16;
                    Context context = activity4;
                    if (((Boolean) obj).booleanValue()) {
                        checkBoxPreference17.setChecked(true);
                    } else {
                        checkBoxPreference17.setChecked(false);
                        if (!TextUtils.isEmpty(iw6.F(context, "service_ril_username"))) {
                            iw6.Y(context);
                            n56.X1(context, ((Object) context.getText(R$string.service_logout_from)) + StringUtils.SPACE + ((Object) context.getText(R$string.service_ril_title)));
                        }
                    }
                    return true;
                }
            });
            preferenceCategory11.addPreference(checkBoxPreference16);
            final CheckBoxPreference checkBoxPreference17 = new CheckBoxPreference(activity4);
            checkBoxPreference17.setKey("service_instapaper");
            checkBoxPreference17.setTitle(R.string.service_instapaper_title);
            checkBoxPreference17.setDefaultValue(bool3);
            checkBoxPreference17.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: dt6
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    CheckBoxPreference checkBoxPreference18 = checkBoxPreference17;
                    Context context = activity4;
                    if (((Boolean) obj).booleanValue()) {
                        checkBoxPreference18.setChecked(true);
                    } else {
                        checkBoxPreference18.setChecked(false);
                        if (!TextUtils.isEmpty(iw6.F(context, "service_instapaper_password"))) {
                            iw6.W(context);
                            n56.X1(context, ((Object) context.getText(R$string.service_logout_from)) + StringUtils.SPACE + ((Object) context.getText(R$string.service_instapaper_title)));
                        }
                    }
                    return true;
                }
            });
            preferenceCategory11.addPreference(checkBoxPreference17);
            PreferenceCategory preferenceCategory12 = new PreferenceCategory(activity4);
            preferenceCategory12.setTitle(R.string.preference_service_mobilizer);
            preferenceScreenCreatePreferenceScreen3.addPreference(preferenceCategory12);
            CheckBoxPreference checkBoxPreference18 = new CheckBoxPreference(activity4);
            checkBoxPreference18.setKey("service_google_mobilizer");
            checkBoxPreference18.setTitle(R.string.service_google_mobilizer_title);
            checkBoxPreference18.setDefaultValue(bool3);
            preferenceCategory12.addPreference(checkBoxPreference18);
            CheckBoxPreference checkBoxPreference19 = new CheckBoxPreference(activity4);
            checkBoxPreference19.setKey("service_readability_mobilizer");
            checkBoxPreference19.setTitle(R.string.service_readability_mobilizer_title);
            checkBoxPreference19.setDefaultValue(bool3);
            preferenceCategory12.addPreference(checkBoxPreference19);
            PreferenceCategory preferenceCategory13 = new PreferenceCategory(activity4);
            preferenceCategory13.setTitle(R.string.preference_service_3rd_party);
            preferenceScreenCreatePreferenceScreen3.addPreference(preferenceCategory13);
            final CheckBoxPreference checkBoxPreference20 = new CheckBoxPreference(activity4);
            checkBoxPreference20.setKey("service_tesla_unread");
            checkBoxPreference20.setTitle(R.string.service_tesla_unread_title);
            checkBoxPreference20.setDefaultValue(bool4);
            checkBoxPreference20.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: lt6
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    CheckBoxPreference checkBoxPreference21 = checkBoxPreference20;
                    Context context = activity4;
                    if (((Boolean) obj).booleanValue()) {
                        checkBoxPreference21.setChecked(true);
                        n56.N1(context, "com.noinnion.android.greader.reader/com.noinnion.android.greader.ui.login.LoginActivity", iw6.K(context));
                    } else {
                        checkBoxPreference21.setChecked(false);
                        try {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("tag", "com.noinnion.android.greader.reader/com.noinnion.android.greader.ui.login.LoginActivity");
                            contentValues.put("count", (Integer) 0);
                            context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);
                        } catch (IllegalArgumentException unused) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });
            preferenceCategory13.addPreference(checkBoxPreference20);
            i(preferenceScreenCreatePreferenceScreen3);
        }