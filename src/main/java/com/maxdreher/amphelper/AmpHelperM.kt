package com.maxdreher.amphelper

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.core.model.Model

class AmpHelperM : AmpHelperBase<String, ApiException>() {

    init {
        TODO("Not yet implemented")
    }

    val g: (GraphQLResponse<Model>) -> Unit = {
        data = "not null"
    }


    override suspend fun onTimeout(onFail: suspend (ApiException) -> Unit, timeout: Int) {
        TODO("Not yet implemented")
    }
}