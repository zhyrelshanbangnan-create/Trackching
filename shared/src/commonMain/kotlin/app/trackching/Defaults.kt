package app.trackching

import app.trackching.db.TrackchingDatabase

private class DefaultCategory(
    val name: String,
    val color: Long,
    val subs: List<Pair<String, Long>> = emptyList(),
)

private val DefaultExpense = listOf(
    DefaultCategory(
        "Food & Beverages", 0xFFE07A00,
        listOf("Meals" to 0xFFE07A00, "Coffee" to 0xFFA0662B, "Snacks" to 0xFFC99700, "Groceries" to 0xFF3F9A33),
    ),
    DefaultCategory(
        "Transportation", 0xFF1663FF,
        listOf("Jeep & Bus" to 0xFF1663FF, "Taxi & Grab" to 0xFF1D9BB3),
    ),
    DefaultCategory(
        "Bills & Utilities", 0xFF7B5CD6,
        listOf("Mobile load" to 0xFF7B5CD6),
    ),
)

private val DefaultIncome = listOf(
    DefaultCategory("Salary", 0xFF1B8A3A),
    DefaultCategory("Allowance", 0xFF3A9D6B),
    DefaultCategory(
        "Side income", 0xFF2C8F8F,
        listOf("Freelance" to 0xFF2C8F8F, "Selling" to 0xFFC0569A),
    ),
    DefaultCategory("Gifts received", 0xFFB0478A),
    DefaultCategory(
        "Interest", 0xFF4D7CC9,
        listOf("Bank interest" to 0xFF4D7CC9, "From IOUs" to 0xFF6B93D6),
    ),
    DefaultCategory("Other", 0xFF5C616C),
)

/** Fills an empty database with a starter account and categories. Safe to call on every launch. */
fun TrackchingDatabase.seedDefaults() {
    transaction {
        if (accountQueries.countAll().executeAsOne() == 0L) {
            accountQueries.insert("Cash on hand", 0)
        }
        if (categoryQueries.countAll().executeAsOne() == 0L) {
            var order = 0L
            fun add(list: List<DefaultCategory>, kind: String) {
                list.forEach { main ->
                    categoryQueries.insert(main.name, kind, null, main.color, order++)
                    val parentId = categoryQueries.lastId().executeAsOne()
                    main.subs.forEach { (name, color) ->
                        categoryQueries.insert(name, kind, parentId, color, order++)
                    }
                }
            }
            add(DefaultExpense, "EXPENSE")
            add(DefaultIncome, "INCOME")
        }
    }
}