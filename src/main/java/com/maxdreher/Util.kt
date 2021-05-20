package com.maxdreher

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.navigation.findNavController
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.QueryOptions
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.datastore.DataStoreException
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.amphelper.AmpHelperD
import com.maxdreher.amphelper.AmpHelperQ
import com.maxdreher.amphelper.suspense.Suspend
import com.maxdreher.amphelper.suspense.SuspendQuery
import com.maxdreher.extensions.IContextBase
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass


/**
 * Utility class
 */
class Util {
    companion object {
        val saneDateFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS")
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")

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
        fun View.buttonToListener(map: Map<Int, () -> Unit>) {
            map.entries.forEach { entry ->
                findViewById<View>(entry.key).setOnClickListener { entry.value.invoke() }
            }
        }


        /**
         * Convert [uri] to [Drawable]
         */
        fun uriToDrawable(uri: Uri?): Drawable? {
            return uri?.let {
                urlToDrawable(it.path)
            }
        }

        /**
         * Convert [url] to [Drawable]
         */
        fun urlToDrawable(url: String?): Drawable? {
            return try {
                val `is`: InputStream = URL(url).content as InputStream
                Drawable.createFromStream(`is`, "src name")
            } catch (e: Exception) {
                null
            }
        }

        /**
         * @param datePicker [DatePicker] to get [Date] from
         * @return [Date]
         */
        fun getDateFromDatePicker(datePicker: DatePicker): Date {
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year
            val calendar = Calendar.getInstance()
//            calendar[year, month] = day
            calendar.set(year, month, day)
            return calendar.time
        }

        /**
         * Save 2d list of [Model]s
         */
        suspend fun saveModels(
            cb: IContextBase,
            list: List<List<Model>>
        ) {
            list.withIndex().map { (index, modelList) ->
                GlobalScope.async {
                    saveModels(cb, modelList, index)
                }
            }.awaitAll()
        }

        /**
         * Save [Model]s ([list]) sequentially
         *
         * @param batch models are typically saved in batches (all A,B,Cs that relate), this gives a
         * unique printout
         * @param errors error consumer on
         */
        suspend fun saveModels(
            cb: IContextBase,
            list: List<Model>,
            batch: Int,
            errors: (Model, Exception, Int) -> Unit =
                { model, error, consumerBatch ->
                    cb.loge("Couldn't save ${model.modelName} of batch $consumerBatch\n${error.message}")
                    error.printStackTrace()
                }
        ) {
            for ((index, model) in list.withIndex()) {
                AmpHelper<Model>().apply {
                    Amplify.DataStore.save(model, g, b)
                    afterWaitSuspense(
                        {
                            cb.log("[$batch] [$index] Saved ${model.modelName} ${model.id}")
                        }, { errors.invoke(model, it, batch) })
                }
            }
        }

        /**
         * Get random [Int] between [min] and [max]
         */
        fun getRandInt(min: Int, max: Int): Int {
            return min + (Math.random() * (max - min + 1)).toInt()
        }

        fun getSaneDate(): String {
            return Date().toSaneDate()
        }

        fun startLogging(cb: IContextBase): Boolean {
            val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(Date())
            try {
                val dir = "/data/data/${cb.getContext()?.applicationInfo?.packageName}/logs"
                Runtime.getRuntime()
                    .exec("mkdir $dir")
                Runtime.getRuntime().exec("logcat -f $dir/$date.txt")
                cb.log("Started logging")
                return true
            } catch (e: IOException) {
                cb.loge("Could not start logging")
            }
            return false
        }
    }
}

private fun Boolean.wrapToInt(): Int {
    return if (this) {
        ViewGroup.LayoutParams.WRAP_CONTENT
    } else {
        ViewGroup.LayoutParams.MATCH_PARENT
    }
}

fun Date.toSimpleDate(): String {
    return Util.simpleDateFormat.format(this)
}

fun Date.toSaneDate(): String {
    return Util.saneDateFormat.format(this)
}

fun String.toSaneDate(): Date? {
    return try {
        Util.saneDateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun View.setMargin(wrapHorizontal: Boolean, wrapVertical: Boolean, margin: Int) {
    layoutParams =
        LinearLayout.LayoutParams(wrapHorizontal.wrapToInt(), wrapVertical.wrapToInt()).apply {
            setMargins(margin, margin, margin, margin)
        }
}

fun ViewGroup.setMargin(wrapHorizontal: Boolean, wrapVertical: Boolean, margin: Int) {
    layoutParams =
        ViewGroup.MarginLayoutParams(wrapHorizontal.wrapToInt(), wrapVertical.wrapToInt())
            .apply {
                setMargins(margin, margin, margin, margin)
            }
}

inline fun <T> Iterable<T>.onlyOne(predicate: (T) -> Boolean): Boolean {
    var oneFound = false
    forEach { item ->
        if (item.let(predicate)) {
            if (oneFound) {
                return false
            }
            oneFound = true
        }
    }
    return oneFound
}

fun DatePicker.getDate(): Date {
    return Util.getDateFromDatePicker(this)
}

fun QueryField.inList(items: List<Model>): QueryPredicate {
    return items.map { this.eq(it.id) as QueryPredicate }.reduce { a, n -> a.or(n) }
}

inline fun <reified T : Model> KClass<T>.deleteAll(
    noinline onSuccess: (String) -> Unit,
    noinline onFail: (DataStoreException) -> Unit
) {
    return delete(QueryPredicates.all(), onSuccess, onFail)
}

inline fun <reified T : Model> T.delete(
    noinline onSuccess: () -> Unit,
    noinline onFail: (DataStoreException) -> Unit
) {
    AmpHelperD().apply {
        Amplify.DataStore.delete(
            this@delete,
            { onSuccess.invoke() },
            onFail
        )
    }
}

inline fun <reified T : Model> KClass<T>.delete(
    queryPredicate: QueryPredicate,
    noinline onSuccess: (String) -> Unit,
    noinline onFail: (DataStoreException) -> Unit,
) {
    AmpHelperD().apply {
        Amplify.DataStore.delete(T::class.java, queryPredicate, g, b)
        afterWait(onSuccess, onFail)
    }
}

inline fun <reified T : Model> KClass<T>.query(
    predicate: QueryPredicate,
    noinline onSuccess: (List<T>) -> Unit,
    noinline onFail: (DataStoreException) -> Unit
) {
    AmpHelperQ<T>().apply {
        Amplify.DataStore.query(T::class.java, predicate, g, b)
        afterWait(onSuccess, onFail)
    }
}

suspend inline fun <reified T : Model> KClass<T>.query(queryPredicate: QueryPredicate): SuspendQuery<T> {
    return query(Where.matches(queryPredicate))
}

suspend inline fun <reified T : Model> KClass<T>.query(predicate: QueryOptions): SuspendQuery<T> {
    return AmpHelperQ<T>().run {
        var result: SuspendQuery<T>? = null
        Amplify.DataStore.query(T::class.java, predicate, g, b)
        afterWaitSuspense({
            result = SuspendQuery(it)
        }, {
            result = SuspendQuery(it)
        })
        result!!
    }
}

inline fun <reified T : Model> KClass<T>.query(
    options: QueryOptions,
    noinline onSuccess: (List<T>) -> Unit,
    noinline onFail: (DataStoreException) -> Unit
) {
    AmpHelperQ<T>().apply {
        Amplify.DataStore.query(T::class.java, options, g, b)
        afterWait(onSuccess, onFail)
    }
}

suspend inline fun <reified T : Model> T.saveSuspend(): Suspend<T> {
    AmpHelper<T>().apply {
        Amplify.DataStore.save(this@saveSuspend, g, b)
        var ret: Suspend<T>? = null
        afterWaitSuspense({
            ret = Suspend(it)
        }, {
            ret = Suspend(it)
        })
        return ret!!
    }
}

inline fun <reified T : Model> T.save(
    noinline onSave: (T) -> Unit = {},
    noinline onFail: (DataStoreException) -> Unit = {}
) {
    AmpHelper<T>().apply {
        Amplify
            .DataStore.save(this@save, g, b)
        afterWait(onSave, onFail)
    }
}

fun <T : Model> T.save(context: IContextBase, onSave: (T) -> Unit = {}) {
    AmpHelper<T>().apply {
        Amplify.DataStore.save(this@save, g, b)
        afterWait(
            {
                context.log("${it.modelName} ${it.id} saved")
                onSave.invoke(it)
            },
            { ex ->
                onCantSave(context, this@save, ex)
            })
    }
}

private fun onCantSave(context: IContextBase, model: Model? = null, ex: Exception? = null) {
    with(context) {
        toast("Could not save model ${model?.modelName}")
        ex?.printStackTrace()
    }
}