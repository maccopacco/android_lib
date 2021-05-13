package com.maxdreher

import android.widget.DatePicker
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.QueryOptions
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.datastore.DataStoreException
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.amphelper.AmpHelperD
import com.maxdreher.amphelper.AmpHelperQ
import com.maxdreher.amphelper.SuspendedQueryResult
import com.maxdreher.extensions.IContextBase
import java.lang.Exception
import java.util.*
import kotlin.reflect.KClass

fun DatePicker.getDate(): Date {
    return Util.getDateFromDatePicker(this)
}

inline fun <reified T : Model> KClass<T>.deleteAll(
    noinline onSuccess: (String) -> Unit,
    noinline onFail: (DataStoreException) -> Unit
) {
    return delete(QueryPredicates.all(), onSuccess, onFail)
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

suspend inline fun <reified T : Model> KClass<T>.query(queryPredicate: QueryPredicate): SuspendedQueryResult<T> {
    return query(Where.matches(queryPredicate))
}

suspend inline fun <reified T : Model> KClass<T>.query(predicate: QueryOptions): SuspendedQueryResult<T> {
    return AmpHelperQ<T>().run {
        var result: SuspendedQueryResult<T>? = null
        Amplify.DataStore.query(T::class.java, predicate, g, b)
        afterWaitSuspense({
            result = SuspendedQueryResult(it)
        }, {
            result = SuspendedQueryResult(it)
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