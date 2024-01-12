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
import com.zenasoft.ami.service.infoextractor.model.MusicInfoWithRequest
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeneralAiPoweredInfoExtractorImpl : KoinComponent, AbstractAiPoweredInfoExtractor() {

    private val logger = KotlinLogging.logger { }

    private val ctx: AmiContext by inject()

    private val jsonKSerializer: Json by inject()

    private val perplexityApiClient: IPerplexityApiClient by inject()

    override suspend fun extractMusicInfo(url: String, query: String): MusicInfoWithRequest {
        val apiRequest = Request(
            model = ModelEnum.PPLX_7B_ONLINE,
            messages = buildInitialMessages(url, query),
        )
        val resp = perplexityApiClient.getCompletion(apiRequest)

        if (resp.choices.isEmpty()) {
            throw AmiException.of(AmiErrorCode.AMI_S002_001, "`choices` is empty")
        }
        val originalContent = resp.choices[0].message.content

        // Get a clean deserializable JSON.
        var musicInfoJson = originalContent.trim()
        // Ignore any '\n'.
        musicInfoJson = musicInfoJson.replace("\n", "")
        // Remove leading and trailing "```" if there is any.
        if (musicInfoJson.startsWith("```json")) {
            musicInfoJson = musicInfoJson.substring(7, musicInfoJson.length)
        } else if (musicInfoJson.startsWith("```")) {
            musicInfoJson = musicInfoJson.substring(3, musicInfoJson.length)
        }
        if (musicInfoJson.endsWith("```")) {
            musicInfoJson = musicInfoJson.substring(0, musicInfoJson.length - 3)
        }

        try {
            val musicInfo = jsonKSerializer.decodeFromString(MusicInfo.serializer(), musicInfoJson)
            return MusicInfoWithRequest(url, query, musicInfo)
        } catch (e: Exception) {
            throw AmiException.of(e, AmiErrorCode.AMI_S002_002, "originalContent: $originalContent")
        }
    }

    override fun buildInitialMessages(url: String, query: String): List<Message> {
        val initialMessages = mutableListOf<Message>()
        initialMessages.add(
            Message(
                role = RoleEnum.SYSTEM,
                content =
                "Follow the user's instruction carefully. You'll receive a URL and a query. Extract the info according to user's query and only output in JSON format that the user gives. Don't output anything other than the JSON struct.",
            )
        )
        initialMessages.add(
            Message(
                role = RoleEnum.USER,
                content =
                """- Requirement: Only output in this JSON format. Don't output anything other than the JSON struct. Fetch info only from the given URL. Don't use other sources. The JSON struct is defined as follows:
```
{
  "artists":${'$'}{artist,list},
  "title":${'$'}{title},
  "album":${'$'}{album},
  "date":${'$'}{date,YYYY-MM-DD},
  "genre":${'$'}{genre},
  "trackNo":${'$'}{trackNo},
  "composers":${'$'}{composer,list},
  "lyricists":${'$'}{lyricist,list},
  "arrangers":${'$'}{arranger,list},
  "confidence":${'$'}{confidenceEnum,oneOf["LOW","HIGH"]},
  "lowConfidenceReason":${'$'}{lowConfidenceReason}
}
```
Fill the info you extracted from the URL into the placeholders. If a field is marked with ",list", use a JSON list to express it, with each String element (artist, composer, lyricist, etc.) separated with a comma. These fields can be a list containing only one element if there actually is only one. `artists` and `title` are required and they're usually provided in the user's query, but use the info on the page if the actual values are different from the ones the user provides. Other fields are optional. If the page **doesn't explicitly** include info for these fields, they should be left not present (as if missing), instead of using null or strings like "unknown" and so on. For example, if the page explicitly mentions "Lyricist" or "Lyrics writer" in whatever language, the `lyricists` field must be filled. Same for the `arrangers` field. Otherwise, if the page only says "Writers" and doesn't explicitly differentiates the lyricist or the arranger, they should be left not present. The `confidence` field is special. It's not extracted from the URL but is a measure of how credible the extraction went. It's either "LOW" or "HIGH" and it should be "LOW" when it's hard to tell the fields from the page or the title doesn't match. Pay attention to the query when the query contains a specific version (like "Acoustic version", "Dance ver.", etc, as it might be a different track than the original version, involving different composers or arrangers etc, and might be included in a different version of album. If the provided URL doesn't contain info about the specific version, use info of the closest one and mark it with "LOW" confidence and fill in the `lowConfidenceReason` if the confidence is "LOW". When extracting info about the composers, lyricists and arrangers, include the full names of the persons. The full name of each person might be found when it first appears on the page. Remember, don't output anything other than the filled JSON struct, even if the info is not extractable. The response should start with "```json" and end with "```".
- URL: $url
- Query: $query""",
            )
        )

        return initialMessages
    }

}