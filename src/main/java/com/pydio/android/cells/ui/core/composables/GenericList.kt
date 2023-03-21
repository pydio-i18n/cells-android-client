package com.pydio.android.cells.ui.core.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pydio.android.cells.R
import com.pydio.android.cells.ui.core.LoadingState
import com.pydio.android.cells.ui.theme.CellsIcons


private const val logTag = "GenericList "

@Composable
fun WithLoadingListBackground(
    loadingState: LoadingState,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    canRefresh: Boolean = true,
    emptyRefreshableDesc: String = stringResource(R.string.empty_folder),
    emptyNoConnDesc: String = stringResource(R.string.empty_cache) + "\n" + stringResource(R.string.server_unreachable),
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        if (isEmpty) {
            if (loadingState == LoadingState.STARTING) {
                Box(
                    modifier = Modifier.fillMaxSize()
//                     .background(CellsColor.danger)
                ) {
                    StartingIndicator(
                        desc = stringResource(R.string.loading_message),
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(.5f)
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    EmptyList(
                        desc = if (canRefresh) {
                            emptyRefreshableDesc
                        } else {
                            emptyNoConnDesc
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(.5f)
                    )
                }
            }
        }
        content()
    }
}

@Composable
fun StartingIndicator(
    modifier: Modifier = Modifier,
    desc: String?
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        CircularProgressIndicator()
        desc?.let {
            Text(it)
        }
    }
}

@Composable
fun EmptyList(
    modifier: Modifier = Modifier,
    desc: String?
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = CellsIcons.EmptyFolder,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.grid_icon_size))
        )
        desc?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3BrowseUpItem(
    parentDescription: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Log.e(logTag, "adding the parent row for $parentDescription")
    ListItem(
        headlineText = { Text("..") },
        modifier = modifier,
        supportingText = { Text(parentDescription) },
        leadingContent = {
            M3IconThumb(
                R.drawable.aa_200_arrow_back_ios_new_24px,
                color = color
            )
        },
    )
}


@Composable
fun BrowseUpItem(
    parentDescription: String,
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Row(Modifier.padding(horizontal = 8.dp)) {
            Surface(
                Modifier
                // .size(40.dp)
                // .clip(RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)))
                // .background(MaterialTheme.colorScheme.error)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_baseline_arrow_back_ios_new_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        // .fillMaxSize()
                        .size(dimensionResource(R.dimen.list_thumb_size))
                        .wrapContentSize(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.list_thumb_margin)))

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimensionResource(R.dimen.card_padding),
                        vertical = dimensionResource(R.dimen.margin_xsmall)
                    )
                    .wrapContentSize(Alignment.CenterStart)
            ) {
                Text(
                    text = "..",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = parentDescription,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
