    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        im7.e(view, "view");
        int id = view.getId();
        if (id == R.id.getting_started) {
            jw6.a(this, "https://noinnion.com/greader/guide");
        }
        switch (id) {
            case R.id.client_feedly /* 2131296436 */:
                startActivityForResult(new Intent(this, (Class<?>) FeedlyLoginActivity.class), 5);
                break;
            case R.id.client_inoreader /* 2131296437 */:
                startActivityForResult(new Intent(this, (Class<?>) InoreaderLoginActivity.class), 8);
                break;
            case R.id.client_old_reader /* 2131296438 */:
                startActivityForResult(new Intent(this, (Class<?>) OldReaderLoginActivity.class), 6);
                break;
            case R.id.client_rss_reader /* 2131296439 */:
                startActivityForResult(new Intent(this, (Class<?>) RssReaderLoginActivity.class), 7);
                break;
        }
    }