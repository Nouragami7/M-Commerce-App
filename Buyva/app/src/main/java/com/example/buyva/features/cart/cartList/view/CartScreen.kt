import android.app.Application
import android.os.Build
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.R
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.features.cart.cartList.view.CartItemRow
import com.example.buyva.features.cart.cartList.view.PaymentSection
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Teal
import com.example.buyva.utils.components.CustomAlertDialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.example.buyva.navigation.navbar.NavigationBar
import com.airbnb.lottie.compose.*
import com.apollographql.apollo3.ApolloClient
import com.example.buyva.data.datasource.remote.RemoteDataSourceImpl
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.cart.CartRepo
import com.example.buyva.data.repository.cart.CartRepoImpl
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModel
import com.example.buyva.features.cart.cartList.viewmodel.CartViewModelFactory
import com.example.buyva.utils.components.EmptyScreen
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.sharedpreference.SharedPreference
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick : () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }
    val application = context.applicationContext as Application
    val cartRepo = CartRepoImpl(RemoteDataSourceImpl(ApolloService.client), SharedPreferenceImpl)

    val viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(application, cartRepo)
    )

    val cartState by viewModel.cartProducts.collectAsState()
    val cartItems = remember { mutableStateListOf<CartItem>() }

    LaunchedEffect(cartState) {
        if (cartState is ResponseState.Success<*>) {
            val newItems = (cartState as ResponseState.Success<List<CartItem>>).data
            cartItems.clear()
            cartItems.addAll(newItems)
        }
    }


    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
        viewModel.showCart()
    }

    val totalPrice by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { it.price * it.quantity }
        }
    }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            ScreenTitle("Cart")


            if (cartItems.isEmpty()) {
                    EmptyScreen("No items in the cart", 28.sp, R.raw.emptycart)
            }
            else {
            LazyColumn(modifier = Modifier.weight(1f)) {

                items(cartItems, key = { it.id }) { item ->
                        val dismissState = rememberDismissState(
                            confirmStateChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToStart || dismissValue == DismissValue.DismissedToEnd) {
                                    itemToDelete = item
                                    showDeleteDialog = true
                                    false
                                } else {
                                    false
                                }
                            }
                        )

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
                                CartItemRow(item = item, onQuantityChange = { newQty ->
                                    val index = cartItems.indexOfFirst { it.id == item.id }
                                    if (index != -1) {
                                        cartItems[index] = cartItems[index].copy(quantity = newQty)
                                    }
                                })

                            }
                        )
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
            PaymentSection(
                address = Address(
                    firstName = "Ali",
                    lastName = "Hassan",
                    address = "123 Nile Street",
                    country = "Egypt",
                    city = "Cairo",
                    floorNumber = "2",
                    buildingNumber = "12A",
                    phoneNumber = "01001234567"
                ),
                onConfirm = { _, _, _ ->
                    showSheet = false
                    Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
                },
                onNavigateToCart = onCheckoutClick

            )
        }
    }

    if (showDeleteDialog && itemToDelete != null) {
        CustomAlertDialog(
            title = "Delete Item",
            message = "Are you sure you want to remove ${itemToDelete?.title} from the cart?",
            onConfirm = {
                //cartItems.remove(itemToDelete)
                showDeleteDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                itemToDelete = null
            },
            confirmText = "Delete",
            dismissText = "Cancel"
        )
    }
}
