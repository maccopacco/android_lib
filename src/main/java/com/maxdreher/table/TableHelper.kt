package com.maxdreher.table

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import de.codecrafters.tableview.SortableTableView
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.model.TableColumnWeightModel

/**
 * Helper class for tables
 */
object TableHelper {
    /**
     * @param context     context to use to update
     * @param input       items to go into table
     * @param entryList   list of columns {@see TableEntry}
     * @param <InputType> type of data to show
     * @return [TableDataAdapter]
    </InputType> */
    private fun <InputType> getTableAdapter(
        context: Context?,
        input: List<InputType>,
        entryList: List<TableEntry<InputType>>
    ): TableDataAdapter<InputType> {
        return object : TableDataAdapter<InputType>(context, input) {
            override fun getCellView(
                rowIndex: Int,
                columnIndex: Int,
                parentView: ViewGroup
            ): View? {
                val data = getRowData(rowIndex)
                val entry: TableEntry<InputType> = try {
                    entryList[columnIndex]
                } catch (e: IndexOutOfBoundsException) {
                    return null
                }
                return entry.generate(getContext(), data)
            }
        }
    }


    /**
     * Update [SortableTableView] with info from [TableEntry]s
     *
     * @param context     context to work with
     * @param tableView   table to update
     * @param input       list of data to enter to table
     * @param entries     [TableEntry] columns to show
     * @param <InputType> type of data
     * @return `input` for chains? i guess
    </InputType> */
    fun <InputType> updateTable(
        context: Context,
        tableView: SortableTableView<InputType>,
        input: List<InputType>,
        entries: List<TableEntry<InputType>>
    ): SortableTableView<InputType> {
        val sortingStatus = tableView.sortingStatus

        //Headers
        val headers = entries.map { it.name }.toTypedArray()
        val adapter = CustomTableHeaderAdapter(context, *headers)
        adapter.setGravity(Gravity.CENTER)
        tableView.headerAdapter = adapter

        //Sorting & weighting
        val size = entries.size
        val columnWeightModel = TableColumnWeightModel(size)
        var hasColumnWeight = false

        for (i in 0 until size) {
            val entry = entries[i]
            if (entry.isSortable) {
                tableView.setColumnComparator(i, entry.comparator)
            }
            val weight = entry.weight
            if (weight != -1) {
                hasColumnWeight = true
                columnWeightModel.setColumnWeight(i, weight)
            }
        }
        if (hasColumnWeight) {
            tableView.columnModel = columnWeightModel
        }
        tableView.dataAdapter = getTableAdapter(context, input, entries)
        if (sortingStatus.isTableSorted) {
            tableView.sort(sortingStatus.sortedColumnIndex, sortingStatus.sortedOrder)
        }
        return tableView
    }
}