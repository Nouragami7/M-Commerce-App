package com.example.buyva.repository.favourite

import android.net.Uri
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buyva.GetFavouriteProductsByIdsQuery
import com.example.buyva.data.repository.favourite.FavouriteRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.*

class FavouriteRepositoryTest {

    private lateinit var favouriteRepository: FavouriteRepositoryImpl
    private lateinit var apolloClient: ApolloClient
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock dependencies
        apolloClient = mockk(relaxed = true)
        firestore = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)

        // Mock static Firebase methods
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns firestore

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns auth

        // Mock Uri.encode() to return the same string
        mockkStatic(Uri::class)
        every { Uri.encode(any()) } answers { firstArg() }

        // Create repository
        favouriteRepository = FavouriteRepositoryImpl(apolloClient)

        // Mock current user
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "test-user-id"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `addFavourite adds product to Firestore`() = testScope.runTest {
        // Setup
        val productId = "product-123"
        val collection = mockk<CollectionReference>(relaxed = true)
        val document = mockk<DocumentReference>(relaxed = true)

        every { firestore.collection("users") } returns mockk {
            every { document("test-user-id") } returns mockk {
                every { collection("favourites") } returns collection
            }
        }
        every { collection.document(productId) } returns document

        // Execute
        favouriteRepository.addFavourite(productId)

        // Verify
        verify { document.set(mapOf("productId" to productId)) }
    }

    @Test
    fun `removeFavourite deletes product from Firestore`() = testScope.runTest {
        // Setup
        val productId = "product-123"
        val collection = mockk<CollectionReference>(relaxed = true)
        val document = mockk<DocumentReference>(relaxed = true)

        every { firestore.collection("users") } returns mockk {
            every { document("test-user-id") } returns mockk {
                every { collection("favourites") } returns collection
            }
        }
        every { collection.document(productId) } returns document

        // Execute
        favouriteRepository.removeFavourite(productId)

        // Verify
        verify { document.delete() }
    }



    @Test
    fun `getUserCollection throws when user not logged in`() = testScope.runTest {
        // Setup no current user
        every { auth.currentUser } returns null

        // Execute and verify exception
        assertFailsWith<IllegalStateException> {
            favouriteRepository.getFavourites().first()
        }
    }
}