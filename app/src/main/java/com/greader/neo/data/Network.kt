package com.greader.neo.data

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/** Cliente HTTP compartilhado (Feedly + extração de artigos + imagens). */
val sharedHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}
