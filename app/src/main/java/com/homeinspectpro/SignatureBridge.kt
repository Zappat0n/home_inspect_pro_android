package com.homeinspectpro

import android.app.AlertDialog
import android.graphics.Bitmap
import android.util.Base64
import android.widget.LinearLayout
import dev.hotwire.core.bridge.BridgeComponent
import dev.hotwire.core.bridge.BridgeDelegate
import dev.hotwire.core.bridge.BridgeDestination
import dev.hotwire.core.bridge.Message
import java.io.ByteArrayOutputStream

class SignatureBridge(
    name: String,
    delegate: BridgeDelegate<BridgeDestination>
) : BridgeComponent<BridgeDestination>(name, delegate) {

    private var pendingMessageId: String? = null

    override fun onReceive(message: Message) {
        when (message.event) {
            "capture" -> {
                pendingMessageId = message.id
                showSignatureDialog()
            }
            "clear" -> {
                replyTo(message.id, mapOf("image" to ""))
            }
        }
    }

    private fun showSignatureDialog() {
        val context = HomeInspectProApplication.instance
        val signatureView = SignatureView(context)
        val padding = 48
        val layout = LinearLayout(context).apply {
            setPadding(padding, padding, padding, padding)
            addView(signatureView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400
            ))
        }

        AlertDialog.Builder(context)
            .setTitle("Signature")
            .setView(layout)
            .setPositiveButton("Done") { _, _ ->
                val bitmap = signatureView.getBitmap()
                replyWithImage(bitmap)
            }
            .setNegativeButton("Clear") { _, _ ->
                signatureView.clear()
            }
            .setOnCancelListener {
                replyWithError("Signature cancelled")
            }
            .show()
    }

    private fun replyWithImage(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        val dataURI = "data:image/png;base64,$base64"

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
        const val NAME = "signature"
    }
}
