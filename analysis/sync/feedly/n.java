    /* JADX WARN: Code duplicated, block: B:234:0x02b4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:240:0x0270 A[SYNTHETIC] */
    public final String n(Reader reader, rn6.b bVar, Set<String> set) throws gw6.a, IOException {
        boolean z;
        ap apVarA = new wo().a(reader);
        cp6 cp6Var = new cp6();
        ArrayList arrayList = new ArrayList();
        apVarA.g();
        String strE = null;
        ap6 ap6Var = null;
        while (apVarA.g() != bp.END_OBJECT) {
            String strB = apVarA.b();
            apVarA.g();
            if ("continuation".equals(strB)) {
                strE = apVarA.e();
            } else if ("items".equals(strB)) {
                while (apVarA.g() != bp.END_ARRAY) {
                    if (this.mRequestStop) {
                        throw new gw6.a();
                    }
                    hp hpVar = (hp) apVarA;
                    bp bpVar = hpVar.f;
                    if (bpVar == bp.START_OBJECT) {
                        ap6Var = new ap6();
                    } else if (bpVar == bp.END_OBJECT) {
                        if (ap6Var != null && ap6Var.f.length() > 0) {
                            arrayList.add(ap6Var);
                        }
                        if (arrayList.size() % 200 == 0) {
                            bVar.a(arrayList);
                            arrayList.clear();
                        }
                        ap6Var = null;
                    }
                    String strB2 = apVarA.b();
                    if (strB2 != null && ap6Var != null) {
                        apVarA.g();
                        if (strB2.equals("id")) {
                            ap6Var.f = s(apVarA.e());
                        } else if (strB2.equals("title")) {
                            ap6Var.h = s(apVarA.e());
                        } else {
                            boolean z2 = false;
                            if (strB2.equals("unread")) {
                                bp bpVar2 = hpVar.f;
                                if (bpVar2 == bp.VALUE_TRUE) {
                                    z2 = true;
                                } else if (bpVar2 != bp.VALUE_FALSE) {
                                    throw new zo(apVarA, String.format("Current token (%s) not of boolean type", bpVar2));
                                }
                                ap6Var.v = !z2;
                            } else if (strB2.equals("categories") || strB2.equals("tags")) {
                                while (apVarA.g() != bp.END_ARRAY) {
                                    String strB3 = apVarA.b();
                                    if (strB3 != null) {
                                        apVarA.g();
                                        if (strB3.equals("id")) {
                                            String strE2 = apVarA.e();
                                            if (strE2 != null) {
                                                if (strE2.endsWith("/global.saved") && strE2.startsWith("user/")) {
                                                    ap6Var.r = true;
                                                } else {
                                                    z = strE2.startsWith("user/") && strE2.contains("/tag/");
                                                    if (z) {
                                                        ap6Var.y.add(strE2);
                                                    }
                                                }
                                                if (z) {
                                                    ap6Var.y.add(strE2);
                                                }
                                            }
                                        } else {
                                            apVarA.i();
                                        }
                                    }
                                }
                            } else if (strB2.equals("published")) {
                                ap6Var.p = apVarA.c() / 1000;
                            } else if (strB2.equals("crawled")) {
                                ap6Var.q = apVarA.c() / 1000;
                            } else if (strB2.equals("alternate")) {
                                while (apVarA.g() != bp.END_ARRAY) {
                                    String strB4 = apVarA.b();
                                    if (strB4 != null) {
                                        apVarA.g();
                                        if (strB4.equals(NSSimpleChapters.HREF)) {
                                            ap6Var.k = apVarA.e();
                                        } else {
                                            apVarA.i();
                                        }
                                    }
                                }
                            } else if (strB2.equals(NSRSS20.ENCLOSURE)) {
                                cp6Var.a();
                                while (apVarA.g() != bp.END_ARRAY) {
                                    bp bpVar3 = hpVar.f;
                                    if (bpVar3 == bp.START_OBJECT) {
                                        cp6Var.a();
                                    } else if (bpVar3 == bp.END_OBJECT) {
                                        ap6Var.i(cp6Var);
                                    }
                                    String strB5 = apVarA.b();
                                    if (strB5 != null) {
                                        apVarA.g();
                                        if (strB5.equals(NSSimpleChapters.HREF)) {
                                            cp6Var.a = apVarA.e();
                                        } else if (strB5.equals(NSRSS20.ENC_TYPE)) {
                                            String strE3 = apVarA.e();
                                            cp6Var.b = strE3;
                                            if (strE3.startsWith(NSRSS20.IMAGE)) {
                                                cp6Var.b = "image/thumbnail";
                                            }
                                        } else {
                                            apVarA.i();
                                        }
                                    }
                                }
                            } else if (strB2.equals(NSContent.NSTAG) || (strB2.equals("summary") && TextUtils.isEmpty(ap6Var.i))) {
                                while (apVarA.g() != bp.END_OBJECT) {
                                    String strB6 = apVarA.b();
                                    if (strB6 != null) {
                                        apVarA.g();
                                        if (strB6.equals(NSContent.NSTAG)) {
                                            ap6Var.i = s(apVarA.e());
                                        } else {
                                            apVarA.i();
                                        }
                                    }
                                }
                            } else if (strB2.equals("visual")) {
                                cp6Var.a();
                                while (apVarA.g() != bp.END_OBJECT) {
                                    String strB7 = apVarA.b();
                                    if (strB7 != null) {
                                        apVarA.g();
                                        if (strB7.equals("url")) {
                                            String strE4 = apVarA.e();
                                            cp6Var.a = strE4;
                                            if (!strE4.equals("none") && cp6Var.a.startsWith("http")) {
                                                cp6Var.b = "image/visual";
                                            }
                                        } else {
                                            apVarA.i();
                                        }
                                    }
                                }
                                ap6Var.i(cp6Var);
                            } else if (strB2.equals("author")) {
                                ap6Var.j = apVarA.e();
                            } else if (strB2.equals("origin")) {
                                while (apVarA.g() != bp.END_OBJECT) {
                                    String strB8 = apVarA.b();
                                    if (strB8 != null) {
                                        apVarA.g();
                                        if (strB8.equals("streamId")) {
                                            String strE5 = apVarA.e();
                                            if (strE5 == null || (set != null && set.contains(strE5))) {
                                                ap6Var.f = "";
                                            } else {
                                                ap6Var.z = strE5;
                                            }
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
                }
                bVar.a(arrayList);
                arrayList.clear();
            } else {
                apVarA.i();
            }
        }
        return strE;
    }