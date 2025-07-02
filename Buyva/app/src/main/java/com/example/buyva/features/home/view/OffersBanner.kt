
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.buyva.R
import com.example.buyva.data.model.DiscountBanner
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.ubuntuMedium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun OfferBanner(banner: List<DiscountBanner>,
                snackbarHostState: SnackbarHostState,
                scope: CoroutineScope
) {
    val pagerState = rememberPagerState(pageCount = { banner.size })
    val imageIds = listOf(
        R.drawable.ten,
        R.drawable.fifteen,
        R.drawable.twenty,
        R.drawable.twentyfive
    )

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) { page ->
                val bannerItem = banner[page]
                val pageOffset =
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                // Extract code from bannerItem.code (e.g. "Use Code: SUMMER10 for 10% Off")
                val extractedCode = Regex("Use Code: (\\w+)").find(bannerItem.code)?.groupValues?.getOrNull(1) ?: bannerItem.code

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val scale = 1f - (0.1f * abs(pageOffset))
                            scaleX = scale
                            scaleY = scale
                            alpha = 1f - (0.3f * abs(pageOffset))
                        }
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    val painter = rememberAsyncImagePainter(model = imageIds[page % imageIds.size])
                    Image(
                        painter =  painter,
                        contentDescription = "Coupon Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(bottomEnd = 12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Use Code: ",
                            color = Color.White,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = ubuntuMedium
                        )
                        SelectionContainer {
                            Text(
                                text = extractedCode,
                                color = Color.White,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = ubuntuMedium
                            )
                        }


                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = {
                            clipboard.setText(AnnotatedString(extractedCode))
                            scope.launch {
                                snackbarHostState.showSnackbar("Copied $extractedCode")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Code",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(banner.size) { index ->
                    val color = if (pagerState.currentPage == index) Cold else Color.Gray.copy(alpha = 0.4f)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }

}
