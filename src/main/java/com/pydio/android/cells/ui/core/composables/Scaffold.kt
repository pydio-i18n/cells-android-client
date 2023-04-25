package com.pydio.android.cells.ui.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.pydio.android.cells.R
import com.pydio.android.cells.ui.theme.CellsIcons
import com.pydio.android.cells.ui.theme.UseCellsTheme
import com.pydio.cells.utils.Str

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun WithDefaultScaffold(
//    title: String,
//    openDrawer: () -> Unit,
//    modifier: Modifier = Modifier,
//    content: @Composable (PaddingValues) -> Unit,
//) {
////    val topAppBarState = rememberTopAppBarState()
//    Scaffold(
//        topBar = {
//            DefaultTopAppBar(
//                title = title,
//                openDrawer = openDrawer,
////                topAppBarState = topAppBarState
//            )
//        },
//        modifier = modifier
//    ) { innerPadding ->
//        content(innerPadding)
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(
    title: String,
    isExpandedScreen: Boolean = false,
    back: (() -> Unit)? = null,
    openDrawer: (() -> Unit)? = null,
    openSearch: (() -> Unit)? = null,
) {

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isExpandedScreen) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

        ),
        navigationIcon = {
            if (back != null) {
                IconButton(onClick = { back() }) {
                    Icon(
                        imageVector = CellsIcons.ArrowBack,
                        contentDescription = stringResource(id = R.string.button_back)
                    )
                }
            } else if (openDrawer != null) {
                IconButton(
                    onClick = { openDrawer() },
                    enabled = true
                ) {
                    Icon(
                        CellsIcons.Menu,
                        contentDescription = stringResource(id = R.string.open_drawer)
                    )
                }
            }
        },
        actions = {
            if (openSearch != null) {
                IconButton(onClick = { openSearch() }) {
                    Icon(
                        CellsIcons.Search,
                        contentDescription = stringResource(id = R.string.action_search)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithMoreMenu(
    title: String,
    back: (() -> Unit)? = null,
    openDrawer: (() -> Unit)? = null,
    openSearch: (() -> Unit)? = null,
    isActionMenuShown: Boolean,
    showMenu: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        navigationIcon = {
            if (back != null) {
                IconButton(onClick = { back() }) {
                    Icon(
                        imageVector = CellsIcons.ArrowBack,
                        contentDescription = stringResource(id = R.string.button_back)
                    )
                }
            } else if (openDrawer != null) {
                IconButton(
                    onClick = { openDrawer() },
                    enabled = true
                ) {
                    Icon(
                        CellsIcons.Menu,
                        contentDescription = stringResource(id = R.string.open_drawer)
                    )
                }
            }
        },
        actions = {
            if (openSearch != null) {
                IconButton(onClick = { openSearch() }) {
                    Icon(
                        CellsIcons.Search,
                        contentDescription = stringResource(id = R.string.action_search)
                    )
                }
            }
            IconButton(onClick = { showMenu(!isActionMenuShown) }) {
                Icon(
                    CellsIcons.MoreVert,
                    contentDescription = stringResource(R.string.open_more_menu)
                )
            }
            DropdownMenu(
                expanded = isActionMenuShown,
                onDismissRequest = { showMenu(false) },
                content = content
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithActions(
    title: String,
    back: (() -> Unit)? = null,
    openDrawer: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (back != null) {
                IconButton(onClick = { back() }) {
                    Icon(
                        imageVector = CellsIcons.ArrowBack,
                        contentDescription = stringResource(id = R.string.button_back)
                    )
                }
            } else if (openDrawer != null) {
                IconButton(
                    onClick = { openDrawer() },
                    enabled = true
                ) {
                    Icon(
                        CellsIcons.Menu,
                        contentDescription = stringResource(id = R.string.open_drawer)
                    )
                }
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithSearch(
    queryStr: String,
    errorMessage: String?,
    updateQuery: (String) -> Unit,
    cancel: (() -> Unit),
    isActionMenuShown: Boolean,
    showMenu: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    contentPadding: PaddingValues = PaddingValues(all = 16.dp),
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        title = {
            TextField(
                value = queryStr,
                textStyle = MaterialTheme.typography.bodyMedium,
                // label = { Icon(CellsIcons.Search, "Search") },
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f)
                    )
                },
                supportingText = {
                    if (Str.notEmpty(errorMessage)) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = imeAction),
                keyboardActions = keyboardActions,
                onValueChange = { newValue -> updateQuery(newValue) },
            )
        },
        navigationIcon = {
            IconButton(onClick = { cancel() }) {
                Icon(
                    imageVector = CellsIcons.CancelSearch,
                    contentDescription = stringResource(id = R.string.button_cancel)
                )
            }

        },
        actions = {
            IconButton(onClick = { showMenu(!isActionMenuShown) }) {
                Icon(
                    CellsIcons.MoreVert,
                    contentDescription = stringResource(R.string.open_more_menu)
                )
            }
            DropdownMenu(
                expanded = isActionMenuShown,
                onDismissRequest = { showMenu(false) },
                content = content
            )
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .wrapContentHeight(CenterVertically)
            .padding(vertical = dimensionResource(R.dimen.margin)),
    )
}

/** Helper method to provide a better design depending on the user's device,
 * inspired from: [TODO]
 */
//@Composable
//fun extraTopPadding(isExpandedScreen: Boolean): Dp {
//    return if (!isExpandedScreen) {
//        0.dp
//    } else {
//        dimensionResource(R.dimen.expanded_screen_extra_top_padding)
//    }
//}

@Preview(name = "Default top bar - Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Default top bar - Dark Mode"
)
@Composable
private fun DefaultTopBarPreview() {
    UseCellsTheme {
        DefaultTopBar(
            "Pydio Cells server",
            false,
            { },
            null,
            { },
        )
    }
}

@Preview(name = "Expanded top bar - Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Expanded top bar - Dark Mode"
)
@Composable
private fun ExpandedTopBarPreview() {
    UseCellsTheme {
        DefaultTopBar(
            "Pydio Cells server",
            true,
            { },
            null,
            { },
        )
    }
}
