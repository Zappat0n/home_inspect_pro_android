package com.homeinspectpro

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import dev.hotwire.core.bridge.BridgeComponent
import dev.hotwire.core.bridge.BridgeDelegate
import dev.hotwire.core.bridge.BridgeDestination
import dev.hotwire.core.bridge.Message
import java.io.ByteArrayOutputStream

class CameraBridge(
    name: String,
    delegate: BridgeDelegate<BridgeDestination>
) : BridgeComponent<BridgeDestination>(name, delegate) {

    private var pendingMessageId: String? = null

    override fun onReceive(message: Message) {
        if (message.event == "capture") {
            pendingMessageId = message.id
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            onStartCamera(intent)
        }
    }

    private var startCameraCallback: ((Intent) -> Unit)? = null

    fun setStartCameraCallback(callback: (Intent) -> Unit) {
        startCameraCallback = callback
    }

    private fun onStartCamera(intent: Intent) {
        startCameraCallback?.invoke(intent)
            ?: replyWithError("Camera callback not set")
    }

    fun handleCameraResult(bitmap: Bitmap?) {
        if (bitmap != null) {
            replyWithImage(bitmap)
        } else {
            replyWithError("No image captured")
        }
    }

    private fun replyWithImage(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        val dataURI = "data:image/jpeg;base64,$base64"

        pendingMessageId?.let {
            replyTo(it, mapOf("image" to dataURI))
        }
        pendingMessageId = null
    }

    private fun replyWithError(error: String) {
        pendingMessageId?.let {
            replyTo(it, mapOf("message" to error))
        }
        pendingMessageId = null
    }

    companion object {
        const val NAME = "camera"
    }
}
