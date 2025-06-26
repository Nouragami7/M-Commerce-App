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
    fun `getFavourites returns empty list when no favourites`() = testScope.runTest {
        val collection = mockk<CollectionReference>(relaxed = true)
        every { firestore.collection("users") } returns mockk {
            every { document("test-user-id") } returns mockk {
                every { collection("favourites") } returns collection
            }
        }

        // Simplified listener setup
        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        val mockRegistration = mockk<ListenerRegistration>()
        every { collection.addSnapshotListener(capture(listenerSlot)) } returns mockRegistration

        val results = mutableListOf<List<GetFavouriteProductsByIdsQuery.OnProduct>>()
        val job = launch { favouriteRepository.getFavourites().toList(results) }

        val mockSnapshot = mockk<QuerySnapshot>()
        every { mockSnapshot.documents } returns emptyList()
        listenerSlot.captured.onEvent(mockSnapshot, null)

        testScheduler.advanceUntilIdle()
        job.cancel()

        assertTrue(results.isNotEmpty())
        assertTrue(results.first().isEmpty())
    }

    @Test
    fun `getFavourites returns products from Apollo`() = testScope.runTest {
        val collection = mockk<CollectionReference>(relaxed = true)
        every { firestore.collection("users") } returns mockk {
            every { document("test-user-id") } returns mockk {
                every { collection("favourites") } returns collection
            }
        }

        // Simplified listener setup
        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        val mockRegistration = mockk<ListenerRegistration>()
        every { collection.addSnapshotListener(capture(listenerSlot)) } returns mockRegistration

        val product1 = mockk<GetFavouriteProductsByIdsQuery.OnProduct>()
        val product2 = mockk<GetFavouriteProductsByIdsQuery.OnProduct>()
        val response = mockk<ApolloResponse<GetFavouriteProductsByIdsQuery.Data>>()
        val node1 = mockk<GetFavouriteProductsByIdsQuery.Node> {
            every { onProduct } returns product1
        }
        val node2 = mockk<GetFavouriteProductsByIdsQuery.Node> {
            every { onProduct } returns product2
        }
        every { response.data?.nodes } returns listOf(node1, node2)
        coEvery {
            apolloClient.query(any<GetFavouriteProductsByIdsQuery>()).execute()
        } returns response

        val results = mutableListOf<List<GetFavouriteProductsByIdsQuery.OnProduct>>()
        val job = launch { favouriteRepository.getFavourites().toList(results) }

        val mockSnapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<QueryDocumentSnapshot> {
            every { getString("productId") } returns "prod-1"
        }
        val doc2 = mockk<QueryDocumentSnapshot> {
            every { getString("productId") } returns "prod-2"
        }
        every { mockSnapshot.documents } returns listOf(doc1, doc2)

        listenerSlot.captured.onEvent(mockSnapshot, null)

        testScheduler.advanceUntilIdle()
        job.cancel()

        assertTrue(results.isNotEmpty())
        assertEquals(2, results.first().size)
        assertEquals(product1, results.first()[0])
        assertEquals(product2, results.first()[1])
    }

    @Test
    fun `getFavourites handles null Apollo response`() = testScope.runTest {
        val collection = mockk<CollectionReference>(relaxed = true)
        every { firestore.collection("users") } returns mockk {
            every { document("test-user-id") } returns mockk {
                every { collection("favourites") } returns collection
            }
        }

        // Simplified listener setup
        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        val mockRegistration = mockk<ListenerRegistration>()
        every { collection.addSnapshotListener(capture(listenerSlot)) } returns mockRegistration

        val response = mockk<ApolloResponse<GetFavouriteProductsByIdsQuery.Data>>()
        every { response.data } returns null
        coEvery {
            apolloClient.query(any<GetFavouriteProductsByIdsQuery>()).execute()
        } returns response

        val results = mutableListOf<List<GetFavouriteProductsByIdsQuery.OnProduct>>()
        val job = launch { favouriteRepository.getFavourites().toList(results) }

        val mockSnapshot = mockk<QuerySnapshot>()
        val doc = mockk<QueryDocumentSnapshot> {
            every { getString("productId") } returns "prod-1"
        }
        every { mockSnapshot.documents } returns listOf(doc)

        listenerSlot.captured.onEvent(mockSnapshot, null)

        testScheduler.advanceUntilIdle()
        job.cancel()

        assertTrue(results.isNotEmpty())
        assertTrue(results.first().isEmpty())
    }

    @Test
    fun `getFavourites handles Firestore errors`() = testScope.runTest {
        val collection = mockk<CollectionReference>(relaxed = true)
        every { firestore.collection("users") } returns mockk {
            every { document("test-user-id") } returns mockk {
                every { collection("favourites") } returns collection
            }
        }

        // Simplified listener setup
        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        val mockRegistration = mockk<ListenerRegistration>()
        every { collection.addSnapshotListener(capture(listenerSlot)) } returns mockRegistration

        val results = mutableListOf<List<GetFavouriteProductsByIdsQuery.OnProduct>>()
        val job = launch { favouriteRepository.getFavourites().toList(results) }

        val exception = mockk<FirebaseFirestoreException>()
        listenerSlot.captured.onEvent(null, exception)

        testScheduler.advanceUntilIdle()
        job.cancel()

        assertTrue(results.isEmpty())
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