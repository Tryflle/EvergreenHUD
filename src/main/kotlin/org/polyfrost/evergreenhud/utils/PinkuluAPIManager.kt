package org.polyfrost.evergreenhud.utils

import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.events.event.WorldLoadEvent
import org.polyfrost.oneconfig.libs.eventbus.Subscribe
import org.polyfrost.oneconfig.utils.v1.Multithreading
import org.polyfrost.oneconfig.utils.v1.NetworkUtils
import org.polyfrost.oneconfig.api.hypixel.v1.LocrawUtil
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object PinkuluAPIManager {
    private var rawJson: JsonArray? = null
    private var cachedMap: JsonObject? = null
    fun initialize() {
        EventManager.INSTANCE.register(this)
        Multithreading.runAsync {
            try {
                rawJson = NetworkUtils.getJsonElement("https://maps.pinkulu.com/trans-rights-are-human-rights.json").asJsonArray // so true bestie
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Subscribe
    private fun onWorldJoin(event: WorldLoadEvent) {
        cachedMap = null
    }

    fun getMapPool(): String? {
        checkCached()
        if (cachedMap == null) return null
        return when (cachedMap!!.get("pool").asString) {
            "BEDWARS_4TEAMS_FAST" -> "Fast 4 Teams"
            "BEDWARS_4TEAMS_SLOW" -> "Slow 4 Teams"
            "BEDWARS_8TEAMS_FAST" -> "Fast 8 Teams"
            "BEDWARS_8TEAMS_SLOW" -> "Slow 8 Teams"
            "SKYWARS_MEGA" -> "Mega Skywars"
            "SKYWARS_RANKED" -> "Ranked Skywars"
            "SKYWARS_STANDARD" -> "Normal Skywars"
            else -> null
        }
    }

    fun getMapHeight(): Int? {
        checkCached()
        if (cachedMap == null) return null
        return cachedMap!!.get("maxBuild").asInt
    }

    private fun checkCached() {
        if (rawJson == null) return
        try {
            val locraw = LocrawUtil.INSTANCE.locrawInfo
            if (locraw == null || locraw.mapName.isNullOrBlank() || locraw.gameType == null) return
            if (cachedMap == null || (cachedMap!!.get("name").asString != locraw.mapName && cachedMap!!.get("gameType").asString != locraw.gameType.serverName)) {
                cachedMap = rawJson!!.firstOrNull { it.asJsonObject.get("name").asString == locraw.mapName && it.asJsonObject.get("gameType").asString == locraw.gameType.serverName }?.asJsonObject
            }
        } catch (e: Exception) {
            cachedMap = null
        }
    }
}