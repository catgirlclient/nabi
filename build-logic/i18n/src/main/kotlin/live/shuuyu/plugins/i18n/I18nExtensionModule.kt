package live.shuuyu.plugins.i18n

import live.shuuyu.plugins.i18n.tasks.GenerateI18nFileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class I18nExtensionModule: Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        createTasks(this)
    }

    private fun createTasks(project: Project) {
        project.tasks.create("GenerateI18nFileTasks", GenerateI18nFileTask::class) {
            description = "Generates files in the selected directory."
            group = "live.shuuyu.plugins.i18n"
        }
    }
}