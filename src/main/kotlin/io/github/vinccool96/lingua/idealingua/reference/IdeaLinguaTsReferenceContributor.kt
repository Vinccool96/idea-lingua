package io.github.vinccool96.lingua.idealingua.reference

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import io.github.vinccool96.lingua.idealingua.psi.Patterns

class IdeaLinguaTsReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(Patterns.TS_TRANSLATION_PLATFORM_PATTERN, IdeaLinguaTsReferenceProvider())
    }

}