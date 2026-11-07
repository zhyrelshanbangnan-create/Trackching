package app.trackching

import app.trackching.db.MostUsed
import app.trackching.db.TrackchingDatabase

class Ledger(private val db: TrackchingDatabase) {

    /** Saves an expense and returns its id, so it can be undone. */
    fun logExpense(centavos: Long, categoryId: Long, accountId: Long, occurredAt: Long): Long {
        require(centavos > 0) { "An expense must be more than ₱0.00" }
        return db.transactionWithResult {
            db.txnQueries.insert("EXPENSE", centavos, accountId, null, categoryId, occurredAt)
            db.txnQueries.lastId().executeAsOne()
        }
    }

    /** Removes a transaction that was just saved ("Saved · Undo"). */
    fun undo(txnId: Long) {
        db.txnQueries.delete(txnId)
    }

    /** The category grid: most used first. */
    fun quickCategories(kind: String, limit: Long = 8): List<MostUsed> =
        db.categoryQueries.mostUsed(kind, limit).executeAsList()

    /** "Paid from" default: the last account used, or the first account if nothing is logged yet. */
    fun lastAccountUsed(): Long? =
        db.txnQueries.lastAccountUsed().executeAsOneOrNull()
            ?: db.accountQueries.active().executeAsList().firstOrNull()?.id

    fun balance(accountId: Long): Long = db.accountQueries.balanceOf(accountId).executeAsOne()
}