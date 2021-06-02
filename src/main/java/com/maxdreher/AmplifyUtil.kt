package com.maxdreher

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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.reflect.KClass

fun DataStoreException.get(): String? {
    printStackTrace()
    return this.message
}

fun QueryField.inList(items: List<Model>): QueryPredicate {
    return items.map { this.eq(it.id) as QueryPredicate }.reduce { a, n -> a.or(n) }
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
            com.amplifyframework.core.Amplify.DataStore.save(model, g, b)
            afterWaitSuspense(
                {
                    cb.log("[$batch] [$index] Saved ${model.modelName} ${model.id}")
                }, { errors.invoke(model, it, batch) })
        }
    }
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
        com.amplifyframework.core.Amplify.DataStore.delete(
            this@delete,
            { g.call() },
            b
        )
        afterWait({ onSuccess.invoke() }, onFail)
    }
}

inline fun <reified T : Model> KClass<T>.delete(
    queryPredicate: QueryPredicate,
    noinline onSuccess: (String) -> Unit,
    noinline onFail: (DataStoreException) -> Unit,
) {
    AmpHelperD().apply {
        com.amplifyframework.core.Amplify.DataStore.delete(
            T::class.java,
            queryPredicate,
            g,
            b
        )
        afterWait(onSuccess, onFail)
    }
}

suspend inline fun <reified T : Model> T.saveSuspend(): Suspend<T> {
    AmpHelper<T>().apply {
        com.amplifyframework.core.Amplify.DataStore.save(this@saveSuspend, g, b)
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
        com.amplifyframework.core.Amplify
            .DataStore.save(this@save, g, b)
        afterWait(onSave, onFail)
    }
}

fun <T : Model> T.save(context: IContextBase, onSave: (T) -> Unit = {}) {
    AmpHelper<T>().apply {
        com.amplifyframework.core.Amplify.DataStore.save(this@save, g, b)
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

private fun onCantSave(
    context: IContextBase,
    model: Model? = null,
    ex: Exception? = null
) {
    with(context) {
        toast("Could not save model ${model?.modelName}")
        ex?.printStackTrace()
    }
}

suspend inline fun <reified T : Model> KClass<T>.query(queryPredicate: QueryPredicate): SuspendQuery<T> {
    return query(Where.matches(queryPredicate))
}

suspend inline fun <reified T : Model> KClass<T>.query(predicate: QueryOptions): SuspendQuery<T> {
    return AmpHelperQ<T>().run {
        var result: SuspendQuery<T>? = null
        com.amplifyframework.core.Amplify.DataStore.query(
            T::class.java,
            predicate,
            g,
            b
        )
        afterWaitSuspense({
            result = SuspendQuery(it)
        }, {
            result = SuspendQuery(it)
        })
        result!!
    }
}


inline fun <reified T : Model> KClass<T>.query(
    predicate: QueryPredicate,
    noinline onSuccess: (List<T>) -> Unit,
    noinline onFail: (DataStoreException) -> Unit
) {
    AmpHelperQ<T>().apply {
        com.amplifyframework.core.Amplify.DataStore.query(
            T::class.java,
            predicate,
            g,
            b
        )
        afterWait(onSuccess, onFail)
    }
}

inline fun <reified T : Model> KClass<T>.query(
    options: QueryOptions,
    noinline onSuccess: (List<T>) -> Unit,
    noinline onFail: (DataStoreException) -> Unit
) {
    AmpHelperQ<T>().apply {
        com.amplifyframework.core.Amplify.DataStore.query(T::class.java, options, g, b)
        afterWait(onSuccess, onFail)
    }
}