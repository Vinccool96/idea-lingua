package io.github.vinccool96.lingua.idealingua.reference

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import io.github.vinccool96.lingua.idealingua.psi.Patterns
import org.angular2.lang.expr.Angular2Language

class IdeaLinguaHtmlReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(Patterns.HTML_TRANSLATION_PLATFORM_PATTERN.withLanguage(Angular2Language),
                IdeaLinguaPipeReferenceProvider())
    }

}