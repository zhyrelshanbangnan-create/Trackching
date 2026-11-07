package app.trackching

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.trackching.db.MostUsed
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

private const val OneDayMillis = 24L * 60 * 60 * 1000

/** "New transaction" on Expense: type, tap a category, tap SAVE. */
@Composable
fun ExpenseSheet(
    ledger: Ledger,
    onDismiss: () -> Unit,
    onSaved: (txnId: Long) -> Unit,
) {
    val c = TrackchingTheme.colors
    val form = remember { ExpenseForm(ledger.lastAccountUsed()) }
    val accounts = remember { ledger.accounts() }
    var showAll by remember { mutableStateOf(false) }
    val categories = remember(showAll) { ledger.quickCategories("EXPENSE", if (showAll) 100L else 8L) }

    val hint = when {
        form.centavos == 0L -> "Type the amount"
        form.categoryId == null -> "Pick a category"
        else -> ""
    }

    TrackchingSheet(title = "New transaction", onDismiss = onDismiss) { close ->
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Amount(form.centavos, AmountKind.Expense, maxFontSize = 40.sp)
            Text(hint, color = c.ink2, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Column(
            Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Field("Category") {
                CategoryGrid(
                    categories = categories,
                    selected = form.categoryId,
                    showMore = !showAll,
                    onPick = { form.categoryId = it },
                    onMore = { showAll = true },
                )
            }
            Field("Paid from") {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    accounts.forEach { account ->
                        Chip(account.name, selected = account.id == form.accountId, onClick = { form.accountId = account.id })
                    }
                }
            }
            Field("Date") {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Chip(todayLabel(), selected = !form.yesterday, onClick = { form.yesterday = false })
                    Chip("Yesterday", selected = form.yesterday, onClick = { form.yesterday = true })
                }
            }
        }

        Keypad(
            onKey = form::onKey,
            saveEnabled = form.canSave,
            onSave = {
                val category = form.categoryId
                val account = form.accountId
                if (category != null && account != null) {
                    val at = if (form.yesterday) nowMillis() - OneDayMillis else nowMillis()
                    onSaved(ledger.logExpense(form.centavos, category, account, at))
                    close()
                }
            },
        )
    }
}

@Composable
private fun Field(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label.uppercase(), color = TrackchingTheme.colors.ink2, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        content()
    }
}

/** 4 tiles per row. The last tile is More until everything is shown. */
@Composable
private fun CategoryGrid(
    categories: List<MostUsed>,
    selected: Long?,
    showMore: Boolean,
    onPick: (Long) -> Unit,
    onMore: () -> Unit,
) {
    // null stands for the More tile.
    val tiles: List<MostUsed?> = categories + if (showMore) listOf(null) else emptyList()
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        tiles.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { cat ->
                    if (cat == null) {
                        CategoryTile("More", "…", Color(0xFF5C616C), null, false, Modifier.weight(1f), onMore)
                    } else {
                        CategoryTile(
                            name = cat.name,
                            letter = cat.name.take(1),
                            color = Color(cat.color),
                            parent = cat.parent_name?.substringBefore(' '),
                            selected = cat.id == selected,
                            modifier = Modifier.weight(1f),
                            onClick = { onPick(cat.id) },
                        )
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CategoryTile(
    name: String,
    letter: String,
    color: Color,
    parent: String?,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val c = TrackchingTheme.colors
    Column(
        modifier
            .padding(end = 4.dp, bottom = 3.dp)
            .then(if (selected) Modifier.offsetShadow(c.orange, x = 4.dp, y = 3.dp) else Modifier)
            .background(if (selected) c.blue else c.field)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .heightIn(min = 66.dp)
            .padding(horizontal = 2.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(Modifier.size(28.dp).background(color), contentAlignment = Alignment.Center) {
            Text(letter, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        Text(
            name,
            color = if (selected) c.onBlue else c.ink,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
        if (parent != null) {
            Text(parent, color = if (selected) c.onBlue else c.ink2, fontSize = 11.sp, maxLines = 1)
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

/** "Today, Nov 7" */
@OptIn(ExperimentalTime::class)
private fun todayLabel(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val month = today.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return "Today, $month ${today.day}"
}