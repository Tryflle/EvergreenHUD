package org.polyfrost.evergreenhud.utils

import com.google.gson.JsonObject
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils.Location
import org.polyfrost.oneconfig.utils.v1.JsonUtils
import org.polyfrost.oneconfig.utils.v1.Multithreading
import org.polyfrost.oneconfig.utils.v1.NetworkUtils
import kotlin.jvm.optionals.getOrNull

object PinkuluAPIHelper {
    private var rawJson: List<JsonObject>? = null
    private var prevLoc: Location? = null
    private var prevData: JsonObject? = null

    init {
        Multithreading.submit {
            try {
                rawJson = JsonUtils.PARSER.parse(NetworkUtils.getString("https://maps.pinkulu.com/trans-rights-are-human-rights.json")).asJsonArray.mapNotNull { if (it.isJsonObject) it.asJsonObject else null } // so true bestie
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRawMapData(location: Location): JsonObject? {
        if (location == prevLoc) return prevData
        val mapName = location.mapName.getOrNull() ?: return null
        val gameType = location.gameType.getOrNull()?.databaseName?.uppercase() ?: return null
        val out = rawJson?.firstOrNull {
            it.get("name")?.asString == mapName && it.get("gameType")?.asString == gameType
        }
        return if (out != null) {
            prevLoc = location
            prevData = out
            out
        } else null
    }

    fun getMapPool(location: Location) = when (getRawMapData(location)?.get("pool")?.asString) {
        "BEDWARS_4TEAMS_FAST" -> "Fast 4 Teams"
        "BEDWARS_4TEAMS_SLOW" -> "Slow 4 Teams"
        "BEDWARS_8TEAMS_FAST" -> "Fast 8 Teams"
        "BEDWARS_8TEAMS_SLOW" -> "Slow 8 Teams"
        "SKYWARS_MEGA" -> "Mega Skywars"
        "SKYWARS_RANKED" -> "Ranked Skywars"
        "SKYWARS_STANDARD" -> "Normal Skywars"
        else -> null
    }

    fun getMapHeight(location: Location) = getRawMapData(location)?.get("maxBuild")?.asInt ?: -1
}