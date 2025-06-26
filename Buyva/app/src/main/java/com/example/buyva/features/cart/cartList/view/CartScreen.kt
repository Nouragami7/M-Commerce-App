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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buyva.BuildConfig
import com.example.buyva.R
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.SelectedOption
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.features.cart.cartList.view.CartItemRow
import com.example.buyva.features.cart.cartList.view.PaymentSection
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModel
import com.example.buyva.features.cart.cartList.viewmodel.PaymentViewModel
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Teal
import com.example.buyva.utils.components.CustomAlertDialog
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.LoadingIndicator
import com.example.buyva.utils.functions.createOrder
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
    onNavigateToProductInfo: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }


    val viewModel: CartViewModel = hiltViewModel()
    val paymentViewModel: PaymentViewModel = hiltViewModel()
    val defaultAddress by viewModel.defaultAddress.collectAsState()

    val orderState by paymentViewModel.order.collectAsState()
    val cartState by viewModel.cartProducts.collectAsState()
    val cartItems = remember {
        mutableStateListOf<CartItem>()
    }
    var quantityVersion by remember { mutableStateOf(0) }

    val totalPrice = remember(quantityVersion) {
        cartItems.sumOf { it.price * it.quantity }
    }

    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
        viewModel.showCart()
        viewModel.loadDefaultAddress()
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        CurrencyManager.loadFromPreferences()
    }


//
//    val totalPrice by remember(cartItems) {
//        derivedStateOf {
//            cartItems.sumOf { it.price * it.quantity }
//        }
//    }


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


    val paymentSheet = rememberPaymentSheet(paymentResultCallback = { result ->
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
                Toast.makeText(
                    context, "Payment Failed: ${result.error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    })


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
                    Log.d("1", "Cart is loading...")
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        LoadingIndicator()
                    }
                }

                is ResponseState.Failure -> {
                    cartItems.clear()
                    val message = (cartState as ResponseState.Failure).message.message
                    Log.e("1", "Cart error: $message")
                    if (message != null) {
                        EmptyScreen(message, 28.sp, R.raw.emptycart)
                    }
                }

                is ResponseState.Success<*> -> {
                    val data = (cartState as ResponseState.Success<*>).data
                    val items = (data as? List<*>)?.filterIsInstance<CartItem>().orEmpty()
                    cartItems.clear()
                    cartItems.addAll(items)

                    if (cartItems.isNotEmpty()) {
                        if (quantityVersion == 0) {
                            quantityVersion++
                        }
                        Log.i("1", "CartScreen not empty")

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(cartItems, key = { it.lineId }) { item ->
//                                val index = cartItems.indexOfFirst { it.id == item.id }
//                                if (index != -1) {
//                                    cartItems[index] = cartItems[index].copy(quantity = item.quantity)
//                                    quantityVersion++
//                                }
//                                quantityVersion++
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
                                                        quantityVersion++
                                                    }
                                                }
                                            , onNavigateToProductInfo = onNavigateToProductInfo
                                        )})


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
                    } else {
                        Log.i("1", "CartScreen empty")
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
                // viewModel.showCart()
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

fun calcualtions(
){

}