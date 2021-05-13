package com.maxdreher

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import androidx.navigation.findNavController
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.extensions.IContextBase
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


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
        fun buttonToListener(view: View, map: Map<Int, () -> Unit>) {
            map.entries.forEach { entry ->
                view.findViewById<View>(entry.key).setOnClickListener { entry.value.invoke() }
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

        fun getSaneDate(date: Date = Date()): String {
            return saneDateFormat.format(date)
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