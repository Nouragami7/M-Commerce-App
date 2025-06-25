
import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.buyva.BuildConfig
import com.example.buyva.R
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.datasource.remote.stripe.StripeClient
import com.example.buyva.data.repository.adresses.AddressRepoImpl
import com.example.buyva.data.repository.cart.CartRepoImpl
import com.example.buyva.data.repository.paymentRepo.PaymentRepoImpl
import com.example.buyva.features.cart.cartList.view.CartItemRow
import com.example.buyva.features.cart.cartList.view.PaymentSection
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModel
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModelFactory
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModel
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModelFactory
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Teal
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.functions.createOrder
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToProductInfo: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }


    val application = context.applicationContext as Application
    val cartRepo = CartRepoImpl(RemoteDataSourceImpl(ApolloService.client,StripeClient.api), SharedPreferenceImpl)
    val addressRepo = AddressRepoImpl(RemoteDataSourceImpl(ApolloService.client,StripeClient.api))
    val viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(application, cartRepo, addressRepo)
    )
    val paymentViewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(
            PaymentRepoImpl(com.example.buyva.data.datasource.remote.RemoteDataSourceImpl(ApolloService.client,StripeClient.api))
        )
    )

    val defaultAddress by viewModel.defaultAddress.collectAsState()

    val orderState by paymentViewModel.order.collectAsState()
    val cartState by viewModel.cartProducts.collectAsState()
    val cartItems = remember {
        mutableStateListOf<CartItem>()
    }


    LaunchedEffect(cartState) {
        when (cartState) {
            is ResponseState.Success<*> -> {
                val data = (cartState as ResponseState.Success<*>).data
                if (data is List<*>) {
                    cartItems.clear()
                    cartItems.addAll(data.filterIsInstance<CartItem>())
                } else {
                    Log.e("CartDebug", "Unexpected data type: ${data?.javaClass}")
                }
            }
            is ResponseState.Failure -> {
                Log.e("CartDebug", "Failed to load cart: ${(cartState as ResponseState.Failure).message}")
            }
            is ResponseState.Loading -> {
                cartItems.clear()
                Log.d("CartDebug", "Cart is loading...")
            }
        }
    }


    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
        viewModel.showCart()
        viewModel.loadDefaultAddress()
         PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        CurrencyManager.loadFromPreferences()


    }


    val totalPrice by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { it.price * it.quantity }
        }
    }









    LaunchedEffect(orderState) {
        when (orderState) {
            is ResponseState.Success<*> -> {
                val fullMessage = (orderState as ResponseState.Success<*>).data.toString()

                val regex = Regex("gid://shopify/DraftOrder/\\d+")
                val match = regex.find(fullMessage)
                val draftOrderId = match?.value

                if (!draftOrderId.isNullOrBlank()) {
                    paymentViewModel.completeDraftOrder(draftOrderId)
                    viewModel.clearCart()
                } else {
                    Log.e("DraftOrder", "Failed to extract draft order ID")
                }

                Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
            }

            is ResponseState.Failure -> {
                Toast.makeText(context, "Failed to place order.", Toast.LENGTH_SHORT).show()
            }

            ResponseState.Loading -> {
                Log.d("DraftOrder", "Creating draft order...")
            }
        }
    }


   val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                createOrder(cartItems, defaultAddress, paymentViewModel, context)
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                    onNavigateToOrders()
                }
                is PaymentSheetResult.Canceled -> {
                    onNavigateToOrders()
                    Toast.makeText(context, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                }
                is PaymentSheetResult.Failed -> {
                    onNavigateToOrders()
                    Toast.makeText(context, "Payment Failed: ${result.error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )


    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart",
                        style = MaterialTheme.typography.titleLarge,
                        color = Cold,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                    )
                },
                navigationIcon = {

                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Cold
                        )


                    }
                }
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (cartState) {
                is ResponseState.Loading -> {
                    cartItems.clear()
                    CircularProgressIndicator(color = Cold)
                }

                is ResponseState.Failure -> {
                    val message = (cartState as ResponseState.Failure).message.message ?: "An error occurred"
                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    cartItems.clear()
                }

                is ResponseState.Success<*> -> {
                    if (!cartItems.isEmpty()) {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(cartItems, key = { it.id }) { item ->
                                val dismissState =
                                    rememberDismissState(confirmStateChange = { dismissValue ->
                                        if (dismissValue == DismissValue.DismissedToStart || dismissValue == DismissValue.DismissedToEnd) {
                                            itemToDelete = item
                                            showDeleteDialog = true
                                            false
                                        } else false
                                    })

                                SwipeToDismiss(
                                    state = dismissState,
                                    directions = setOf(DismissDirection.EndToStart),
                                    background = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Teal
                                            )
                                        }
                                    },
                                    dismissContent = {
                                        CartItemRow(
                                            item = item,
                                            onQuantityChange = { newQty ->
                                                val index = cartItems.indexOfFirst { it.id == item.id }
                                                if (index != -1) {
                                                    cartItems[index] = cartItems[index].copy(quantity = newQty)
                                                }
                                            },
                                            onNavigateToProductInfo = onNavigateToProductInfo
                                        )
                                    }
                                )
                            }
                        }

                        Text(
                            text = "Total: ${CurrencyManager.currencyUnit.value} %.2f".format(CurrencyManager.currencyRate.value*totalPrice),
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 25.sp,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                            color = Cold
                        )

                        Button(
                            onClick = {
                                showSheet = true
                                scope.launch { sheetState.show() }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Cold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Proceed to Payment", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(56.dp))
                    }else{

                        EmptyScreen("No items in the cart", 28.sp, R.raw.emptycart)

                    }
                }
            }
        }

    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            tonalElevation = 10.dp
        ) {
            PaymentSection(price = totalPrice, address = defaultAddress ?: Address(
                firstName = "choose default address",
                lastName = "",
                address1 = "",
                city = "",
                country = "",
                address2 = "",
                phone = ""
            ), onConfirm = { _, _, _ ->
                showSheet = false
                Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
            }, onPayWithCardClick = {
                paymentViewModel.initiatePaymentFlow(
                    amount = (totalPrice * 100).toInt(),
                    onClientSecretReady = { secret ->
                        paymentSheet.presentWithPaymentIntent(
                            paymentIntentClientSecret = secret,
                            configuration = PaymentSheet.Configuration(
                                merchantDisplayName = "Buyva"
                            )
                        )
                    })
            },  onAddressClick = { onNavigateToAddresses() },
                paymentViewModel = paymentViewModel,
                cartItems = cartItems,
                defaultAddress = defaultAddress

            )
        }
    }

    if (showDeleteDialog && itemToDelete != null) {
        Log.i("1", "CartScreen: $itemToDelete")
        CustomAlertDialog(
            title = "Delete Item",
            message = "Are you sure you want to remove ${itemToDelete?.title} from the cart?",
            onConfirm = {
                Log.i("1", "CartScreen onConfirm: $itemToDelete")
                cartItems.remove(itemToDelete)
                viewModel.removeProductFromCart(itemToDelete!!.lineId)
                showDeleteDialog = false
                itemToDelete = null
            },
            onDismiss = {
                Log.i("1", "CartScreen onDismiss: $itemToDelete")
                showDeleteDialog = false
                itemToDelete = null
            },
            confirmText = "Delete",
            dismissText = "Cancel"
        )
    }
}

