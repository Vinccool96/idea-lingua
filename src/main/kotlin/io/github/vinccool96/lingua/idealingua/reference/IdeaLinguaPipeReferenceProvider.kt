package io.github.vinccool96.lingua.idealingua.reference

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.html.HtmlTag
import com.intellij.util.ProcessingContext
import io.github.vinccool96.lingua.idealingua.psi.TranslationFramework
import io.github.vinccool96.lingua.idealingua.psi.TranslationUtils
import org.angular2.lang.expr.psi.Angular2PipeExpression
import org.angular2.lang.expr.psi.Angular2PipeReferenceExpression

class IdeaLinguaPipeReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element !is JSLiteralExpression) {
            return arrayOf()
        }

        var parentPointer: PsiElement = element
        var validated = false

        while (parentPointer !is HtmlTag && !validated) {
            parentPointer = parentPointer.parent

            if (parentPointer is Angular2PipeExpression) { // Check for expr|pipe
                validated = parentPointer.children.filterIsInstance<Angular2PipeReferenceExpression>().firstOrNull()
                        ?.let { expression ->
                            TranslationUtils.FRAMEWORKS.map(TranslationFramework::pipeName)
                                    .any { expression.textMatches(it) }
                        } ?: false
            }
        }

        return if (validated) arrayOf(IdeaLinguaReference(element, TextRange.allOf(element.text)))
        else arrayOf()
    }

}