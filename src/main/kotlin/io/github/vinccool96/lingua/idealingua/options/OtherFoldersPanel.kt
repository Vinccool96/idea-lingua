package io.github.vinccool96.lingua.idealingua.options

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.Consumer
import com.intellij.util.PathUtil
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import io.github.vinccool96.lingua.idealingua.IdeaLinguaBundle
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class OtherFoldersPanel(private val mySettings: IdeaLinguaSettings, private val myProject: Project) : JPanel() {

    private val myOtherFoldersTableView = OtherFoldersTableView(mySettings.otherTranslationsPaths)

    init {
        val decorator = ToolbarDecorator.createDecorator(myOtherFoldersTableView)
                .setAddActionName(IdeaLinguaBundle.message("idealingua.configurable.settings.other.folders.add"))
                .setAddAction {
                    myOtherFoldersTableView.stopEditing()
                    doChooseFolders { files ->
                        files.map { PathUtil.toSystemDependentName(it.path) }
                                .filter { it !in myOtherFoldersTableView.items }
                                .forEach { myOtherFoldersTableView.listTableModel.addRow(it) }
                    }
                }
                .setRemoveActionName(IdeaLinguaBundle.message("idealingua.configurable.settings.other.folders.remove"))
                .setRemoveAction { TableUtil.removeSelectedItems(myOtherFoldersTableView) }
                .disableUpDownActions()

        myOtherFoldersTableView.emptyText.text =
                IdeaLinguaBundle.message("idealingua.configurable.settings.other.folders.empty")
        layout = BorderLayout()
        add(decorator.createPanel(), BorderLayout.CENTER)
    }

    val isModified: Boolean
        get() {
            val oldFolders = mySettings.otherTranslationsPaths
            val newFolders = myOtherFoldersTableView.items

            if (oldFolders.isEmpty()) {
                return newFolders.isNotEmpty()
            }

            return oldFolders.size != newFolders.size || !newFolders.containsAll(oldFolders) ||
                    !oldFolders.containsAll(newFolders)
        }

    private fun doChooseFolders(consumer: Consumer<in List<VirtualFile>>) {
        val descriptor = IdeaLinguaSettingsManager.createOtherFoldersDescriptor()
        val directory = myProject.guessProjectDir()
        FileChooser.chooseFiles(descriptor, myProject, parent, directory, consumer)
    }

    private class OtherFoldersTableView(otherFolders: List<String>) : TableView<String>() {

        private val myRenderer = createRenderer()

        init {
            val items = ArrayList(otherFolders)
            setModelAndUpdateColumns(ListTableModel(createColumnInfos(), items, 0))
            setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN)
            setShowGrid(false)
            setShowVerticalLines(false)
            setGridColor(foreground)
            setTableHeader(null)
        }

        private fun createColumnInfos(): Array<ColumnInfo<String, String>> {
            return arrayOf(object : ColumnInfo<String, String>(
                    IdeaLinguaBundle.message("idealingua.configurable.settings.other.folders.title")) {

                override fun valueOf(item: String?): String? {
                    return item
                }

                override fun getRenderer(item: String?): TableCellRenderer {
                    return myRenderer
                }

            })
        }

        companion object {

            private fun createRenderer(): TableCellRenderer {
                return object : TableCellRenderer {

                    private val myLabel = SimpleColoredComponent()

                    override fun getTableCellRendererComponent(table: JTable, value: Any, isSelected: Boolean,
                            hasFocus: Boolean, row: Int, column: Int): Component {
                        myLabel.clear()
                        myLabel.append(value as String, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                        myLabel.foreground = if (isSelected) table.selectionForeground else table.foreground
                        myLabel.background = if (isSelected) table.selectionBackground else table.background
                        return myLabel
                    }
                }
            }

        }

    }
}