package io.github.vinccool96.lingua.idealingua.psi

import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import org.angular2.lang.expr.psi.Angular2PipeReferenceExpression

data class TranslationFramework(val pipeName: String, val functionName: String) {

    val callPattern: PsiElementPattern.Capture<JSReferenceExpression>
        get() {
            return PlatformPatterns.psiElement(JSReferenceExpression::class.java)
                    .withChild(PlatformPatterns.psiElement().withText(functionName))
        }

    val pipePattern: PsiElementPattern.Capture<Angular2PipeReferenceExpression>
        get() {
            return PlatformPatterns.psiElement(Angular2PipeReferenceExpression::class.java)
                    .withChild(PlatformPatterns.psiElement().withText(pipeName))
        }

}
