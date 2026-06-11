package com.homeinspectpro

import dev.hotwire.core.config.Hotwire
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeInspectProApplicationTest {

    @Test
    fun `application class is registered`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        assertNotNull(activity.application)
        assertTrue(activity.application is HomeInspectProApplication)
    }

    @Test
    fun `hotwire user agent prefix is set`() {
        Robolectric.buildActivity(MainActivity::class.java).setup().get()
        assertEquals("HomeInspectPro;", Hotwire.config.applicationUserAgentPrefix)
    }

    @Test
    fun `camera bridge component is registered`() {
        Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val factories = Hotwire.config.registeredBridgeComponentFactories
        val cameraFactory = factories.find { it.name == "camera" }
        assertNotNull("CameraBridge should be registered", cameraFactory)
    }

    @Test
    fun `path configuration is loaded`() {
        Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val pathConfig = Hotwire.config.pathConfiguration
        assertNotNull("Path configuration should be loaded", pathConfig)
    }
}
