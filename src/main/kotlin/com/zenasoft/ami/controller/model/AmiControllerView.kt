package com.zenasoft.ami.controller.model

import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import kotlinx.serialization.Serializable
import mu.KotlinLogging

@Serializable
class AmiControllerView<T> {

    var data: T? = null

    var error: AmiException? = null

    val isOk: Boolean
        get() = error == null

    companion object {
        private val logger = KotlinLogging.logger { }

        fun <T> ofOk(data: T): AmiControllerView<T> {
            val view = AmiControllerView<T>()
            view.data = data
            return view
        }

        fun <T> ofError(error: Exception): AmiControllerView<T> {
            val view = AmiControllerView<T>()
            if (error is AmiException) {
                view.error = error
            } else {
                view.error = AmiException.of(AmiErrorCode.DEFAULT)
            }
            return view
        }

        fun <T> wrap(func: () -> T): AmiControllerView<T> {
            return try {
                ofOk(func())
            } catch (e: Exception) {
                logger.error(e.stackTraceToString())
                ofError(e)
            }
        }

        suspend fun <T> wrapSuspend(func: suspend () -> T): AmiControllerView<T> {
            return try {
                ofOk(func())
            } catch (e: Exception) {
                logger.error(e.stackTraceToString())
                ofError(e)
            }
        }
    }

}