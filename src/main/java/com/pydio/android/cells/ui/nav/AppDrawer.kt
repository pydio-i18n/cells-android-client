package com.pydio.android.cells.ui.nav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pydio.android.cells.R
import com.pydio.android.cells.ui.ConnectionVM
import com.pydio.android.cells.ui.browse.BrowseDestinations
import com.pydio.android.cells.ui.browse.BrowseNavigationActions
import com.pydio.android.cells.ui.core.composables.BottomSheetDivider
import com.pydio.android.cells.ui.core.composables.DefaultTitleText
import com.pydio.android.cells.ui.core.composables.getWsThumbVector
import com.pydio.android.cells.ui.theme.CellsIcons
import com.pydio.android.cells.ui.theme.CellsTheme
import com.pydio.cells.transport.StateID

private const val logTag = "AppDrawer"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    currRoute: String?,
    currSelectedID: StateID?,
    connectionVM: ConnectionVM,
    cellsNavActions: CellsNavigationActions,
    systemNavActions: SystemNavigationActions,
    browseNavActions: BrowseNavigationActions,
    closeDrawer: () -> Unit,
) {
    Log.e(logTag, "... route: $currRoute, selected ID: $currSelectedID")

    val defaultPadding = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    val accountID = connectionVM.currAccountID.observeAsState()
    val wss = connectionVM.wss.observeAsState()
    val cells = connectionVM.cells.observeAsState()

    ModalDrawerSheet(
        windowInsets = WindowInsets.systemBars
            //.only(if (excludeTop) WindowInsetsSides.Bottom else WindowInsetsSides.Vertical)
            .add(WindowInsets(bottom = 12.dp))
    ) {

        LazyColumn {

            item {
                PydioLogo(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
                )
            }

            accountID.value?.let { currAccountID ->
                // This section: offline, bookmark, transfers and root workspace accesses
                // is only relevant when we have a defined account
                Log.e(logTag, "... Composing account part of the drawer")
                Log.e(logTag, "... route: $currRoute")
                item {
                    MyNavigationDrawerItem(
                        label = stringResource(R.string.action_open_offline_roots),
                        icon = CellsIcons.KeepOfflineOld,
                        selected = BrowseDestinations.OfflineRoots.isCurrent(currRoute),
                        onClick = { browseNavActions.toOfflineRoots(currAccountID);closeDrawer() },
                        modifier = defaultPadding
                    )
                    MyNavigationDrawerItem(
                        label = stringResource(R.string.action_open_bookmarks),
                        icon = CellsIcons.Bookmark,
                        selected = BrowseDestinations.Bookmarks.isCurrent(currRoute),
                        onClick = { browseNavActions.toBookmarks(currAccountID);closeDrawer() },
                        modifier = defaultPadding
                    )
                    MyNavigationDrawerItem(
                        label = stringResource(R.string.action_open_transfers),
                        icon = CellsIcons.Transfers,
                        selected = BrowseDestinations.Transfers.isCurrent(currRoute),
                        onClick = { browseNavActions.toTransfers(currAccountID); closeDrawer() },
                        modifier = defaultPadding
                    )

                    BottomSheetDivider()
                    DefaultTitleText(stringResource(R.string.my_workspaces))

                    wss.value?.listIterator()?.forEach {
                        val selected = BrowseDestinations.Open.isCurrent(currRoute)
                                && it.getStateID() == currSelectedID
                        MyNavigationDrawerItem(
                            label = it.label ?: it.slug,
                            icon = getWsThumbVector(it.sortName ?: ""),
                            selected = selected,
                            onClick = { browseNavActions.toBrowse(it.getStateID());closeDrawer() },
                            modifier = defaultPadding
                        )
                    }

                    cells.value?.listIterator()?.forEach {
                        val selected = BrowseDestinations.Open.isCurrent(currRoute)
                                && it.getStateID() == currSelectedID
                        MyNavigationDrawerItem(
                            label = it.label ?: it.slug,
                            icon = getWsThumbVector(it.sortName ?: ""),
                            selected = selected,
                            onClick = { browseNavActions.toBrowse(it.getStateID()); closeDrawer() },
                            modifier = defaultPadding
                        )
                    }
                }
            } ?: run {
                item {
                    MyNavigationDrawerItem(
                        label = stringResource(id = R.string.choose_account),
                        icon = Icons.Filled.Group,
                        selected = CellsDestinations.Accounts.route == currRoute,
                        onClick = { cellsNavActions.navigateToAccounts();closeDrawer() },
                        modifier = defaultPadding
                    )
                }
            }

            item {
                BottomSheetDivider()
                DefaultTitleText(stringResource(R.string.my_account))

                MyNavigationDrawerItem(
                    label = stringResource(R.string.action_settings),
                    icon = CellsIcons.Settings,
                    selected = false, // TODO
                    onClick = { closeDrawer() }, // TODO
                    modifier = defaultPadding
                )
                MyNavigationDrawerItem(
                    label = stringResource(R.string.action_clear_cache),
                    icon = CellsIcons.EmptyRecycle,
                    selected = false, // TODO
                    onClick = { systemNavActions.navigateToClearCache(); closeDrawer() },
                    modifier = defaultPadding
                )
                MyNavigationDrawerItem(
                    label = stringResource(R.string.action_open_jobs),
                    icon = CellsIcons.Jobs,
                    selected = false, // TODO
                    onClick = { systemNavActions.navigateToJobs(); closeDrawer() },
                    modifier = defaultPadding
                )
                MyNavigationDrawerItem(
                    label = stringResource(R.string.action_open_logs),
                    icon = CellsIcons.Logs,
                    selected = false, // TODO
                    onClick = { systemNavActions.navigateToLogs(); closeDrawer() },
                    modifier = defaultPadding
                )

                MyNavigationDrawerItem(
                    label = stringResource(id = R.string.action_open_about),
                    icon = CellsIcons.About,
                    selected = SystemDestinations.ABOUT_ROUTE == currRoute,
                    onClick = { systemNavActions.navigateToAbout(); closeDrawer() },
                    modifier = defaultPadding,
                )
            }
        }
    }
}

@Composable
private fun PydioLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(
            painterResource(R.drawable.pydio_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNavigationDrawerItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
) {
    NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, label) },
        selected = selected,
        onClick = onClick,
        modifier = modifier.height(dimensionResource(id = R.dimen.menu_item_height)),
        shape = ShapeDefaults.Small,
    )
    //    badge: (@Composable () -> Unit)? = null,
    //    colors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
    //    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    CellsTheme {
//        AppDrawer(
//            currAccountID = Transport.UNDEFINED_STATE_ID,
//            currentRoute = CellsDestinations.Home.route,
//            navigateToHome = {},
//            navigateToBrowse = {},
//            navigateToAccounts = {},
//            navigateToAbout = {},
//            closeDrawer = { }
//        )
    }
}
