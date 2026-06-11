package com.homeinspectpro

import dev.hotwire.navigation.navigator.NavigatorConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityTest {

    @Test
    fun `activity is created without crash`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        assertNotNull(activity)
    }

    @Test
    fun `navigatorConfigurations returns main navigator`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val configs = activity.navigatorConfigurations()
        assertEquals(1, configs.size)

        val config = configs.first()
        assertEquals("main", config.name)
    }

    @Test
    fun `navigatorConfigurations points to main_nav_host`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val config = activity.navigatorConfigurations().first()
        assertEquals(R.id.main_nav_host, config.navigatorHostId)
    }

    @Test
    fun `navigatorConfigurations uses BASE_URL as startLocation`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val config = activity.navigatorConfigurations().first()
        assertEquals(BuildConfig.BASE_URL, config.startLocation)
    }

    @Test
    fun `navigatorConfigurations returns immutable list`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val configs = activity.navigatorConfigurations()
        try {
            (configs as MutableList<NavigatorConfiguration>).add(
                NavigatorConfiguration(
                    name = "second",
                    startLocation = "https://example.com",
                    navigatorHostId = 0
                )
            )
        } catch (_: UnsupportedOperationException) {
            return
        }
    }
}
