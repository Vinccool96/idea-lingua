package io.github.vinccool96.lingua.idealingua.options

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import io.github.vinccool96.lingua.idealingua.IdeaLinguaBundle
import javax.swing.JComponent

class IdeaLinguaSettingsManager(private val project: Project) : SearchableConfigurable {

    private val settings = IdeaLinguaSettings.getInstance(project)

    private var settingsPane: IdeaLinguaSettingsPane? = null

    override fun createComponent(): JComponent {
        if (settingsPane == null) {
            settingsPane = IdeaLinguaSettingsPane(settings, project)
        }

        return settingsPane!!.root
    }

    override fun isModified(): Boolean {
        return settingsPane?.isModified ?: true
    }

    override fun apply() {
        settingsPane?.apply()
    }

    override fun reset() {
        settingsPane?.reset()
    }

    @Suppress("DialogTitleCapitalization")
    override fun getDisplayName(): String {
        return IdeaLinguaBundle.message("idealingua.configurable.service.title")
    }

    override fun getId(): String {
        return "settings.idealingua"
    }

    @Suppress("CompanionObjectInExtension")
    companion object {

        fun createBaseFolderDescriptor(): FileChooserDescriptor {
            val descriptor = FileChooserDescriptorFactory.singleDir()
            descriptor.title = IdeaLinguaBundle.message("idealingua.configurable.descriptor.main.folder.title")
            descriptor.description =
                    IdeaLinguaBundle.message("idealingua.configurable.descriptor.main.folder.description")
            return descriptor
        }

        fun createOtherFoldersDescriptor(): FileChooserDescriptor {
            val descriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor()
            descriptor.title = IdeaLinguaBundle.message("idealingua.configurable.descriptor.other.folders.title")
            descriptor.description =
                    IdeaLinguaBundle.message("idealingua.configurable.descriptor.other.folders.description")
            return descriptor
        }

    }

}