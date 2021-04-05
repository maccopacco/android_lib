package com.maxdreher

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.navigation.findNavController
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.DataStoreCategory
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.DataStoreItemChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.function.Consumer

/**
 * Utility class
 */
class Util {
    companion object {
        fun getDefaultMargin(context: Context?, id: Int): Int {
            return context!!.resources.getDimension(id).toInt()
        }

        /**
         * Convert map of buttons to navigate to specified resource ids
         */
        fun buttonToAction(view: View, map: Map<Int, Int>) {
            val navy = view.findNavController()
            map.entries.forEach { entry ->
                view.findViewById<Button>(entry.key).setOnClickListener {
                    navy.navigate(entry.value)
                }
            }
        }

        /**
         * Apply [View.OnClickListener] to map of buttons
         */
        fun buttonToListener(view: View, map: Map<Int, View.OnClickListener>) {
            map.entries.forEach { entry ->
                view.findViewById<View>(entry.key).setOnClickListener(entry.value)
            }
        }

    }

}