package com.pydio.android.cells.ui.browse.menus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pydio.android.cells.R
import com.pydio.android.cells.db.nodes.RTreeNode
import com.pydio.android.cells.ui.browse.composables.NodeAction
import com.pydio.android.cells.ui.core.composables.Thumbnail
import com.pydio.android.cells.ui.core.composables.menus.BottomSheetHeader
import com.pydio.android.cells.ui.core.composables.menus.BottomSheetListItem
import com.pydio.android.cells.ui.theme.CellsIcons
import com.pydio.cells.transport.StateID

// private const val logTag = "SearchMenu"

@Composable
fun SearchMenu(
    stateID: StateID,
    rTreeNode: RTreeNode,
    launch: (NodeAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.bottom_sheet_v_spacing))
            .verticalScroll(scrollState)

    ) {
        BottomSheetHeader(
            thumb = { Thumbnail(rTreeNode) },
            title = stateID.fileName ?: "",
            desc = stateID.parentPath,
        )

        if (rTreeNode.isFile()) {
            BottomSheetListItem(
                icon = CellsIcons.OpenLocation,
                title = stringResource(R.string.open_parent_in_workspaces),
                onItemClick = { launch(NodeAction.OpenParentLocation) })

            BottomSheetListItem(
                icon = CellsIcons.DownloadToDevice,
                title = stringResource(R.string.download_to_device),
                onItemClick = { launch(NodeAction.DownloadToDevice) },
            )

        } else {
            BottomSheetListItem(
                icon = CellsIcons.OpenLocation,
                title = stringResource(R.string.open_in_workspaces),
                onItemClick = { launch(NodeAction.OpenInApp) })

        }
    }
}