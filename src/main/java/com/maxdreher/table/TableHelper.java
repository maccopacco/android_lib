package com.maxdreher.table;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.SortingStatus;
import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.model.TableColumnWeightModel;

//TODO #6
public class TableHelper {

    public static <InputType> TableDataAdapter<InputType> getTableAdapter(Context context, List<InputType> input, List<TableEntry<InputType>> entryList) {
        return new TableDataAdapter<InputType>(context, input) {

            @Override
            public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
                InputType data = getRowData(rowIndex);
                Context context = getContext();

                TableEntry<InputType> entry;
                try {
                    entry = entryList.get(columnIndex);
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }

                return entry.generate(context, data);
            }
        };
    }

    public static <InputType> SortableTableView<InputType> updateTable(Context context, SortableTableView<InputType> tableView, List<InputType> input, List<TableEntry<InputType>> entries) {
        final SortingStatus sortingStatus = tableView.getSortingStatus();

        //Headers
        final String[] headers = entries.stream().map(TableEntry::getName).toArray(String[]::new);
        CustomTableHeaderAdapter adapter = new CustomTableHeaderAdapter(context, headers);
        adapter.setGravity(Gravity.CENTER);
        tableView.setHeaderAdapter(adapter);

        //Sorting & weighting
        final int size = entries.size();
        TableColumnWeightModel columnWeightModel = new TableColumnWeightModel(size);
        boolean hasColumnWeight = false;

        for (int i = 0; i < size; i++) {
            final TableEntry<InputType> entry = entries.get(i);
            if (entry.isSortable()) {
                tableView.setColumnComparator(i, entry.getComparator());
            }

            final int weight = entry.getWeight();
            if (weight != -1) {
                hasColumnWeight = true;
                columnWeightModel.setColumnWeight(i, weight);
            }
        }

        if (hasColumnWeight) {
            tableView.setColumnModel(columnWeightModel);
        }

        tableView.setDataAdapter(getTableAdapter(context, input, entries));

        if (sortingStatus.isTableSorted()) {
            tableView.sort(sortingStatus.getSortedColumnIndex(), sortingStatus.getSortedOrder());
        }
        return tableView;
    }
}