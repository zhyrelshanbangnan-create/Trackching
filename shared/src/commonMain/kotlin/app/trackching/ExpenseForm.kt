package app.trackching

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** The state of the expense sheet: the amount typed, and what's picked. */
class ExpenseForm(accountId: Long?) {
    var typed by mutableStateOf("")
        private set
    var categoryId by mutableStateOf<Long?>(null)
    var accountId by mutableStateOf(accountId)
    var yesterday by mutableStateOf(false)

    val centavos: Long get() = parseCentavos(typed) ?: 0L

    /** SAVE is grey until there's an amount, a category and an account. */
    val canSave: Boolean get() = centavos > 0 && categoryId != null && accountId != null

    fun onKey(key: Key) {
        typed = press(typed, key)
    }
}