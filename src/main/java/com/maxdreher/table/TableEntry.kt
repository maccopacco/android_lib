package com.maxdreher.table

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.maxdreher.consumers.ConsumeAndSupply
import com.maxdreher.consumers.ConsumeTwo
import com.maxdreher.consumers.ConsumeTwoAndSupply
import de.codecrafters.tableview.SortableTableView
import java.util.*


/**
 * A table value for a [SortableTableView]
 *
 * Holds:
 * name of column
 * a converter from input -> view (can be assisted with [textViewGenerator])
 * comparator
 * layout weight
 */
class TableEntry<Input> @JvmOverloads constructor(
    val name: String,
    private val viewGenerator: (Context, Input) -> View,
    val weight: Int = 1,
    val comparator: Comparator<Input>? = null,
) {

    val isSortable: Boolean
        get() = comparator != null

    fun generate(context: Context, input: Input): View {
        return viewGenerator.invoke(context, input)
    }

    companion object {

        fun <Input> from(
            margin: Int,
            map: Map<String, (Input) -> String>
        ): List<TableEntry<Input>> {
            return map.map { toText(it.key, it.value, margin) }
        }

        fun <Input> toText(
            name: String,
            toString: (Input) -> String,
            margin: Int
        ): TableEntry<Input> {
            return TableEntry(name, textViewGenerator(toString, margin))
        }

        fun <Input> textViewGenerator(
            convertToText: (Input) -> String,
            margin: Int,
            viewConsumer: ((TextView, Input) -> Unit)? = null
        ): (Context, Input) -> View {
            return { context: Context, input: Input ->

                val linearLayout = LinearLayout(context)

                linearLayout.apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val textView = TextView(context).apply {
                    text = convertToText.invoke(input)
                    gravity = Gravity.CENTER
                }

                if (margin != -1) {
                    textView.setPadding(margin, margin, margin, margin)
                }

                viewConsumer?.invoke(textView, input)

                linearLayout.addView(textView)
                linearLayout
            }
        }
    }
}