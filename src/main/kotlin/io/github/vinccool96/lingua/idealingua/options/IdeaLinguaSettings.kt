package io.github.vinccool96.lingua.idealingua.options

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import org.jdom.Element

@State(name = "IdeaLinguaSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
@Service(Service.Level.PROJECT)
class IdeaLinguaSettings : PersistentStateComponent<Element> {

    override fun getState(): Element? {
        TODO("Not yet implemented")
    }

    override fun loadState(state: Element) {
        TODO("Not yet implemented")
    }

    companion object {

        fun getInstance(project: Project): IdeaLinguaSettings {
            return project.getService(IdeaLinguaSettings::class.java)
        }

    }

}