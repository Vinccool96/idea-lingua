package io.github.vinccool96.lingua.idealingua.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import com.intellij.util.ui.ColorIcon
import io.github.vinccool96.lingua.idealingua.IdeaLinguaBundle
import io.github.vinccool96.lingua.idealingua.psi.TranslationUtils

class IdeaLinguaCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext,
            result: CompletionResultSet) {
        (parameters.originalPosition?.parent as? JSLiteralExpression)?.let {
            val partiallyProvidedPath = it.stringValue ?: return
            val candidates = TranslationUtils
                    .findTranslationPartialKey(ModuleUtilCore.findModuleForPsiElement(it), it.project,
                            partiallyProvidedPath.split('.'))
                    .flatMap { item ->
                        item.value.map { (json, score) ->
                            if (json is JsonStringLiteral) { // End key
                                LookupElementBuilder
                                        .create(item.key)
                                        .withIcon(ColorIcon(TranslationUtils.ICON_SIZE, JBColor.BLUE))
                                        .withTailText("=${json.value}", true)
                                        .withTypeText(IdeaLinguaBundle.message("idealingua.ui.translation_key_leaf"),
                                                true)
                                        .withPsiElement(json) to score
                            } else {
                                LookupElementBuilder // Intermediate key
                                        .create(item.key + ".") // Append '.' to continue completion
                                        .withPresentableText(item.key)
                                        .withIcon(ColorIcon(TranslationUtils.ICON_SIZE, JBColor.CYAN))
                                        .withTypeText(IdeaLinguaBundle.message("idealingua.ui.translation_key_node"),
                                                true) to score
                            }
                        }
                    }.sortedByDescending { (_, isMainFile) -> isMainFile }.map { (lookupElement, _) -> lookupElement }

            result.withPrefixMatcher(partiallyProvidedPath).addAllElements(candidates)
        }
    }

}