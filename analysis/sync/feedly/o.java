    public final void o(Reader reader, rn6.c cVar, rn6.d dVar) throws gw6, IOException {
        ap apVarA = new wo().a(reader);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ip6 ip6Var = null;
        lp6 lp6Var = null;
        while (apVarA.g() != bp.END_ARRAY) {
            hp hpVar = (hp) apVarA;
            bp bpVar = hpVar.f;
            if (bpVar == bp.START_OBJECT) {
                ip6Var = new ip6();
            } else if (bpVar == bp.END_OBJECT) {
                arrayList.add(ip6Var);
                ip6Var = null;
            }
            String strB = apVarA.b();
            if (strB != null) {
                apVarA.g();
                if (strB.equals("id")) {
                    ip6Var.f = apVarA.e();
                } else if (strB.equals("title")) {
                    ip6Var.h = s(apVarA.e());
                } else if (strB.equals("sortid")) {
                    ip6Var.g = apVarA.e();
                } else if (strB.equals("website")) {
                    ip6Var.j = apVarA.e();
                } else if (strB.equals("visualUrl")) {
                    ip6Var.k = apVarA.e();
                } else if (strB.equals("updated")) {
                    ip6Var.n = apVarA.c() / 1000;
                } else if (strB.equals("categories")) {
                    while (apVarA.g() != bp.END_ARRAY) {
                        bp bpVar2 = hpVar.f;
                        if (bpVar2 == bp.START_OBJECT) {
                            lp6Var = new lp6();
                        } else if (bpVar2 == bp.END_OBJECT) {
                            if (lp6Var != null) {
                                arrayList2.add(lp6Var);
                            }
                            lp6Var = null;
                        }
                        String strB2 = apVarA.b();
                        if (strB2 != null) {
                            apVarA.g();
                            if (strB2.equals("id")) {
                                ip6Var.l.add(apVarA.e());
                                lp6Var.f = apVarA.e();
                                lp6Var.g = 11;
                            } else if (strB2.equals("label")) {
                                lp6Var.i = apVarA.e();
                            } else {
                                apVarA.i();
                            }
                        }
                    }
                } else {
                    apVarA.i();
                }
            }
        }
        ((ep6.a) dVar).a(arrayList2);
        ((ep6.b) cVar).a(arrayList);
    }