    private void subscribe(String str, String str2, String[] strArr) throws Throwable {
        loadDataSet();
        String strN = jo.n(USER_FEED, str);
        if (this.dataSet.feeds.containsKey(strN)) {
            return;
        }
        this.dataSet.feeds.put(strN, new RssFeed(strN, str2));
        saveDataSet();
        if (strArr == null || strArr.length == 0) {
            return;
        }
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = getCategoryUid(strArr[i]);
        }
        subscriptionAddLabel(strN, strArr);
    }