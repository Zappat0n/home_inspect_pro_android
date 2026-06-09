package com.homeinspectpro

import android.content.Intent
import android.graphics.Bitmap
import dev.hotwire.core.bridge.BridgeComponent
import dev.hotwire.core.bridge.BridgeComponentFactory
import dev.hotwire.core.bridge.BridgeDelegate
import dev.hotwire.core.bridge.BridgeDestination
import dev.hotwire.core.bridge.KotlinXJsonConverter
import dev.hotwire.core.bridge.Message
import dev.hotwire.core.bridge.Metadata
import dev.hotwire.core.config.Hotwire
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CameraBridgeTest {

    @Test
    fun `NAME constant is camera`() {
        assertEquals("camera", CameraBridge.NAME)
    }

    @Test
    fun `getName returns camera`() {
        val delegate = createDelegate()
        val bridge = CameraBridge("camera", delegate)
        assertEquals("camera", bridge.name)
    }

    @Test
    fun `onReceive with capture event invokes startCameraCallback`() {
        val delegate = createDelegate()
        val bridge = CameraBridge("camera", delegate)
        var capturedIntent: Intent? = null
        bridge.setStartCameraCallback { intent ->
            capturedIntent = intent
        }
        val message = Message(
            id = "1",
            component = "camera",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        assertNotNull(capturedIntent)
        assertEquals(
            "android.media.action.IMAGE_CAPTURE",
            capturedIntent?.action
        )
    }

    @Test
    fun `onReceive with capture and no callback does not invoke callback`() {
        val delegate = createDelegate()
        val bridge = CameraBridge("camera", delegate)
        var callbackInvoked = false
        // Do NOT set callback
        val message = Message(
            id = "1",
            component = "camera",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        assertFalse(callbackInvoked)
    }

    @Test
    fun `onReceive with unknown event does nothing`() {
        val delegate = createDelegate()
        val bridge = CameraBridge("camera", delegate)
        var callbackInvoked = false
        bridge.setStartCameraCallback { callbackInvoked = true }
        val message = Message(
            id = "1",
            component = "camera",
            event = "unknown",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        assertFalse(callbackInvoked)
    }

    @Test
    fun `handleCameraResult with bitmap produces reply`() {
        val delegate = createDelegate()
        val bridge = CameraBridge("camera", delegate)
        bridge.setStartCameraCallback { }
        val message = Message(
            id = "2",
            component = "camera",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )
        bridge.onReceive(message)

        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        bridge.handleCameraResult(bitmap)

        // Verify state is cleared after handling
        bridge.onReceive(
            Message(
                id = "3",
                component = "camera",
                event = "capture",
                metadata = Metadata("{}"),
                jsonData = "{}"
            )
        )
        bridge.handleCameraResult(null)

        // Both replies should go through the delegate
    }

    @Test
    fun `handleCameraResult with null bitmap produces error reply`() {
        val delegate = createDelegate()
        val bridge = CameraBridge("camera", delegate)
        bridge.setStartCameraCallback { }
        val message = Message(
            id = "3",
            component = "camera",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )
        bridge.onReceive(message)

        bridge.handleCameraResult(null)

        // Verify pending message is cleared
        var callbackInvoked = false
        bridge.setStartCameraCallback { callbackInvoked = true }
        bridge.onReceive(
            Message(
                id = "4",
                component = "camera",
                event = "capture",
                metadata = Metadata("{}"),
                jsonData = "{}"
            )
        )
        assertTrue(callbackInvoked)
    }
}

private fun createDelegate(): BridgeDelegate<BridgeDestination> {
    Hotwire.config.jsonConverter = KotlinXJsonConverter()
    return BridgeDelegate(
        location = "test",
        destination = object : BridgeDestination {
            override fun bridgeWebViewIsReady(): Boolean = true
        },
        componentFactories = listOf(
            BridgeComponentFactory("camera") { name, del ->
                CameraBridge(name, del)
            }
        )
    )
}
