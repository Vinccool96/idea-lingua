package io.github.vinccool96.lingua.idealingua

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

object IdeaLinguaBundle : DynamicBundle(IdeaLinguaBundle::class.java, IdeaLinguaConstants.BUNDLE) {

    fun message(@PropertyKey(resourceBundle = IdeaLinguaConstants.BUNDLE) key: String): String {
        return getMessage(key)
    }

}