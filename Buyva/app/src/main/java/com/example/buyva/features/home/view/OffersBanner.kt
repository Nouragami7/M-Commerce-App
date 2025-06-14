import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
import com.example.buyva.data.model.OfferBannerItem
import com.example.buyva.ui.theme.Cold

@Composable
fun OfferBanner() {
    val bannerItems = listOf(
        OfferBannerItem(R.drawable.offer_one, "Use Code: BUY20"),
        OfferBannerItem(R.drawable.offer_one, "Use Code: BUY30"),
        OfferBannerItem(R.drawable.offer_one, "Use Code: BUY50")
    )

    val pagerState = rememberPagerState(pageCount = { bannerItems.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            val banner = bannerItems[page]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = banner.imageRes),
                    contentDescription = "Sale Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(bottomEnd = 12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = banner.couponText,
                        color = Color.White,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(bannerItems.size) { index ->
                val color = if (pagerState.currentPage == index) Cold else Color.Gray.copy(alpha = 0.4f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}
