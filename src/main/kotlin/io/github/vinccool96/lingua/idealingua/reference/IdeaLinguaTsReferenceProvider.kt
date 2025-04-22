package io.github.vinccool96.lingua.idealingua.reference

import com.intellij.lang.javascript.hierarchy.JSHierarchyUtils
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import io.github.vinccool96.lingua.idealingua.psi.TranslationFramework
import io.github.vinccool96.lingua.idealingua.psi.TranslationUtils

class IdeaLinguaTsReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val function = JSHierarchyUtils.getTypeHierarchyTargetElement(
                element.parent.parent.children.first() as? JSReferenceExpression).let { it as? TypeScriptFunction }
                ?.takeIf {
                    TranslationUtils.FRAMEWORKS.map(TranslationFramework::functionName)
                            .any { funName -> it.name == funName }
                } ?: return arrayOf()

        // Retrieve class from 'translate' function
        val functionClass = JSUtils.getMemberContainingClass(function)

        // Only reference if class is TranslateService
        return if (functionClass.jsType.typeText == "TranslateService") {
            arrayOf(IdeaLinguaReference(element as JSLiteralExpression, TextRange.allOf(element.text)))
        } else arrayOf()
    }

}