import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Sea

@Composable
fun SearchBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .shadow(2.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Sea,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Cold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBuyVaHomeScreen() {
    SearchBar()

}
