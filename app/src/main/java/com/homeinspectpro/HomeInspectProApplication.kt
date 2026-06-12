package com.homeinspectpro

import android.app.Application
import dev.hotwire.core.bridge.BridgeComponentFactory
import dev.hotwire.core.config.Hotwire
import dev.hotwire.core.turbo.config.PathConfiguration

class HomeInspectProApplication : Application() {
    companion object {
        lateinit var instance: HomeInspectProApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Hotwire.config.pathConfiguration.load(
            applicationContext,
            PathConfiguration.Location(
                assetFilePath = "json/path-configuration.json"
            ),
            PathConfiguration.LoaderOptions()
        )

        Hotwire.config.applicationUserAgentPrefix = "HomeInspectPro;"

        Hotwire.config.registeredBridgeComponentFactories = listOf(
            BridgeComponentFactory("camera", ::CameraBridge),
            BridgeComponentFactory("signature", ::SignatureBridge)
        )
    }
}
