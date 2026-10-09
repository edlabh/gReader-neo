    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        im7.e(menu, "menu");
        getMenuInflater().inflate(R.menu.item, menu);
        MenuItem menuItemFindItem = menu.findItem(R.id.menu_item_info);
        im7.d(menuItemFindItem, NSRSS20.ITEM);
        View actionView = menuItemFindItem.getActionView();
        TextView textView = (TextView) actionView.findViewById(R.id.cache_view);
        this.z = textView;
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        TextView textView2 = (TextView) actionView.findViewById(R.id.web_view);
        this.y = textView2;
        if (textView2 != null) {
            textView2.setOnClickListener(this);
        }
        TextView textView3 = (TextView) actionView.findViewById(R.id.feed_view);
        this.x = textView3;
        if (textView3 == null) {
            return true;
        }
        textView3.setOnClickListener(this);
        return true;
    }