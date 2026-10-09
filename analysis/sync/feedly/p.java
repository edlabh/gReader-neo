    public final void p(Reader reader, rn6.d dVar) throws IOException {
        ap apVarA = new wo().a(reader);
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        lp6 lp6Var = null;
        while (apVarA.g() != bp.END_ARRAY) {
            bp bpVar = ((hp) apVarA).f;
            if (bpVar == bp.START_OBJECT) {
                lp6Var = new lp6();
            } else if (bpVar == bp.END_OBJECT) {
                arrayList.add(lp6Var);
                lp6Var = null;
            }
            String strB = apVarA.b();
            if (strB != null) {
                apVarA.g();
                if (strB.equals("id")) {
                    String strE = apVarA.e();
                    lp6Var.f = strE;
                    lp6Var.g = 10;
                    if (strE.endsWith("tag/global.saved")) {
                        lp6Var.i = "tag/global.saved";
                        lp6Var.g = 1;
                        z = true;
                    }
                } else if (strB.equals("label")) {
                    lp6Var.i = apVarA.e();
                } else {
                    apVarA.i();
                }
            }
        }
        if (!z) {
            try {
                lp6 lp6Var2 = new lp6();
                lp6Var2.f = getTagUid("global.saved");
                lp6Var2.i = "tag/global.saved";
                lp6Var2.g = 1;
                arrayList.add(lp6Var2);
            } catch (gw6 unused) {
            }
        }
        try {
            ((ep6.a) dVar).a(arrayList);
        } catch (gw6 e) {
            e.printStackTrace();
        }
    }