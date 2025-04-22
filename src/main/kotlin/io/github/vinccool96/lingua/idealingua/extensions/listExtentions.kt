package io.github.vinccool96.lingua.idealingua.extensions

import com.intellij.json.psi.JsonValue
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult

/**
 * Provide simple shorthand to convert the List of JsonValue into a ResolveResult
 */
fun List<JsonValue>.toTypedResolveResult(): Array<ResolveResult> {
    return map(::PsiElementResolveResult).toTypedArray()
}