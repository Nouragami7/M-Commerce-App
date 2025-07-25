package com.example.buyva.data.datasource.remote.graphql

import com.apollographql.apollo3.ApolloClient
import com.example.buyva.utils.constants.Access_Token
import com.example.buyva.utils.constants.ClientBase_Url

object ApolloService {
    val client: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(ClientBase_Url)
            .addHttpHeader("X-Shopify-Storefront-Access-Token", Access_Token)
            .addHttpHeader("Content-Type", "application/json")
            .build()
    }
}
