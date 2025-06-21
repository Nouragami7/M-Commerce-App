package com.example.buyva.data.repository.favourite

import android.net.Uri
import com.apollographql.apollo3.ApolloClient
import com.example.buyva.GetFavouriteProductsByIdsQuery
import com.example.buyva.GetFavouriteProductsByIdsQuery.OnProduct // CORRECT IMPORT
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class FavouriteRepositoryImpl(
    private val apolloClient: ApolloClient
) : FavouriteRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("favourites")

    override suspend fun addFavourite(productId: String) {
        val encodedId = Uri.encode(productId)
        collection.document(encodedId).set(mapOf("productId" to productId))
    }

    override suspend fun removeFavourite(productId: String) {
        val encodedId = Uri.encode(productId)
        collection.document(encodedId).delete()
    }

    override fun getFavourites(): Flow<List<GetFavouriteProductsByIdsQuery.OnProduct>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val ids = snapshot.documents.mapNotNull { it.getString("productId") }
                trySend(ids)
            }
        }
        awaitClose { listener.remove() }
    }.map { ids ->
        if (ids.isEmpty()) emptyList()
        else {
            val response = apolloClient.query(GetFavouriteProductsByIdsQuery(ids)).execute()
            response.data?.nodes?.mapNotNull { it?.onProduct } ?: emptyList()
        }
    }
}