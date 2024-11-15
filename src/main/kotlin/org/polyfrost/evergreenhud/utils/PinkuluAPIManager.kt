package org.polyfrost.evergreenhud.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.HypixelLocationEvent
import org.polyfrost.oneconfig.utils.v1.JsonUtils
import org.polyfrost.oneconfig.utils.v1.Multithreading
import org.polyfrost.oneconfig.utils.v1.NetworkUtils
import kotlin.jvm.optionals.getOrNull

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
        eventHandler { event: HypixelLocationEvent ->
            cachedMap = null
            val location = event.location
            val mapName = location.mapName.getOrNull() ?: return
            val gameType = location.gameType.getOrNull()?.databaseName ?: return
            cachedMap = rawJson?.firstOrNull {
                if (!it.isJsonObject) return@eventHandler
                val obj = it.asJsonObject
                obj.get("name")?.asString == mapName && obj.get("gameType")?.asString == gameType
            }?.asJsonObject
        }
    }

    fun getMapPool() = when (cachedMap?.get("pool")?.asString) {
        "BEDWARS_4TEAMS_FAST" -> "Fast 4 Teams"
        "BEDWARS_4TEAMS_SLOW" -> "Slow 4 Teams"
        "BEDWARS_8TEAMS_FAST" -> "Fast 8 Teams"
        "BEDWARS_8TEAMS_SLOW" -> "Slow 8 Teams"
        "SKYWARS_MEGA" -> "Mega Skywars"
        "SKYWARS_RANKED" -> "Ranked Skywars"
        "SKYWARS_STANDARD" -> "Normal Skywars"
        else -> null
    }

    fun getMapHeight() = cachedMap?.get("maxBuild")?.asInt ?: -1
}