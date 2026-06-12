package com.homeinspectpro

import android.app.AlertDialog
import android.widget.LinearLayout
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
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
class SignatureBridgeTest {

    @Test
    fun `NAME constant is signature`() {
        assertEquals("signature", SignatureBridge.NAME)
    }

    @Test
    fun `getName returns signature`() {
        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)
        assertEquals("signature", bridge.name)
    }

    @Test
    fun `onReceive with capture event shows dialog`() {
        Robolectric.buildActivity(MainActivity::class.java).setup()

        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)

        val message = Message(
            id = "1",
            component = "signature",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertNotNull("AlertDialog should be shown", dialog)
    }

    @Test
    fun `onReceive with unknown event does nothing`() {
        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)
        var callbackInvoked = false

        val message = Message(
            id = "1",
            component = "signature",
            event = "unknown",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        assertFalse(callbackInvoked)
    }

    @Test
    fun `onReceive with clear event does not show dialog`() {
        ShadowAlertDialog.reset()

        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)

        val message = Message(
            id = "1",
            component = "signature",
            event = "clear",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        assertNull(ShadowAlertDialog.getLatestAlertDialog())
    }

    @Test
    fun `clear event does not crash without prior capture`() {
        Robolectric.buildActivity(MainActivity::class.java).setup()

        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)

        val message = Message(
            id = "1",
            component = "signature",
            event = "clear",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        assertNull(ShadowAlertDialog.getLatestAlertDialog())
    }

    @Test
    fun `dialog has Signature title`() {
        Robolectric.buildActivity(MainActivity::class.java).setup()

        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)

        val message = Message(
            id = "1",
            component = "signature",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertNotNull(dialog)
        assertEquals("Signature", shadowOf(dialog).title?.toString())
    }

    @Test
    fun `dialog has Done positive button`() {
        Robolectric.buildActivity(MainActivity::class.java).setup()

        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)

        val message = Message(
            id = "1",
            component = "signature",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertNotNull(dialog)
        assertEquals("Done", dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.text?.toString())
    }

    @Test
    fun `dialog has Clear negative button`() {
        Robolectric.buildActivity(MainActivity::class.java).setup()

        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)

        val message = Message(
            id = "1",
            component = "signature",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertNotNull(dialog)
        assertEquals("Clear", dialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.text?.toString())
    }

    @Test
    fun `dialog contains a SignatureView`() {
        Robolectric.buildActivity(MainActivity::class.java).setup()

        val delegate = createDelegate()
        val bridge = SignatureBridge("signature", delegate)

        val message = Message(
            id = "1",
            component = "signature",
            event = "capture",
            metadata = Metadata("{}"),
            jsonData = "{}"
        )

        bridge.onReceive(message)

        val dialog = ShadowAlertDialog.getLatestAlertDialog()
        assertNotNull(dialog)
        val customView = shadowOf(dialog).view
        assertTrue(customView is LinearLayout)
        val layout = customView as LinearLayout
        assertEquals(1, layout.childCount)
        assertTrue(layout.getChildAt(0) is SignatureView)
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
            BridgeComponentFactory("signature") { name, del ->
                SignatureBridge(name, del)
            }
        )
    )
}
