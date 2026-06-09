package com.homeinspectpro

import android.app.Application
import dev.hotwire.core.bridge.BridgeComponentFactory
import dev.hotwire.core.config.Hotwire
import dev.hotwire.core.turbo.config.PathConfiguration

class HomeInspectProApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Hotwire.config.pathConfiguration.load(
            applicationContext,
            PathConfiguration.Location(
                assetFilePath = "json/path-configuration.json"
            ),
            PathConfiguration.LoaderOptions()
        )

        Hotwire.config.applicationUserAgentPrefix = "HomeInspectPro;"

        Hotwire.config.registeredBridgeComponentFactories = listOf(
            BridgeComponentFactory("camera", ::CameraBridge)
        )
    }
}
