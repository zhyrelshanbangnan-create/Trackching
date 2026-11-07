package app.trackching

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Temporary Home: each account and its balance. The real Home is P1-18. */
@Composable
fun HomeScreen(ledger: Ledger) {
    val c = TrackchingTheme.colors
    val balances by remember(ledger) { ledger.balances() }.collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Tag("Accounts")
        balances.forEach { account ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(account.name, color = c.ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Amount(
                    account.balance,
                    if (account.balance < 0) AmountKind.Expense else AmountKind.Transfer,
                    maxFontSize = 20.sp,
                )
            }
        }
    }
}