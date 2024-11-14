package org.polyfrost.evergreenhud.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.WorldLoadEvent
import org.polyfrost.oneconfig.utils.v1.JsonUtils
import org.polyfrost.oneconfig.utils.v1.Multithreading
import org.polyfrost.oneconfig.utils.v1.NetworkUtils

object PinkuluAPIManager {
    private var rawJson: JsonArray? = null
    private var cachedMap: JsonObject? = null
    fun initialize() {
        Multithreading.submit {
            try {
                rawJson = JsonUtils.PARSER.parse(NetworkUtils.getString("https://maps.pinkulu.com/trans-rights-are-human-rights.json")).asJsonArray // so true bestie
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        eventHandler { _: WorldLoadEvent ->
            cachedMap = null
        }
    }

    fun getMapPool(): String? {
        checkCached()
        return when (cachedMap?.get("pool")?.asString) {
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

    fun getMapHeight(): Int {
        checkCached()
        return cachedMap?.get("maxBuild")?.asInt ?: -1
    }

    private fun checkCached() {
        if (rawJson == null) return
        try {
            val cachedMap = cachedMap
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