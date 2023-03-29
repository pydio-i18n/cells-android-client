package com.pydio.android.cells.ui.browse.menus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.pydio.android.cells.AppNames
import com.pydio.android.cells.R
import com.pydio.android.cells.ui.browse.models.FilterTransferByMenuVM
import com.pydio.android.cells.ui.core.composables.menus.BottomSheetDivider
import com.pydio.android.cells.ui.core.composables.menus.BottomSheetHeader
import com.pydio.android.cells.ui.core.composables.menus.BottomSheetListItem
import com.pydio.android.cells.ui.core.composables.menus.GenericBottomSheetHeader
import com.pydio.android.cells.ui.theme.CellsIcons
import com.pydio.cells.utils.Log
import org.koin.androidx.compose.koinViewModel

private const val logTag = "FilterTransfersByMenu"

@Composable
fun FilterTransfersByMenu(
    done: () -> Unit,
    filterByMenuVM: FilterTransferByMenuVM = koinViewModel()
) {
    val keys = stringArrayResource(R.array.filter_transfer_by_status_values)
    val labels = stringArrayResource(R.array.filter_transfer_by_status_labels)

    val selectedFilter =
        filterByMenuVM.jobFilter.collectAsState(initial = AppNames.JOB_STATUS_NO_FILTER)

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.bottom_sheet_v_spacing))
            .verticalScroll(scrollState)

    ) {
        GenericBottomSheetHeader(
            icon = CellsIcons.FilterBy,
            title = stringResource(R.string.filter_by_status_title),
        )

        for (i in keys.indices) {
            val selected = keys[i] == selectedFilter.value
            BottomSheetListItem(
                icon = null,
                title = labels[i],
                onItemClick = {
                    Log.d(logTag, "New filter: ${keys[i]}")
                    filterByMenuVM.setFilterBy(keys[i])
                    done()
                },
                selected = selected,
            )
        }

    }
}
