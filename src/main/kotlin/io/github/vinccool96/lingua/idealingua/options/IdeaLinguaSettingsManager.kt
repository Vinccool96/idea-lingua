package io.github.vinccool96.lingua.idealingua.options

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import io.github.vinccool96.lingua.idealingua.IdeaLinguaBundle
import javax.swing.JComponent

class IdeaLinguaSettingsManager(private val project: Project) : SearchableConfigurable {

    private val settings = IdeaLinguaSettings.getInstance(project)

    private var settingsPane: IdeaLinguaSettingsPane? = null

    override fun createComponent(): JComponent {
        if (settingsPane == null) {
            settingsPane = IdeaLinguaSettingsPane(settings)
        }

        return settingsPane!!.root
    }

    override fun isModified(): Boolean {
        return settingsPane == null
    }

    override fun apply() {
        // TODO("Not yet implemented")
    }

    @Suppress("DialogTitleCapitalization")
    override fun getDisplayName(): String {
        return IdeaLinguaBundle.message("idealingua.configurable.service.title")
    }

    override fun getId(): String {
        return "settings.idealingua"
    }

}