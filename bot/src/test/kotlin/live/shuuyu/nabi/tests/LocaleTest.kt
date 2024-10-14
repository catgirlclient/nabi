package live.shuuyu.nabi.tests

import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.Path


class LocaleTest {
    @Test
    fun generateAndParseI18nTest() {
        val directory = Files.list(Path("../resources/tests/locales/"))


    }
}