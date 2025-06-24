package com.example.buyva.data.datasource.remote.graphql

import com.apollographql.apollo3.ApolloClient
import com.example.buyva.utils.constants.AdminBase_Url

object ApolloAdmin {
    val admin: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(AdminBase_Url)
            .addHttpHeader("X-Shopify-Access-Token", "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")
            .addHttpHeader("Content-Type", "application/json")
            .build()
    }
}
