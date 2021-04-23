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
    private val viewGenerator: ConsumeTwoAndSupply<Context, Input, in View>,
    val weight: Int = 1,
    val comparator: Comparator<Input>? = null,
) {

    val isSortable: Boolean
        get() = comparator != null

    fun generate(context: Context?, input: Input): View {
        return viewGenerator.consume(context, input) as View
    }

    companion object {

        fun <Input> toText(
            name: String,
            toString: (Input) -> String,
            margin: Int
        ): TableEntry<Input> {
            return TableEntry(name, textViewGenerator(toString, margin))
        }

        fun <Input> textViewGenerator(
            convertToText: ConsumeAndSupply<Input, String>,
            margin: Int,
            viewConsumer: ConsumeTwo<TextView, Input>? = null
        ): ConsumeTwoAndSupply<Context, Input, View> {
            return ConsumeTwoAndSupply { context: Context, input: Input ->

                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.gravity = Gravity.CENTER
                linearLayout.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val textView = TextView(context)
                textView.text = convertToText.consume(input)
                textView.gravity = Gravity.CENTER
                if (margin != -1) {
                    textView.setPadding(margin, margin, margin, margin)
                }

                viewConsumer?.consume(textView, input)

                linearLayout.addView(textView)
                return@ConsumeTwoAndSupply linearLayout
            }
        }
    }
}