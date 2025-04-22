package io.github.vinccool96.lingua.idealingua.psi

import com.intellij.json.JsonFileType
import com.intellij.json.JsonUtil
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import io.github.vinccool96.lingua.idealingua.options.IdeaLinguaSettings
import kotlin.io.path.Path

object TranslationUtils {

    val FRAMEWORKS =
            listOf(TranslationFramework("translate", "instant"), TranslationFramework("transloco", "translate"))

    const val ICON_SIZE = 8

    // Path element filter for default Translation location
    private val ASSETS_PATH = Path("assets")

    fun findTranslationKey(module: Module?, project: Project, path: List<String>?): List<JsonStringLiteral> {
        // Don't bother filtering on files if the path is empty
        if (module == null || path.isNullOrEmpty()) {
            return emptyList()
        }

        return getJsonAssets(module, project).mapNotNull {
            val jsonTranslationPath = path.listIterator()

            val jsonFile: JsonFile? = PsiManager.getInstance(project).findFile(it) as JsonFile?
            var jsonValue: JsonValue? = JsonUtil.getTopLevelObject(jsonFile)

            while (jsonValue != null && jsonValue is JsonObject && jsonTranslationPath.hasNext()) {
                jsonValue = jsonValue.findProperty(jsonTranslationPath.next())?.value
            }

            if (!jsonTranslationPath.hasNext() && jsonValue is JsonStringLiteral) jsonValue else null
        }
    }

    private fun recurseKeysWithFilter(value: JsonValue, lookupKey: List<String>,
            keys: List<String> = listOf()): Map<String, JsonValue> {
        val localMap = if (keys.isEmpty()) { // Prevent returning root
            mapOf()
        } else mapOf(keys.joinToString(".") to value)

        return when (value) {
            is JsonObject ->
                value.propertyList
                        .filter {
                            lookupKey.isEmpty() || it.name.matches(".*${Regex.escape(lookupKey.first())}.*".toRegex())
                        }.map { prop ->
                            ProgressManager.checkCanceled() // Check cancel before continuing recursion
                            recurseKeysWithFilter(prop.value!!, lookupKey.drop(1), keys + prop.name)
                        }.fold(localMap) { acc, map -> acc + map } // Iterate and filter key
            is JsonStringLiteral -> mapOf(keys.joinToString(".") to value)
            else -> throw IllegalArgumentException(
                    "JsonValue should be JsonObject or JsonStringLiteral in translation files")
        }
    }

    /**
     * Find all translation keys matching with the currently provided path
     *
     * @param path The list of components in the translation key, they can be partial (e.g., CAT.K for MY_CAT.MY_KEY)
     */
    fun findTranslationPartialKey(module: Module?, project: Project,
            path: List<String>?): Map<String, List<Pair<JsonValue, Boolean>>> {
        // Don't bother filtering on files if the path is empty or the module is absent
        if (module == null || path.isNullOrEmpty()) {
            return emptyMap()
        }

        val results = mutableMapOf<String, List<Pair<JsonValue, Boolean>>>()

        // Provide better filtering on translation files
        getJsonAssets(module, project).forEach {
            ProgressManager.checkCanceled()
            val jsonFile: JsonFile = PsiManager.getInstance(project).findFile(it) as JsonFile? ?: return@forEach
            val isMainFile = isSelectedFile(project, it)
            ProgressManager.checkCanceled()

            val jsonValue: JsonValue? = JsonUtil.getTopLevelObject(jsonFile)

            if (jsonValue != null) {
                val keysForFile = recurseKeysWithFilter(jsonValue, path)
                keysForFile.forEach { (k, v) ->
                    results.compute(k) { _, l ->
                        if (l == null) listOf(
                                v to isMainFile
                        ) else l + (v to isMainFile)
                    }
                }
            }
        }

        return results
    }

    /**
     * Check if a file is corresponding to the configured default translation file
     *
     * @param file The file to check
     *
     * @return `true` if the virtual file is the translation file stored in the project configuration
     */
    private fun isSelectedFile(project: Project, file: VirtualFile): Boolean {
        return file.runCatching { toNioPath() == Path(getJsonTranslationFile(project)) }.getOrElse { false }
    }

    /**
     * Retrieve JSON files either stored in the path defined in the plugin configuration or in the default assets folder
     */
    private fun getJsonAssets(module: Module, project: Project): List<VirtualFile> {
        val jsonFiles = mutableListOf<VirtualFile>()

        // Try to add translations from the provided paths
        jsonFiles.addAll(getJsonTranslationPaths(project).filter { it.isNotBlank() }
                .map { getJsonAssetsByPath(FileUtil.toSystemDependentName(it)) }.flatten())

        // Fallback if no translation file exists in the provided path or the provided path is not configured
        if (jsonFiles.isEmpty()) {
            jsonFiles += getJsonAssetsByAssetsFilter(module)
        }

        return jsonFiles
    }

    private fun getJsonTranslationFile(project: Project): String {
        val settings = IdeaLinguaSettings.getInstance(project)
        val baseFolder = VirtualFileManager.getInstance()
                .findFileByNioPath(Path(FileUtil.toSystemDependentName(settings.translationsPath))) ?: return ""
        val baseFile =
                baseFolder.takeIf { it.isDirectory }?.children?.firstOrNull { it.fileType == JsonFileType.INSTANCE }
                        ?: return ""
        return baseFile.path
    }

    private fun getJsonTranslationPaths(project: Project): List<String> {
        val settings = IdeaLinguaSettings.getInstance(project)
        val result = arrayListOf(settings.translationsPath)
        result.addAll(settings.otherTranslationsPaths)
        return result
    }

    /**
     * Retrieve JSON files stored in the project assets
     */
    private fun getJsonAssetsByAssetsFilter(module: Module): List<VirtualFile> {
        return FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.moduleScope(module))
                .filter { it.runCatching { toNioPath().contains(ASSETS_PATH) }.getOrDefault(false) }
    }

    /**
     * Retrieve JSON files stored in a path
     *
     * @param path Path where JSON translations are stored
     */
    private fun getJsonAssetsByPath(path: String): List<VirtualFile> {
        return VirtualFileManager.getInstance()
                .runCatching { findFileByNioPath(Path(path)).takeIf { it?.isDirectory ?: false } }
                .getOrNull()?.children?.filter { it.fileType == JsonFileType.INSTANCE } ?: listOf()
    }

}