package com.zenasoft.ami.integration.perplexityapi.model

import com.zenasoft.ami.TestBase
import com.zenasoft.ami.integration.perplexityapi.model.dto.toDto
import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum
import com.zenasoft.ami.integration.perplexityapi.model.type.RoleEnum
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PerplexityApiModelSerializationTest : TestBase() {

    private val json: Json by inject(named("perplexityApiJsonKSerializer"))

    @Test
    fun testSerializeRequest() {
        run {
            val request = Request(
                model = ModelEnum.PPLX_70B_ONLINE,
                messages = listOf(
                    Message(
                        role = RoleEnum.SYSTEM,
                        content = "system msg"
                    ),
                    Message(
                        role = RoleEnum.USER,
                        content = "user msg"
                    ),
                )
            )

            val requestDto = request.toDto()
            val jsonStr = json.encodeToString(requestDto)
            Assertions.assertEquals(
                """{"model":"pplx-70b-online","messages":[{"role":"system","content":"system msg"},{"role":"user","content":"user msg"}]}""",
                jsonStr
            )
        }
    }

}
