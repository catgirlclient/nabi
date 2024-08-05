package live.shuuyu.plugins.i18n

import org.gradle.api.Plugin
import org.gradle.api.Project

public class I18nPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val parent = project.parent
    }
}