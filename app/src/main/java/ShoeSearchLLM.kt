import com.example.ecommerceapp.Models.Category
import com.example.ecommerceapp.Models.ShoesItems
import com.example.ecommerceapp.Models.ShoesResponse

//class ShoeSearchLLM {
//    // Search intent classification
//    enum class SearchIntent {
//        PRICE_ABOVE,
//        PRICE_BELOW,
//        PRICE_EQUAL,
//        STOCK_ABOVE,
//        STOCK_BELOW,
//        CATEGORY,
//        NAME_SEARCH,
//        COMPLEX_SEARCH
//    }
//
//    // Prompt mapping and intent recognition
//    private val priceAbovePatterns = listOf(
//        "price above",
//        "price more than",
//        "price greater than",
//        "price above than",
//        "shoes of price above",
//        "shoes of price more than",
//        "shoes of price greater than",
//        "more than",
//        "greater than",
//        "shoes over"
//    )
//
//    private val priceBelowPatterns = listOf(
//        "price less",
//        "less price",
//        "cheap price",
//        "cheap shoes",
//        "price less than",
//        "price lesser than",
//        "shoes of price less",
//        "shoes of price under",
//        "shoes of price less than",
//        "less than",
//        "more than",
//        "under"
//    )
//
//    private val priceEqualPatterns = listOf(
//        "price equal",
//        "price equal to",
//        "exactly",
//        "cost of",
//        "cost equal",
//        "cost equal to",
//        "price",
//        "cost",
//        "shoes of price"
//    )
//
//    private val stockAbovePatterns = listOf(
//        "stocks greater than",
//        "more stock",
//        "stock above"
//    )
//
//    private val stockBelowPatterns = listOf(
//        "stocks less than",
//        "low stock",
//        "stock below"
//    )
//
//    private val categoryPatterns = listOf(
//        "category",
//        "type of",
//        "shoe type"
//    )
//
//    // Advanced search intent classification
//    fun classifySearchIntent(query: String): SearchIntent {
//        return when {
//            priceAbovePatterns.any { query.contains(it, ignoreCase = true) } ->
//                SearchIntent.PRICE_ABOVE
//
//            priceBelowPatterns.any { query.contains(it, ignoreCase = true) } ->
//                SearchIntent.PRICE_BELOW
//
//            priceEqualPatterns.any { query.contains(it, ignoreCase = true) } ->
//                SearchIntent.PRICE_EQUAL
//
//            stockAbovePatterns.any { query.contains(it, ignoreCase = true) } ->
//                SearchIntent.STOCK_ABOVE
//
//            stockBelowPatterns.any { query.contains(it, ignoreCase = true) } ->
//                SearchIntent.STOCK_BELOW
//
//            categoryPatterns.any { query.contains(it, ignoreCase = true) } ->
//                SearchIntent.CATEGORY
//
//            else -> SearchIntent.NAME_SEARCH
//        }
//    }
//
//    // Extract numeric value from query
//    fun extractNumericValue(query: String): Int {
//        return query.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
//    }
//
//    // Search implementation
//    fun performSearch(shoes: List<ShoesItems>, query: String): List<ShoesItems> {
//        val intent = classifySearchIntent(query)
//        val numericValue = extractNumericValue(query)
//
//        return shoes.filter { shoe ->
//            when (intent) {
//                SearchIntent.PRICE_ABOVE ->
//                    shoe.price.toIntOrNull()?.let { it > numericValue } ?: false
//
//                SearchIntent.PRICE_BELOW ->
//                    shoe.price.toIntOrNull()?.let { it < numericValue } ?: false
//
//                SearchIntent.PRICE_EQUAL ->
//                    shoe.price.toIntOrNull()?.let { it == numericValue } ?: false
//
//                SearchIntent.STOCK_ABOVE ->
//                    shoe.quantity.toIntOrNull()?.let { it > numericValue } ?: false
//
//                SearchIntent.STOCK_BELOW ->
//                    shoe.quantity.toIntOrNull()?.let { it < numericValue } ?: false
//
//                SearchIntent.CATEGORY ->
//                    shoe.name.contains(query, ignoreCase = true)
//
//                SearchIntent.NAME_SEARCH ->
//                    shoe.name.contains(query, ignoreCase = true) ||
//                            shoe.description.contains(query, ignoreCase = true)
//
//                SearchIntent.COMPLEX_SEARCH -> true
//            }
//        }
//    }
//}

class ShoeSearchLLM {
    private var allProducts: List<ShoesItems> = emptyList()
    private var allCategories: List<Category> = emptyList()

    // Initialize data
    fun initializeData(apiResponse: ShoesResponse) {
        allCategories = apiResponse.result
        allProducts = apiResponse.result.flatMap { it.products }
    }

    // Search by name
    fun searchByName(query: String): List<ShoesItems> {
        if (query.isEmpty()) return emptyList()

        return allProducts.filter { product ->
            product.name.contains(query, ignoreCase = true)
        }
    }

    // Search by price range
    fun searchByPriceRange(minPrice: Int, maxPrice: Int): List<ShoesItems> {
        return allProducts.filter { product ->
            val price = product.price.toIntOrNull() ?: 0
            price in minPrice..maxPrice
        }
    }

    // Search by category
    fun searchByCategory(categoryName: String): List<ShoesItems> {
        return allCategories
            .find { it.category.equals(categoryName, ignoreCase = true) }
            ?.products
            ?: emptyList()
    }

    // Advanced search with multiple criteria
    fun advancedSearch(
        query: String = "",
        categoryName: String = "",
        minPrice: Int = 0,
        maxPrice: Int = Int.MAX_VALUE,
        minQuantity: Int = 0
    ): List<ShoesItems> {
        return allProducts.filter { product ->
            val matchesQuery = if (query.isEmpty()) {
                true
            } else {
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }

            val matchesCategory = if (categoryName.isEmpty()) {
                true
            } else {
                allCategories.any {
                    it.category.equals(categoryName, ignoreCase = true) &&
                            it.products.contains(product)
                }
            }

            val price = product.price.toIntOrNull() ?: 0
            val quantity = product.quantity.toIntOrNull() ?: 0

            matchesQuery &&
                    matchesCategory &&
                    price in minPrice..maxPrice &&
                    quantity >= minQuantity
        }
    }

    // Get suggestions based on partial input
    fun getSuggestions(partialInput: String): List<String> {
        if (partialInput.length < 2) return emptyList()

        val suggestions = mutableSetOf<String>()

        allProducts.forEach { product ->
            if (product.name.contains(partialInput, ignoreCase = true)) {
                // Add full product names that match
                suggestions.add(product.name)

                // Add individual words that match
                product.name.split(" ").forEach { word ->
                    if (word.contains(partialInput, ignoreCase = true)) {
                        suggestions.add(word)
                    }
                }
            }
        }

        return suggestions.toList().sorted()
    }

    // Get trending or popular products (based on quantity as a simple metric)
    fun getTrendingProducts(limit: Int = 5): List<ShoesItems> {
        return allProducts
            .sortedByDescending { it.quantity.toIntOrNull() ?: 0 }
            .take(limit)
    }

    // Get products by similar characteristics
    fun getSimilarProducts(productId: Int, limit: Int = 4): List<ShoesItems> {
        val targetProduct = allProducts.find { it.id == productId } ?: return emptyList()
        val targetCategory = allCategories.find { it.products.contains(targetProduct) }

        return allProducts
            .filter { product ->
                product.id != productId && // Exclude the same product
                        (allCategories.find { it.products.contains(product) } == targetCategory) // Same category
            }
            .sortedBy { product ->
                // Simple similarity score based on price difference
                abs(
                    (product.price.toIntOrNull() ?: 0) -
                            (targetProduct.price.toIntOrNull() ?: 0)
                )
            }
            .take(limit)
    }

    private fun abs(value: Int): Int = if (value < 0) -value else value
}

