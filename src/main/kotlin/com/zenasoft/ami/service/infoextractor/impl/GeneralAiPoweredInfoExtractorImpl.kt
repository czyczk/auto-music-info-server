package com.zenasoft.ami.service.infoextractor.impl

import com.zenasoft.ami.common.AmiContext
import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import com.zenasoft.ami.integration.perplexityapi.IPerplexityApiClient
import com.zenasoft.ami.integration.perplexityapi.model.Message
import com.zenasoft.ami.integration.perplexityapi.model.Request
import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum
import com.zenasoft.ami.integration.perplexityapi.model.type.RoleEnum
import com.zenasoft.ami.service.infoextractor.AbstractAiPoweredInfoExtractor
import com.zenasoft.ami.service.infoextractor.model.MusicInfo
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeneralAiPoweredInfoExtractorImpl : KoinComponent, AbstractAiPoweredInfoExtractor() {

    private val logger = KotlinLogging.logger { }

    private val ctx: AmiContext by inject()

    private val jsonKSerializer: Json by inject()

    private val perplexityApiClient: IPerplexityApiClient by inject()

    override suspend fun extractMusicInfo(url: String, query: String): MusicInfo {
        val apiRequest = Request().also {
            it.model = ModelEnum.PPLX_70B_ONLINE
            it.messages = buildInitialMessages(url, query)
        }
        val resp = perplexityApiClient.getCompletion(apiRequest)

        if (resp.choices.isEmpty()) {
            throw AmiException.of(AmiErrorCode.AMI_S002_001, "`choices` is empty")
        }
        val musicInfoJson = resp.choices[0].message.content

        val musicInfo = jsonKSerializer.decodeFromString(MusicInfo.serializer(), musicInfoJson)
        return musicInfo
    }

    override fun buildInitialMessages(url: String, query: String): List<Message> {
        val initialMessages = mutableListOf<Message>()
        initialMessages.add(Message().also {
            it.role = RoleEnum.SYSTEM
            it.content =
                "Follow the user's instruction carefully. You'll receive a URL and a query. Extract the info according to user's query and only output in JSON format that the user gives. Don't output anything other than the JSON struct."
        })
        initialMessages.add(Message().also {
            it.role = RoleEnum.USER
            it.content =
                """- Requirement: Only output in this JSON format. Don't output anything other than the JSON struct.
```
{
  "artist":${'$'}{artist},
  "title":${'$'}{title},
  "album\\\":${'$'}{album},
  "date":${'$'}{date,YYYY-MM-DD},
  "genre":${'$'}{genre},
  "trackNo":${'$'}{trackNo},
  "composer":${'$'}{composer},
  "lyricist":${'$'}{lyricist},
  "arranger":${'$'}{arranger},
  "confidence":${'$'}{confidenceEnum,oneOf["LOW","HIGH"]}
}
```
Fill the info you extracted from the URL into the placeholders. `artist` and `title` are required and they're usually provided in the user's query, but use the info on the page if the actual values are different from the ones the user provides. Other fields are optional and should be left not present (as if missing) if the page **doesn't explicitly** include the info. For example, if the page explicitly mentions "Lyricist" or "Lyrics writer" in whatever language, the "lyricist" field must be filled. Same for the "arranger" field. Otherwise, if the page only says "Writers" and doesn't explicitly differentiates the lyricist or the arranger, they should be left not present. The `confidence` field is special. It's not extracted from the URL but is a measure of how credible the extraction went. It's either "LOW" or "HIGH" and it should be "LOW" when it's hard to tell the fields from the page or the title doesn't match. Pay attention to the query when the query contains a specific version (like "Acoustic version", "Dance ver.", etc, as it might be a different track than the original version, involving different composers or arrangers etc, and might be included in a different version of album. When extracting info about the composer, lyricist and arranger, include the full name of the person. The full name might be found when they first appears on the page. Remember, don't output anything other than the filled JSON struct.
- URL: $url
- Query: $query"""
        })

        return initialMessages
    }

}