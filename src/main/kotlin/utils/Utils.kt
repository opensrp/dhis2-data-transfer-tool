package utils

import configuration.Application.ApplicationCsvMapper.csvMapper
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*


object Utils {
    val logger = LoggerFactory.getLogger(javaClass)

    inline fun <reified T> writeToCsv(data: Collection<T>, fileName: String) {
        csvMapper.writer(csvMapper.schemaFor(T::class.java).withHeader())
                .writeValue(File(fileName), data)

        logger.info("$fileName created.")
    }

    fun idToUUID(id: String?): String {
        return if (id != null) UUID.nameUUIDFromBytes(id.toByteArray()).toString() else ""
    }
}
