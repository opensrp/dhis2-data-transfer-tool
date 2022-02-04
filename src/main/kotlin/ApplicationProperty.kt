import java.io.File
import java.io.FileInputStream
import java.util.*

object ApplicationProperty {

    private const val PROPERTY_FILE_NAME: String = "application.properties"

    private val properties: Properties = Properties().apply {
        val file = File(PROPERTY_FILE_NAME)
        if(file.exists())
            load(FileInputStream(file))
        else
            load(ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTY_FILE_NAME))
    }

    fun getProperty(property: String): Any? {
        return properties.getOrDefault(property, null)
    }
}
