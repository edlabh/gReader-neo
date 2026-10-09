    public final int m(Reader reader, rn6.a aVar) throws IOException {
        ap apVarA = new wo().a(reader);
        ArrayList arrayList = new ArrayList();
        apVarA.g();
        while (apVarA.g() != bp.END_OBJECT) {
            String strB = apVarA.b();
            if (strB != null) {
                apVarA.g();
                if (strB.equals("ids")) {
                    while (apVarA.g() != bp.END_ARRAY) {
                        String strE = apVarA.e();
                        if (strE != null) {
                            arrayList.add(strE);
                        }
                    }
                    aVar.a(arrayList);
                } else {
                    apVarA.i();
                }
            }
        }
        return arrayList.size();
    }