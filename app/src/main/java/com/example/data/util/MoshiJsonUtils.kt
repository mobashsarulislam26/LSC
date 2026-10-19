package com.example.data.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Utility for robust JSON serialization and deserialization using Moshi.
 * Ensures user-generated notes, special characters (quotes, newlines, tabs, Bangla characters)
 * are correctly escaped without breaking the sync process.
 */
object MoshiJsonUtils {

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    private val stringMapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)

    private val mapAdapter by lazy { moshi.adapter<Map<String, Any?>>(mapType) }
    private val stringMapAdapter by lazy { moshi.adapter<Map<String, String>>(stringMapType) }

    fun toJson(map: Map<String, Any?>): String {
        return try {
            mapAdapter.toJson(map)
        } catch (e: Exception) {
            "{}"
        }
    }

    fun toTypedStringMapJson(map: Map<String, String>): String {
        return try {
            stringMapAdapter.toJson(map)
        } catch (e: Exception) {
            "{}"
        }
    }

    fun jsonToMap(json: String): Map<String, Any?> {
        if (json.isBlank()) return emptyMap()
        return try {
            mapAdapter.fromJson(json) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    inline fun <reified T> fromJson(json: String): T? {
        if (json.isBlank()) return null
        return try {
            moshi.adapter(T::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> toJson(data: T): String {
        return try {
            moshi.adapter(T::class.java).toJson(data)
        } catch (e: Exception) {
            "{}"
        }
    }
}
