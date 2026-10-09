    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        im7.e(menuItem, NSRSS20.ITEM);
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            this.j.a();
            return false;
        }
        if (itemId != com.noinnion.android.greader.reader.R.id.tts_preferences) {
            return false;
        }
        final c cVar = c.e;
        int i = TextToSpeechPreferenceFragment.l;
        t75 t75Var = new t75(this);
        t75Var.n(getLayoutInflater().inflate(com.noinnion.android.greader.reader.R.layout.text_to_speech_preference, (ViewGroup) null));
        t75Var.d(R.string.ok, null);
        final wb wbVarO = o();
        t75Var.l(com.noinnion.android.greader.reader.R.string.preference_text_to_speech);
        a0 a0VarA = t75Var.a();
        a0VarA.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: nt6
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                wb wbVar = wbVarO;
                DialogInterface.OnDismissListener onDismissListener = cVar;
                int i2 = TextToSpeechPreferenceFragment.l;
                Fragment fragmentB = wbVar.b(com.noinnion.android.greader.reader.R.id.preference_fragment);
                if (fragmentB != null) {
                    qb qbVar = new qb((xb) wbVar);
                    qbVar.q(fragmentB);
                    qbVar.c();
                }
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(dialogInterface);
                }
            }
        });
        a0VarA.show();
        return false;
    }