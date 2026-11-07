package app.trackching

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpenseFormTest {

    private fun ExpenseForm.type(vararg keys: Key) = keys.forEach { onKey(it) }
    private fun d(text: String) = Key.Digits(text)

    @Test
    fun saveIsOffAtFirst() {
        assertFalse(ExpenseForm(accountId = 1).canSave)
    }

    @Test
    fun amountAloneIsNotEnough() {
        val form = ExpenseForm(accountId = 1)
        form.type(d("1"), d("2"), d("0"))
        assertEquals(12_000L, form.centavos)
        assertFalse(form.canSave)
    }

    @Test
    fun categoryAloneIsNotEnough() {
        val form = ExpenseForm(accountId = 1)
        form.categoryId = 5
        assertFalse(form.canSave)
    }

    @Test
    fun amountAndCategoryTurnSaveOn() {
        val form = ExpenseForm(accountId = 1)
        form.type(d("1"), d("2"), d("0"))
        form.categoryId = 5
        assertTrue(form.canSave)
    }

    @Test
    fun noAccountMeansNoSave() {
        val form = ExpenseForm(accountId = null)
        form.type(d("5"))
        form.categoryId = 5
        assertFalse(form.canSave)
    }
}