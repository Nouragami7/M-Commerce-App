import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium

@Composable
fun ProductSection() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "For You",
            style = MaterialTheme.typography.headlineSmall,
            color = Cold,
            fontFamily = ubuntuMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        {
            items(productList) { product ->
                ProductCard(product)
            }
        }
    }
}

data class Product(val name: String, val price: String, val imageRes: Int)

val productList = listOf(
    Product("ADIDAS | CLASSIC", "3200.00 EGP", R.drawable.adidas),
    Product("ADIDAS | CLASSIC", "2250.00 EGP", R.drawable.adidas),
    Product("ADIDAS | CLASSIC", "2250.00 EGP", R.drawable.adidas),
    Product("ADIDAS | CLASSIC", "2250.00 EGP", R.drawable.adidas)


)

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontFamily = ubuntuMedium
            )
            Text(
                text = product.price,
                color = Cold,
                fontSize = 13.sp
            )
        }
    }
}
