package com.shinonometn.fx.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.shinonometn.fx.JsonUtils
import com.shinonometn.fx.app.FxApp
import com.shinonometn.fx.asValue
import javafx.stage.Stage
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates

class FxAppContextImpl(internal val rootStage: Stage, val app: FxApp) {

    val stage = rootStage

    /**
     * Application configuration file path
     * */
    var configurationFile = File("./settings.json")
        private set

    /**
     * Set the configuration should persistent when app exit
     * */
    var persistentSettingsOnExit by Delegates.observable(false) { _, _, new ->
        if (new) onEnableConfigPersistent() else onDisableConfigPersistent()
    }

    private fun onDisableConfigPersistent() {
        app.removeExitAction(saveConfigurationOnExitAction)
    }

    private fun onEnableConfigPersistent() {
        app.addExitAction(saveConfigurationOnExitAction)
    }

    private val saveConfigurationOnExitAction = { saveSettings() }

    /**
     * The current application configuration storage
     * */
    private val configurations = HashMap<String, AppSettingBean>()

    /**
     * Application configuration snapshot on start up
     * */
    private var appSettingTree: JsonNode? = null

    init {
        configurationFile.takeIf { it.exists() }?.let {
            appSettingTree = JsonUtils.toJsonTree(it)
        }

        app.addExitAction {
            saveSettings()
        }
    }

    /*
    *
    *
    * */

    fun getConfiguration(name: String): AppSettingBean? {
        return configurations[name]
    }

    fun saveSettings() {
        val destFile = configurationFile
        val map = configurations.entries.map { it.key to it.value.properties }.toMap()
        JsonUtils.writeValue(FileOutputStream(destFile), map)
    }

    fun registerSetting(appSettingBean: AppSettingBean) {
        appSettingTree?.let {
            it[appSettingBean.name]
        }?.let {
            val type = object : TypeReference<Map<String, Any>>() {}
            appSettingBean.properties.putAll(it.asValue(type))
        }

        configurations[appSettingBean.name] = appSettingBean
    }
}