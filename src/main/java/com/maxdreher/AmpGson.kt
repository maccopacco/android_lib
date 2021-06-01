/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.maxdreher

import com.amplifyframework.core.model.temporal.Temporal
import com.google.gson.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object AmpGson {

    fun serialize(pretty: Boolean = false): Gson {
        return GsonBuilder()
            .apply {
                if (pretty) setPrettyPrinting()
            }
            .registerTypeAdapter(Date::class.java, DateSerializer())
            .registerTypeAdapter(
                Temporal.Timestamp::class.java, TemporalTimestampSerializer()
            )
            .registerTypeAdapter(Temporal.Date::class.java, TemporalDateSerializer())
            .registerTypeAdapter(
                Temporal.DateTime::class.java, TemporalDateTimeSerializer()
            )
            .registerTypeAdapter(Temporal.Time::class.java, TemporalTimeSerializer())
            .create()
    }

    fun deserialize(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Temporal.Date::class.java, TemporalDateDeserializer())
            .registerTypeAdapter(Temporal.Time::class.java, TemporalTimeDeserializer())
            .registerTypeAdapter(Temporal.Timestamp::class.java, TemporalTimestampDeserializer())
            .registerTypeAdapter(Temporal.DateTime::class.java, TemporalDateTimeDeserializer())
            .registerTypeAdapter(String::class.java, StringDeserializer())
            .create()
    }


    private class TemporalDateDeserializer : JsonDeserializer<Temporal.Date> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Temporal.Date {
            return Temporal.Date(json.asString)
        }
    }

    private class TemporalDateTimeDeserializer : JsonDeserializer<Temporal.DateTime> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Temporal.DateTime {
            return Temporal.DateTime(json.asString)
        }
    }

    private class TemporalTimeDeserializer : JsonDeserializer<Temporal.Time> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Temporal.Time {
            return Temporal.Time(json.asString)
        }
    }

    private class TemporalTimestampDeserializer : JsonDeserializer<Temporal.Timestamp> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Temporal.Timestamp {
            return Temporal.Timestamp(json.asLong, TimeUnit.SECONDS)
        }
    }

    private class StringDeserializer : JsonDeserializer<String> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
        ): String {
            return if (json.isJsonPrimitive) {
                json.asJsonPrimitive.asString
            } else if (json.isJsonObject) {
                json.toString()
            } else {
                throw JsonParseException("Failed to parse String from $json")
            }
        }
    }

    /**
     * Serializer of [Temporal.Date], an extended ISO-8601 Date string, with an optional timezone offset.
     *
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    private class TemporalDateSerializer : JsonSerializer<Temporal.Date> {
        override fun serialize(
            date: Temporal.Date,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(date.format())
        }
    }

    /**
     * Serializer of [Temporal.DateTime], an extended ISO-8601 DateTime string.
     * Time zone offset is required.
     *
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    private class TemporalDateTimeSerializer : JsonSerializer<Temporal.DateTime> {
        override fun serialize(
            dateTime: Temporal.DateTime,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(dateTime.format())
        }
    }

    /**
     * Serializer of [Temporal.Time], an extended ISO-8601 Time string, with an optional timezone offset.
     *
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    private class TemporalTimeSerializer : JsonSerializer<Temporal.Time> {
        override fun serialize(
            time: Temporal.Time,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(time.format())
        }
    }

    /**
     * Serializer of [Temporal.Timestamp], an AppSync scalar type that represents
     * the number of seconds elapsed since 1970-01-01T00:00Z. Timestamps are serialized as numbers.
     * Negative values are also accepted and these represent the number of seconds till 1970-01-01T00:00Z.
     *
     * https://docs.aws.amazon.com/appsync/latest/devguide/scalars.html
     */
    private class TemporalTimestampSerializer : JsonSerializer<Temporal.Timestamp> {
        override fun serialize(
            timestamp: Temporal.Timestamp,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(timestamp.secondsSinceEpoch)
        }
    }

    /**
     * Earlier versions of the model gen used to use Java's [Date] to represent all of the
     * temporal types. This led to challenges while trying to decode/encode the timezone,
     * among other things. The model gen will now spit out [Temporal.Date], [Temporal.DateTime],
     * [Temporal.Time], and [Temporal.Timestamp], instead. This DateSerializer is left for
     * compat, until such a time as it can be safely removed (that is, when all models no longer
     * use a raw Date type.)
     */
    private class DateSerializer : JsonSerializer<Date?> {
        override fun serialize(
            date: Date?,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return JsonPrimitive(dateFormat.format(date))
        }
    }
}