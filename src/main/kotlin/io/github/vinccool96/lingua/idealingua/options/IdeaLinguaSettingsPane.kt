package io.github.vinccool96.lingua.idealingua.options

import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import io.github.vinccool96.lingua.idealingua.IdeaLinguaBundle

@Suppress("UnstableApiUsage")
class IdeaLinguaSettingsPane(private val settings: IdeaLinguaSettings) {

    private lateinit var mainFolder: TextFieldWithBrowseButton

    val root = panel {
        row(IdeaLinguaBundle.message("idealingua.configurable.settings.main.folder")) {
            mainFolder = textFieldWithBrowseButton(IdeaLinguaSettingsManager.createBaseFolderDescriptor())
                    .columns(COLUMNS_LARGE).component
        }
        row(IdeaLinguaBundle.message("idealingua.configurable.settings.other.folders")) {
            OtherFoldersPanel()
        }
    }

    fun reset() {
        mainFolder.text = FileUtil.toSystemDependentName(settings.translationsPath)
    }

    fun apply() {
        settings.translationsPath = FileUtil.toSystemIndependentName(mainFolder.text.trim())
    }

}