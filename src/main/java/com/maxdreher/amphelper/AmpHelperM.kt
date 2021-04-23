package com.maxdreher.amphelper

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.core.model.Model

class AmpHelperM : AmpHelperBase<String, ApiException>() {
    val g: (GraphQLResponse<Model>) -> Unit = {
        data = "not null"
    }
}