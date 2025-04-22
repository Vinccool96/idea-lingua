package io.github.vinccool96.lingua.idealingua.options

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.ui.IdeBorderFactory
import com.intellij.util.ui.JBUI
import io.github.vinccool96.lingua.idealingua.IdeaLinguaBundle
import java.awt.BorderLayout
import javax.swing.JPanel

class IdeaLinguaSettingsPane(private val settings: IdeaLinguaSettings, project: Project) {

    lateinit var root: JPanel

    lateinit var myMainFolder: TextFieldWithBrowseButton

    lateinit var myPanelForOtherFolders: JPanel

    private val myOtherFoldersPanel = OtherFoldersPanel(settings, project)

    init {
        myMainFolder.addBrowseFolderListener(project, IdeaLinguaSettingsManager.createBaseFolderDescriptor())

        myPanelForOtherFolders.border = IdeBorderFactory.createTitledBorder(
                IdeaLinguaBundle.message("idealingua.configurable.settings.other.folders"), false, JBUI.insetsTop(0))
                .setShowLine(false)
        myPanelForOtherFolders.layout = BorderLayout()
        myPanelForOtherFolders.add(myOtherFoldersPanel, BorderLayout.CENTER)
    }

    val isModified: Boolean
        get() {
            return FileUtil.toSystemIndependentName(myMainFolder.text.trim()) != settings.translationsPath ||
                    myOtherFoldersPanel.isModified
        }

    fun reset() {
        myMainFolder.text = FileUtil.toSystemDependentName(settings.translationsPath)
        myOtherFoldersPanel.reset()
    }

    fun apply() {
        settings.translationsPath = FileUtil.toSystemIndependentName(myMainFolder.text.trim())
        myOtherFoldersPanel.apply()
    }

}