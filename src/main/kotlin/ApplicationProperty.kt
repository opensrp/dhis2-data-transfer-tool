import java.util.*

object ApplicationProperty {

    private const val PROPERTY_FILE_NAME: String = "application.properties"

    private val properties: Properties = Properties().apply { load(ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTY_FILE_NAME)) };

    fun getProperty(property: String): Any? {
        return properties.getOrDefault(property, null)
    }
}
