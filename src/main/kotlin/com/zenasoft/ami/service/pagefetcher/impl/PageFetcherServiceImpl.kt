package com.zenasoft.ami.service.pagefetcher.impl

import com.zenasoft.ami.service.pagefetcher.IPageFetcherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PageFetcherServiceImpl : KoinComponent, IPageFetcherService {

    private val okHttpClient: OkHttpClient by inject()

    override suspend fun getGoogleHomepage(): String {
        val request = okhttp3.Request.Builder()
            .url("https://www.google.com.hk")
            .build()

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            response.body?.string() ?: ""
        }
    }

}