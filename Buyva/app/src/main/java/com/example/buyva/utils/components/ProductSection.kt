
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.buyva.BrandsAndProductsQuery
import com.example.buyva.GetFavouriteProductsByIdsQuery
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.features.favourite.viewmodel.FavouriteScreenViewModel
import com.example.buyva.utils.components.AnimatedProductItem

@Composable
fun ProductSection(
    products: List<*>,
    onProductClick: (String) -> Unit,
    favouriteViewModel: FavouriteScreenViewModel ? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val rows = products.chunked(2)

        rows.forEachIndexed { _, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEachIndexed { index, product ->
                    val modifier = Modifier.weight(1f)
                    val id = when (product) {
                        is BrandsAndProductsQuery.Node -> product.id
                        is ProductsByCollectionQuery.Node -> product.id
                        is GetProductsByCategoryQuery.Node -> product.id
                        is GetFavouriteProductsByIdsQuery.OnProduct -> product.id
                        else -> return@forEachIndexed
                    }

                    key(id) {
                        AnimatedProductItem(
                            id = id,
                            index = index,
                            product = product,
                            onProductClick = onProductClick,
                            modifier = modifier,
                            favouriteViewModel = favouriteViewModel
                        )
                    }
                }

                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


