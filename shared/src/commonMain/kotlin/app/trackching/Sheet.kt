package app.trackching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import trackching.shared.generated.resources.Res
import trackching.shared.generated.resources.ic_close

/**
 * The full-height sheet used for "New transaction", "New event" and so on:
 * blue top edge, a sticker title, and a close button.
 * `content` gets a `close` function to slide the sheet away, e.g. after saving.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackchingSheet(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.(close: () -> Unit) -> Unit,
) {
    val c = TrackchingTheme.colors
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Slide the sheet down first, then tell the caller it's gone.
    val close: () -> Unit = { scope.launch { state.hide() }.invokeOnCompletion { onDismiss() } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        shape = RectangleShape,
        containerColor = c.sheet,
        dragHandle = null,
    ) {
        Box(Modifier.fillMaxWidth().height(4.dp).background(c.blue))
        Row(
            Modifier.fillMaxWidth().padding(start = 14.dp, end = 4.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) { Tag(title) }
            IconButton(onClick = close) {
                Icon(painterResource(Res.drawable.ic_close), contentDescription = "Close without saving", tint = c.ink)
            }
        }
        content(close)
    }
}