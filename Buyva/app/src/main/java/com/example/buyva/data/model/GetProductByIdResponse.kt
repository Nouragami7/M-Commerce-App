package com.example.buyva.data.model
data class GetProductByIdResponse(
    val product: Product?
)

data class Product(
    val id: String,
    val title: String,
    val description: String?,
    val productType: String?,
    val vendor: String?,
    val totalInventory: Int?,
    val options: List<ProductOption>,
    val variants: ProductVariantConnection,
    val images: ProductImageConnection,
    val onlineStoreUrl: String?
)

data class ProductOption(
    val name: String,
    val values: List<String>
)

data class ProductVariantConnection(
    val edges: List<ProductVariantEdge>
)

data class ProductVariantEdge(
    val node: ProductVariant
)

data class ProductVariant(
    val id: String,
    val title: String,
    val price: ProductPrice,
    val selectedOptions: List<SelectedOption>,
    val quantityAvailable: Int?,
    val image: ProductImage?
)

data class ProductPrice(
    val amount: String,
    val currencyCode: String
)

data class SelectedOption(
    val name: String,
    val value: String
)

data class ProductImageConnection(
    val edges: List<ProductImageEdge>
)

data class ProductImageEdge(
    val node: ProductImage
)

data class ProductImage(
    val originalSrc: String?
)
