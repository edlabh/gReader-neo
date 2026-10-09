    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem menuItemFindItem = menu.findItem(R.id.menu_action);
        if (menuItemFindItem != null) {
            Context applicationContext = getApplicationContext();
            View actionView = menuItemFindItem.getActionView();
            SwitchCompat switchCompat = (SwitchCompat) actionView.findViewById(R.id.switch_unread);
            switchCompat.setChecked(!on6.g(applicationContext).e);
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: tp6
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    this.a.Q();
                }
            });
            View viewFindViewById = actionView.findViewById(R.id.sync_button);
            this.D = viewFindViewById;
            if (viewFindViewById != null) {
                viewFindViewById.setOnClickListener(this);
                this.D.setOnLongClickListener(this.N);
            }
            View viewFindViewById2 = actionView.findViewById(R.id.sync_cancel_button);
            this.E = viewFindViewById2;
            if (viewFindViewById2 != null) {
                viewFindViewById2.setOnClickListener(this);
            }
            View viewFindViewById3 = actionView.findViewById(R.id.mark_read_button);
            if (viewFindViewById3 != null) {
                if (iw6.j(applicationContext) == 0) {
                    viewFindViewById3.setVisibility(0);
                    viewFindViewById3.setOnClickListener(this);
                    viewFindViewById3.setOnLongClickListener(this.N);
                } else {
                    viewFindViewById3.setVisibility(8);
                }
            }
            if (on6.b) {
                S(true);
            }
        }
        this.y.setMenuItem(menu.findItem(R.id.menu_search));
        R();
        return true;
    }