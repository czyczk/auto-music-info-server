package com.zenasoft.ami.service.pagefetcher

interface IPageFetcherService {

    suspend fun getGoogleHomepage(): String

}