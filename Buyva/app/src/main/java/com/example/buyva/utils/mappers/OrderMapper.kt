
import com.apollographql.apollo3.api.Optional
import com.example.buyva.admin.type.DraftOrderInput
import com.example.buyva.admin.type.DraftOrderLineItemInput
import com.example.buyva.admin.type.MailingAddressInput
import com.example.buyva.data.model.OrderItem

fun OrderItem.toDraftOrderInput(): DraftOrderInput {
    val lineItems = cartItems.map {
        DraftOrderLineItemInput(
            title = Optional.Present(it.title),
            quantity = it.quantity,
            variantId = Optional.Present(it.variantId),
            originalUnitPrice = Optional.Present(it.price.toString())
        )
    }

    val noteBuilder = StringBuilder()
    cartItems.forEachIndexed { index, item ->
        noteBuilder.append("Item ${index + 1}: ${item.title}, Image: ${item.imageUrl}\n")
    }
    val note = noteBuilder.toString().trim()


    return DraftOrderInput(
        email = Optional.Present(email),
        shippingAddress = Optional.Present(
            MailingAddressInput(
                firstName = Optional.Present(address.firstName),
                lastName = Optional.Present(address.lastName),
                address1 = Optional.Present(address.address1),
                address2 = Optional.Present(address.address2),
                city = Optional.Present(address.city),
                country = Optional.Present(address.country),
                phone = Optional.Present(address.phone)
            )
        ),
        lineItems = Optional.Present(lineItems),
        tags = Optional.Present(listOf("Paid with Stripe")),
        note = Optional.Present(note),

    )
}
