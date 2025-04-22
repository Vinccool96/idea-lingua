package io.github.vinccool96.lingua.idealingua.reference

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import io.github.vinccool96.lingua.idealingua.extensions.toTypedResolveResult
import io.github.vinccool96.lingua.idealingua.psi.TranslationUtils

class IdeaLinguaReference(element: JSLiteralExpression, textRange: TextRange) :
        PsiReferenceBase.Poly<PsiElement?>(element, textRange, false), PsiPolyVariantReference {

    private val path: List<String>? = element.stringValue?.split('.')

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return TranslationUtils.findTranslationKey(ModuleUtilCore.findModuleForPsiElement(element), myElement!!.project,
                path).toTypedResolveResult()
    }

}