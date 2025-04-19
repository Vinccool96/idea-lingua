package io.github.vinccool96.lingua.idealingua.extensions

import com.intellij.json.JsonFileType
import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.isJsonFolder() = isDirectory && children.any { isJsonFile() }

fun VirtualFile.isJsonFile() = fileType === JsonFileType.INSTANCE