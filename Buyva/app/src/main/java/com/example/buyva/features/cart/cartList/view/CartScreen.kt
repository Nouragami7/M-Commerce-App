
import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.BuildConfig
import com.example.buyva.R
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.data.model.OrderItem
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.remote.StripeClient
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
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.constants.DEFAULT_ADDRESS_ID
import com.example.buyva.utils.constants.USER_TOKEN
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth
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
    onNavigateToAddresses: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    val token = SharedPreferenceImpl.getFromSharedPreferenceInGeneral(USER_TOKEN)
    val defaultAddressId = SharedPreferenceImpl.getFromSharedPreferenceInGeneral("${DEFAULT_ADDRESS_ID}$token")


    val application = context.applicationContext as Application
    val cartRepo = CartRepoImpl(RemoteDataSourceImpl(ApolloService.client), SharedPreferenceImpl)
    val addressRepo = AddressRepoImpl(RemoteDataSourceImpl(ApolloService.client))
    val viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(application, cartRepo, addressRepo)
    )
    val paymentViewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(
            PaymentRepoImpl(com.example.buyva.data.remote.RemoteDataSourceImpl(StripeClient.api))
        )
    )

    val defaultAddress by viewModel.defaultAddress.collectAsState()

    val orderState by paymentViewModel.order.collectAsState()

    val cartState by viewModel.cartProducts.collectAsState()
    val cartItems = remember {
        mutableStateListOf<CartItem>()
    }

    LaunchedEffect(cartState) {
        Log.d("CartDebug", "cartState = $cartState")
        when (cartState) {
            is ResponseState.Success<*> -> {
                cartItems.clear()
                cartItems.addAll((cartState as ResponseState.Success<List<CartItem>>).data)
            }

            is ResponseState.Failure -> {
                Log.e(
                    "CartDebug",
                    "Failed to load cart: ${(cartState as ResponseState.Failure).message}"
                )
            }

            else -> Unit
        }
    }




    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
        viewModel.showCart()
        viewModel.loadDefaultAddress()

    }



    val totalPrice by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { it.price * it.quantity }
        }
    }

    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
    }

    LaunchedEffect(orderState) {
        when (orderState) {
            is ResponseState.Success<*> -> {
                val fullMessage = (orderState as ResponseState.Success<*>).data.toString()
                Log.d("DraftOrder", "Success message: $fullMessage")

                val regex = Regex("gid://shopify/DraftOrder/\\d+")
                val match = regex.find(fullMessage)
                val draftOrderId = match?.value

                if (!draftOrderId.isNullOrBlank()) {
                    Log.d("DraftOrder", "Extracted ID: $draftOrderId")
                    paymentViewModel.completeDraftOrder(draftOrderId)
                } else {
                    Log.e("DraftOrder", "Failed to extract draft order ID")
                }

                Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
            }

            is ResponseState.Failure -> {
                Log.e("DraftOrder", "Failed to create draft order: ${(orderState as ResponseState.Failure).message}")
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
                Log.d("DraftOrder", " address is  ${defaultAddress?.address1}" +
                        "address2 is ${defaultAddress?.address2}")

                println("hiiiiiiiiiiiiiiiiiiiiiii")

                    Log.d("Stripe", "Payment completed!")

                    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                    if (email.isNotBlank() && cartItems.isNotEmpty() && defaultAddress != null) {
                        Log.d("DraftOrder", "Creating draft order after Stripe payment...")
                        val orderItem = OrderItem(
                            email = email,
                            address = defaultAddress!!,
                            cartItems = cartItems
                        )
                        paymentViewModel.createDraftOrder(orderItem)

                    } else {
                        Log.e("DraftOrder", "Missing data: email=$email, items=${cartItems.size}, address=$defaultAddress")
                        Toast.makeText(context, "Cannot create order: missing data", Toast.LENGTH_SHORT).show()
                    }


                //Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Canceled -> {
              //  onNavigateToOrders()
                Toast.makeText(context, "Payment Cancelled", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Failed -> {
               // onNavigateToOrders()
                Toast.makeText(
                    context,
                    "Payment Failed: ${result.error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    })




    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            ScreenTitle("Cart")


            if (cartItems.isEmpty()) {
                EmptyScreen("No items in the cart", 28.sp, R.raw.emptycart)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {

                    items(cartItems, key = { it.id }) { item ->
                        val dismissState =
                            rememberDismissState(confirmStateChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToStart || dismissValue == DismissValue.DismissedToEnd) {
                                    itemToDelete = item
                                    showDeleteDialog = true
                                    false
                                } else {
                                    false
                                }
                            })

                        SwipeToDismiss(state = dismissState,
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
                                CartItemRow(item = item, onQuantityChange = { newQty ->
                                    val index = cartItems.indexOfFirst { it.id == item.id }
                                    if (index != -1) {
                                        cartItems[index] = cartItems[index].copy(quantity = newQty)
                                    }
                                })

                            })
                    }
                }
            }

            Text(
                text = "Total: EGP %.2f".format(totalPrice),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyLarge,
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
                paymentViewModel.initiatePaymentFlow(amount = (totalPrice * 100).toInt(),
                    onClientSecretReady = { secret ->
                        paymentSheet.presentWithPaymentIntent(
                            paymentIntentClientSecret = secret,
                            configuration = PaymentSheet.Configuration(
                                merchantDisplayName = "Buyva"
                            )
                        )
                    })
            }, onAddressClick = {
                onNavigateToAddresses()
            })
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
                viewModel.removeProductFromCart(itemToDelete!!.id)
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
