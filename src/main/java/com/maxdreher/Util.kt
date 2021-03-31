package com.maxdreher

import android.content.Context
import android.view.View
import android.widget.Button
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

class Util {
    companion object {
        fun getDefaultMargin(context: Context?, id: Int): Int {
            return context!!.resources.getDimension(id).toInt()
        }

        fun buttonToAction(view: View, map: Map<Int, Int>) {
            val navy = view.findNavController()
            map.entries.forEach { entry ->
                view.findViewById<Button>(entry.key).setOnClickListener {
                    navy.navigate(entry.value)
                }
            }
        }

        fun buttonToListener(view: View, map: Map<Int, View.OnClickListener>) {
            map.entries.forEach { entry ->
                view.findViewById<View>(entry.key).setOnClickListener(entry.value)
            }
        }

//        fun <T : Model> ampSave(
//            datastore: DataStoreCategory,
//            item: T,
//            onSuccess: Consumer<T>,
//            onFail: Consumer<DataStoreException>,
//            queryPredicate: QueryPredicate? = null,
//        ) {
//            GlobalScope.launch {
//                var data: T? = null
//                var dataEx: DataStoreException? = null
//
//                val g: (value: DataStoreItemChange<T>) -> Unit = { data = it.item() }
//                val b: (value: DataStoreException) -> Unit = { dataEx = it }
//
//                queryPredicate?.let {
//                    datastore.save(item, it, g, b)
//                } ?: datastore.save(item, g, b)
//
//                while (data == null && dataEx == null) {
//                }
//                withContext(Dispatchers.Main) {
//                    data?.let { onSuccess.accept(it) } ?: dataEx?.let { onFail.accept(it) }
//                }
//            }
//        }

    }

}