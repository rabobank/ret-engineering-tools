package io.rabobank.ret.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.Serial

class CommaDelimitedToListDeserializer : StdDeserializer<List<String>>(String::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?,
    ) = p.valueAsString?.run { split(",").map { it.trim() } }.orEmpty()

    companion object {
        @Serial
        private const val serialVersionUID: Long = 1L
    }
}
