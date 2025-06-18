import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
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
import com.example.buyva.R
import com.example.buyva.data.model.Address
import com.example.buyva.data.model.CartItem
import com.example.buyva.features.cart.view.CartItemRow
import com.example.buyva.features.cart.view.PaymentSection
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Teal
import com.example.buyva.utils.components.CustomAlertDialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.example.buyva.navigation.navbar.NavigationBar
import com.airbnb.lottie.compose.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CartScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }

    val cartItems = remember {
        mutableStateListOf(
            CartItem(R.drawable.bag1, "Bag 1", 2000.0, mutableStateOf(1)),
            CartItem(R.drawable.bag2, "Bag 2", 3500.0, mutableStateOf(2))
        )
    }

    val totalPrice by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { it.price * it.quantity.value }
        }
    }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues).padding(top = 40.dp)
        ) {
            if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LottieAnimation(
                                composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.emptycart)).value,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Your cart is empty", color = Color.Gray)
                        }
                    }
            }
            else {
            LazyColumn(modifier = Modifier.weight(1f)) {

                    items(cartItems, key = { it.name }) { item ->
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
                                    item.quantity.value = newQty
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
                currentDate = LocalDate.now(),
                currentTime = LocalTime.now(),
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
                }
            )
        }
    }

    if (showDeleteDialog && itemToDelete != null) {
        CustomAlertDialog(
            title = "Delete Item",
            message = "Are you sure you want to remove ${itemToDelete?.name} from the cart?",
            onConfirm = {
                cartItems.remove(itemToDelete)
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
