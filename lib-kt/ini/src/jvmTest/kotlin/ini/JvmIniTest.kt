package ini

import parsers.*
import ini.Ini
import kotlin.test.*

class JvmIniTest {
	fun getTextResource(path: String): String =
		ClassLoader.getSystemResource(path).readText()

	@Test
	fun jvmTest() {
		val normal = getTextResource("good/normal.ini")
		val parsed = Ini.parse(normal)
		println(parsed)
	}
}
