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
import com.example.buyva.GetProductsByCategoryQuery
import com.example.buyva.ProductsByCollectionQuery
import com.example.buyva.data.model.FavouriteProduct
import com.example.buyva.utils.components.AnimatedProductItem

@Composable
fun ProductSection(
    products: List<*>,
    onProductClick: (String) -> Unit
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
                    when (product) {
                        is BrandsAndProductsQuery.Node -> {
                            key(product.id) {
                                AnimatedProductItem(
                                    id = product.id,
                                    index = index,
                                    product = product,
                                    onProductClick = onProductClick,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        is ProductsByCollectionQuery.Node -> {
                            key(product.id) {
                                AnimatedProductItem(
                                    id = product.id,
                                    index = index,
                                    product = product,
                                    onProductClick = onProductClick,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        is GetProductsByCategoryQuery.Node -> {
                            key(product.id) {
                                AnimatedProductItem(
                                    id = product.id,
                                    index = index,
                                    product = product,
                                    onProductClick = onProductClick,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        is FavouriteProduct -> {
                            key(product.id) {
                                AnimatedProductItem(
                                    id = product.id,
                                    index = index,
                                    product = product,
                                    onProductClick = onProductClick
                                )
                            }
                        }

                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}





