    @Override // com.noinnion.android.reader.ui.BaseDialogActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        A(bundle, true);
        setContentView(R.layout.music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            s().x(toolbar);
            t().t(R.string.media_now_playing);
            t().o(true);
        }
        this.J = (TextView) findViewById(R.id.media_title);
        this.K = (TextView) findViewById(R.id.play_time);
        this.L = (TextView) findViewById(R.id.duration);
        this.y = (ImageView) findViewById(R.id.play_button);
        this.z = (ImageView) findViewById(R.id.forward_button);
        this.A = (ImageView) findViewById(R.id.rewind_button);
        this.B = (Button) findViewById(R.id.stop_button);
        this.C = (Button) findViewById(R.id.close_button);
        this.y.setOnClickListener(this);
        this.z.setOnClickListener(this);
        this.A.setOnClickListener(this);
        this.B.setOnClickListener(this);
        this.C.setOnClickListener(this);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        this.D = seekBar;
        seekBar.setOnSeekBarChangeListener(new wr6(this));
        bindService(new Intent(this, (Class<?>) MusicService.class), this.G, 1);
    }