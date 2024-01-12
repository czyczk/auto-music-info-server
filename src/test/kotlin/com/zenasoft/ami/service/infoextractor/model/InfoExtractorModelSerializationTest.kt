package com.zenasoft.ami.service.infoextractor.model

import com.zenasoft.ami.TestBase
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.koin.test.inject

class InfoExtractorModelSerializationTest : TestBase() {

    private val json: Json by inject()

    @Test
    fun testDeserializeMusicInfo() {
        run {
            val jsonStr = """{
  "artist": "Adele",
  "title": "Someone Like You",
  "album": "21",
  "date": "2011-02-04",
  "genre": "Pop",
  "trackNo": 2,
  "composer": "Adele",
  "lyricist": "Adele",
  "arranger": "Adele",
  "confidence": "LOW"
}"""
            val musicInfo = json.decodeFromString(MusicInfo.serializer(), jsonStr)
            Assertions.assertEquals("Adele", musicInfo.artists)
            Assertions.assertEquals("Someone Like You", musicInfo.title)
            Assertions.assertEquals("21", musicInfo.album)
            Assertions.assertEquals("2011-02-04", musicInfo.date)
            Assertions.assertEquals("Pop", musicInfo.genre)
            Assertions.assertEquals(2, musicInfo.trackNo)
            Assertions.assertEquals("Adele", musicInfo.composers)
            Assertions.assertEquals("Adele", musicInfo.lyricists)
            Assertions.assertEquals("Adele", musicInfo.arrangers)
            Assertions.assertEquals("LOW", musicInfo.confidence)
        }

    }

}