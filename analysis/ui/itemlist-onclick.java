    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (getActivity() == null) {
            return;
        }
        Context applicationContext = getActivity().getApplicationContext();
        int id = view.getId();
        if (id != R.id.footer) {
            if (id == R.id.left_action || id == R.id.right_action) {
                hw6 hw6VarG = on6.g(applicationContext);
                d(((Long) view.getTag()).longValue(), view.getId() == R.id.left_action ? hw6VarG.q : hw6VarG.r, view);
                return;
            }
            return;
        }
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).G();
        } else if (getActivity() instanceof HomeTabletActivity) {
            ((HomeTabletActivity) getActivity()).G();
        }
    }