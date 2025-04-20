package io.github.vinccool96.lingua.idealingua.options

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import org.jdom.Element

@State(name = "IdeaLinguaSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
@Service(Service.Level.PROJECT)
class IdeaLinguaSettings : PersistentStateComponent<Element> {

    var translationsPath = DEFAULT_LOCATION

    var otherTranslationsPaths: List<String> = ArrayList()

    override fun getState(): Element? {
        if (translationsPath == DEFAULT_LOCATION && otherTranslationsPaths.isEmpty()) {
            return null
        }

        val element = Element(IDEA_LINGUA_SETTINGS_TAG)

        element.setAttribute(TRANSLATIONS_PATH_ATTRIBUTE_NAME, translationsPath)

        element.setAttribute(OTHER_TRANSLATIONS_PATHS_ATTRIBUTE_NAME, otherTranslationsPaths.size.toString())

        for ((i, otherTranslationsPath) in otherTranslationsPaths.withIndex()) {
            element.setAttribute("$OTHER_TRANSLATIONS_PATH_ATTRIBUTE_NAME$i", otherTranslationsPath)
        }

        return element
    }

    override fun loadState(state: Element) {
        val otherPaths = otherTranslationsPaths as MutableList<String>
        otherPaths.clear()

        try {
            translationsPath = state.getAttributeValue(TRANSLATIONS_PATH_ATTRIBUTE_NAME)

            val otherPathsSize =
                    StringUtil.parseInt(state.getAttributeValue(OTHER_TRANSLATIONS_PATHS_ATTRIBUTE_NAME), 0)

            for (i in 0..<otherPathsSize) {
                otherPaths.add(state.getAttributeValue("$OTHER_TRANSLATIONS_PATH_ATTRIBUTE_NAME$i"))
            }
        } catch (_: Exception) {
        }
    }

    companion object {

        private const val IDEA_LINGUA_SETTINGS_TAG = "IdeaLinguaSettings"

        private const val TRANSLATIONS_PATH_ATTRIBUTE_NAME = "TranslationsPath"

        private const val OTHER_TRANSLATIONS_PATHS_ATTRIBUTE_NAME = "OtherTranslationsPaths"

        private const val OTHER_TRANSLATIONS_PATH_ATTRIBUTE_NAME = "OtherTranslationsPath"

        private const val DEFAULT_LOCATION = "./src/assets/i18n"

        fun getInstance(project: Project): IdeaLinguaSettings {
            return project.getService(IdeaLinguaSettings::class.java)
        }

    }

}