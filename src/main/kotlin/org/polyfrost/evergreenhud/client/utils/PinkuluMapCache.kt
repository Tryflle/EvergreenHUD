package org.polyfrost.evergreenhud.client.utils

import com.google.gson.JsonObject
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils
import org.polyfrost.oneconfig.utils.v1.JsonUtils
import org.polyfrost.oneconfig.utils.v1.Multithreading
import org.polyfrost.oneconfig.utils.v1.NetworkUtils
import kotlin.jvm.optionals.getOrNull

object PinkuluMapCache {

    private lateinit var cachedJson: List<JsonObject>
    private var previousLocation: HypixelUtils.Location? = null
    private var previousData: JsonObject? = null

    val isReady: Boolean
        get() = ::cachedJson.isInitialized

    fun initialize() {
        if (isReady) {
            return
        }

        Multithreading.submit {
            try {
                val jsonString = NetworkUtils.getString("https://maps.pinkulu.com/trans-rights-are-human-rights.json")
                val element = JsonUtils.parse(jsonString)
                if (!element.isJsonArray) {
                    return@submit
                }

                val array = element.asJsonArray
                cachedJson = array.mapNotNull {
                    if (it.isJsonObject) {
                        it.asJsonObject
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFor(location: HypixelUtils.Location): JsonObject? {
        if (!isReady || previousLocation == location) {
            return previousData
        }

        val mapName = location.mapName.getOrNull() ?: return null
        val gameType = location.gameType.getOrNull()?.databaseName?.uppercase() ?: return null
        val data = cachedJson.firstOrNull {
            it.has("mapName") && it.get("mapName").asString == mapName &&
            it.has("gameType") && it.get("gameType").asString == gameType
        } ?: return null

        previousLocation = location
        previousData = data
        return data
    }

    fun getMapName(location: HypixelUtils.Location): String? {
        return when (getFor(location)?.get("pool")?.asString) {
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

    fun getMapHeight(location: HypixelUtils.Location): Int {
        return getFor(location)?.get("maxBuild")?.asInt ?: -1
    }

}
