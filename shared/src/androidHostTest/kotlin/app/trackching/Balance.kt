package app.trackching

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.trackching.db.TrackchingDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceTest {

    // A fresh, empty database in memory for every test.
    private val db = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).let { driver ->
        TrackchingDatabase.Schema.create(driver)
        TrackchingDatabase(driver)
    }

    private fun account(name: String, starting: Long): Long {
        db.accountQueries.insert(name, starting)
        return db.accountQueries.lastId().executeAsOne()
    }

    private fun txn(kind: String, centavos: Long, from: Long, to: Long? = null): Long {
        db.txnQueries.insert(kind, centavos, from, to, null, 0)
        return db.txnQueries.lastId().executeAsOne()
    }

    private fun balance(id: Long) = db.accountQueries.balanceOf(id).executeAsOne()

    @Test
    fun newAccountShowsStartingBalance() {
        val cash = account("Cash", 100_000)
        assertEquals(100_000, balance(cash))
    }

    @Test
    fun expenseLowersAndDeleteRestores() {
        val cash = account("Cash", 100_000)          // ₱1,000
        val lunch = txn("EXPENSE", 20_000, cash)     // ₱200
        assertEquals(80_000, balance(cash))
        db.txnQueries.delete(lunch)
        assertEquals(100_000, balance(cash))
    }

    @Test
    fun incomeRaises() {
        val cash = account("Cash", 0)
        txn("INCOME", 50_000, cash)
        assertEquals(50_000, balance(cash))
    }

    @Test
    fun transferMovesMoneyBetweenAccounts() {
        val bpi = account("BPI", 500_000)
        val gcash = account("GCash", 120_000)
        txn("TRANSFER", 100_000, bpi, gcash)
        assertEquals(400_000, balance(bpi))
        assertEquals(220_000, balance(gcash))
    }
}