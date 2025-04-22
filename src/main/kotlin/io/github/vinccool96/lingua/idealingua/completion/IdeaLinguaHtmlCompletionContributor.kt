package io.github.vinccool96.lingua.idealingua.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import io.github.vinccool96.lingua.idealingua.psi.Patterns

class IdeaLinguaHtmlCompletionContributor : CompletionContributor() {

    init {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(Patterns.HTML_TRANSLATION_PLATFORM_PATTERN),
                IdeaLinguaCompletionProvider())
    }

}