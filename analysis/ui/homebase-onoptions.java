    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        im7.e(menuItem, NSRSS20.ITEM);
        switch (menuItem.getItemId()) {
            case R.id.menu_buy /* 2131296705 */:
                n56.Z1(this);
                return true;
            case R.id.menu_clear_cache /* 2131296706 */:
                yo6.i(this);
                wb wbVarO = o();
                int i2 = ClearCacheDialog.f;
                try {
                    ClearCacheDialog clearCacheDialog = new ClearCacheDialog();
                    xb xbVar = (xb) wbVarO;
                    Objects.requireNonNull(xbVar);
                    qb qbVar = new qb(xbVar);
                    qbVar.f(0, clearCacheDialog, "fragment_clear_cache", 1);
                    qbVar.d();
                    break;
                } catch (Exception unused) {
                }
                return true;
            case R.id.menu_feedback /* 2131296710 */:
                startActivityForResult(new Intent(this, (Class<?>) WelcomeActivity.class), 2);
                return true;
            case R.id.menu_logout /* 2131296721 */:
                yo6.i(this);
                t75 t75Var = new t75(this);
                t75Var.m(R.string.txt_logout);
                t75Var.g(R.string.msg_confirm_logout);
                t75Var.k(getText(android.R.string.ok), new xp6(this));
                t75Var.i(getText(android.R.string.cancel), yp6.e);
                t75Var.a().show();
                return true;
            case R.id.menu_prefs /* 2131296724 */:
                yo6.i(this);
                ew6 ew6Var = on6.a;
                on6.j = iw6.I(this);
                on6.k = iw6.H(this);
                on6.l = iw6.g(this);
                on6.m = iw6.j(this);
                on6.n = iw6.k(this);
                on6.o = iw6.i(this);
                startActivityForResult(new Intent(this, (Class<?>) SettingsActivity.class), 1);
                return true;
            case R.id.menu_theme /* 2131296727 */:
                Context applicationContext = getApplicationContext();
                boolean zF = iw6.f(applicationContext, "application_theme_auto_dark", true);
                int iE = lw6.e(applicationContext);
                ro.c cVar = new ro.c(this);
                cVar.d = getText(R.string.menu_theme);
                cVar.a(R.menu.dialog_theme);
                cVar.f = new bq6(this, applicationContext, zF, iE);
                ro roVar = new ro(cVar.a, cVar.c);
                roVar.p = cVar;
                im7.d(roVar, "bottomSheet");
                MenuItem menuItemFindItem = roVar.p.b.findItem(R.id.theme_auto_dark);
                im7.d(menuItemFindItem, "bottomSheet.menu.findItem(R.id.theme_auto_dark)");
                menuItemFindItem.setChecked(zF);
                if (iE == 0) {
                    MenuItem menuItemFindItem2 = roVar.p.b.findItem(R.id.theme_default);
                    im7.d(menuItemFindItem2, "bottomSheet.menu.findItem(R.id.theme_default)");
                    menuItemFindItem2.setChecked(true);
                } else if (iE == 1) {
                    MenuItem menuItemFindItem3 = roVar.p.b.findItem(R.id.theme_dark);
                    im7.d(menuItemFindItem3, "bottomSheet.menu.findItem(R.id.theme_dark)");
                    menuItemFindItem3.setChecked(true);
                } else if (iE == 2) {
                    MenuItem menuItemFindItem4 = roVar.p.b.findItem(R.id.theme_black);
                    im7.d(menuItemFindItem4, "bottomSheet.menu.findItem(R.id.theme_black)");
                    menuItemFindItem4.setChecked(true);
                } else if (iE == 3) {
                    MenuItem menuItemFindItem5 = roVar.p.b.findItem(R.id.theme_sepia);
                    im7.d(menuItemFindItem5, "bottomSheet.menu.findItem(R.id.theme_sepia)");
                    menuItemFindItem5.setChecked(true);
                } else if (iE == 5) {
                    MenuItem menuItemFindItem6 = roVar.p.b.findItem(R.id.theme_green);
                    im7.d(menuItemFindItem6, "bottomSheet.menu.findItem(R.id.theme_green)");
                    menuItemFindItem6.setChecked(true);
                }
                roVar.show();
                return true;
            default:
                return false;
        }
    }