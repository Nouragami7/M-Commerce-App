import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.data.model.DiscountBanner
import com.example.buyva.utils.sharedpreference.currency.CurrencyManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherSection(
    price: Float,
    discountBanners: List<DiscountBanner>,
    modifier: Modifier = Modifier,
    onDiscountApplied: (Float) -> Unit
) {
    var voucherCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(discountBanners) {
        println("Discount banners updated: $discountBanners")
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = voucherCode,
                onValueChange = {
                    voucherCode = it
                    errorMessage = null
                    successMessage = null
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter voucher code") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF006A71),
                    unfocusedIndicatorColor = Color.LightGray,
                ),
                singleLine = true
            )

            Button(
                onClick = {
                    val code = voucherCode.trim()
                    if (code.isEmpty()) {
                        errorMessage = "Please enter a voucher code"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null
                    successMessage = null

                    val matchedDiscount = discountBanners.firstOrNull {
                        it.code.trim().equals(code, ignoreCase = true)
                    }

                    if (matchedDiscount != null) {
                        val discountValue = price * (matchedDiscount.percentage / 100f)
                        val newPrice = price - discountValue
                        successMessage = "${matchedDiscount.percentage}% discount applied!"
                        onDiscountApplied(newPrice)
                    } else {
                        errorMessage = "Invalid voucher code"
                        onDiscountApplied(price)
                    }
                    isLoading = false
                },
                modifier = Modifier.size(height = 56.dp, width = 100.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("APPLY")
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        if (successMessage != null) {
            Text(
                text = successMessage!!,
                color = Color(0xFF388E3C),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}