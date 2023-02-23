package com.pydio.android.cells.ui.box

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pydio.android.cells.AppNames
import com.pydio.android.cells.ui.browse.screens.SelectFolderScreen
import com.pydio.android.cells.ui.browse.screens.SelectTargetAccount
import com.pydio.android.cells.ui.models.AccountListVM
import com.pydio.android.cells.ui.models.BrowseLocalFoldersVM
import com.pydio.android.cells.ui.models.BrowseRemoteVM
import com.pydio.android.cells.ui.models.LoadingState
import com.pydio.android.cells.ui.models.UploadsVM
import com.pydio.android.cells.ui.theme.CellsTheme
import com.pydio.android.cells.ui.transfer.UploadProgressList
import com.pydio.cells.api.Transport
import com.pydio.cells.transport.StateID
import com.pydio.cells.utils.Str

private const val logTag = "SelectTargetHost.kt"

sealed class SelectTargetDestination(val route: String) {

    object ChooseAccount : SelectTargetDestination("choose-account")

    object OpenFolder : SelectTargetDestination("open/{stateId}") {
        fun createRoute(stateID: StateID) = "open/${stateID.id}"
        fun getPathKey() = "stateId"
    }

    object UploadInProgress : SelectTargetDestination("upload-in-progress/{stateId}") {
        fun createRoute(stateID: StateID) = "upload-in-progress/${stateID.id}"
        fun getPathKey() = "stateId"
    }

    // TODO add safety checks to prevent forbidden copy-move
    //  --> to finalise we must really pass the node*s* to copy or move rather than its parent
}

@Composable
fun SelectTargetHost(
    navController: NavHostController,
    action: String,
    initialStateId: String,
    browseLocalVM: BrowseLocalFoldersVM,
    browseRemoteVM: BrowseRemoteVM,
    accountListVM: AccountListVM,
    uploadsVM: UploadsVM,
    postActivity: (stateID: StateID, action: String?) -> Unit,
) {

    val currLoadingState by browseRemoteVM.loadingState.observeAsState()

    /* Define callbacks */
    val open: (StateID) -> Unit = { stateID ->
        Log.d(logTag, "in open($stateID)")
        val newRoute = SelectTargetDestination.OpenFolder.createRoute(stateID)
        Log.i(logTag, "About to navigate to [$newRoute]")
        navController.navigate(newRoute)
    }

    val openParent: (StateID) -> Unit = { stateID ->
        Log.d(logTag, ".... In OpenParent: $stateID - ${stateID.workspace} ")
        if (Str.empty(stateID.workspace)) {
            navController.navigate(SelectTargetDestination.ChooseAccount.route)
        } else {
            val parent = stateID.parent()
            navController.navigate(SelectTargetDestination.OpenFolder.createRoute(parent))
        }
    }

    val interceptPost: (StateID, String?) -> Unit = { stateID, currAction ->
        if (AppNames.ACTION_UPLOAD == currAction) {
            navController.navigate(
                SelectTargetDestination.UploadInProgress.createRoute(stateID),
            ) {
                // We insure that the navigation page is first on the back stack
                // So that the end user cannot launch the upload twice using the back btn
                popUpTo(SelectTargetDestination.ChooseAccount.route) { inclusive = true }
            }
        }
        postActivity(stateID, currAction)
    }

    val canPost: (StateID) -> Boolean = { stateID ->
        Str.notEmpty(stateID.workspace)
        // true
//        if (action == AppNames.ACTION_UPLOAD) {
//            true
//        } else {
//            // Optimistic check to prevent trying to copy move inside itself
//            // TODO this does not work: we get the parent state as initial input
//            //   (to start from the correct location), we should rather get a list of states
//            //   that are about to copy / move to provide better behaviour in the select target activity
//            !((stateID.id.startsWith(initialStateId) && (stateID.id.length > initialStateId.length)))
//        }
    }

    val forceRefresh: (StateID) -> Unit = { browseRemoteVM.watch(it, true) }

    val startDestination = if (initialStateId != Transport.UNDEFINED_STATE) {
        SelectTargetDestination.OpenFolder.route
    } else {
        SelectTargetDestination.ChooseAccount.route
    }

    // Configure navigation 
    NavHost(
        navController = navController, startDestination = startDestination
    ) {

        composable(SelectTargetDestination.ChooseAccount.route) {
            Log.d(logTag, ".... Open choose account page")

            // TODO double check this might not be called on the second pass
            LaunchedEffect(true) {
                Log.e(logTag, ".... Choose account, launching effect")
                accountListVM.watch()
                browseRemoteVM.pause()
            }

            SelectTargetAccount(
                accountListVM = accountListVM,
                openAccount = open,
                cancel = { postActivity(Transport.UNDEFINED_STATE_ID, AppNames.ACTION_CANCEL) },
                login = { postActivity(it, AppNames.ACTION_LOGIN) },
            )
        }

        composable(SelectTargetDestination.OpenFolder.route) { navBackStackEntry ->
            val stateId =
                navBackStackEntry.arguments?.getString(SelectTargetDestination.OpenFolder.getPathKey())
                    ?: initialStateId
            Log.e(logTag, ".... Open choose *folder* page, with ID: ${StateID.fromId(stateId)}")

            LaunchedEffect(key1 = stateId) {
                accountListVM.pause()
                browseLocalVM.setState(StateID.fromId(stateId))
                browseRemoteVM.watch(StateID.fromId(stateId), false)
            }

            SelectFolderScreen(
                action,
                stateId,
                currLoadingState ?: LoadingState.STARTING,
                browseLocalVM,
                open,
                openParent,
                canPost,
                postActivity = interceptPost,
                forceRefresh,
            )
        }

        composable(SelectTargetDestination.UploadInProgress.route) { navBackStackEntry ->
            Log.d(logTag, "About to navigate to upload screen")

            // throws an IllegalArgExc:
            // java.lang.IllegalArgumentException: DerivedState(value=<Not calculated>)@254214545 cannot be saved using the current SaveableStateRegistry. The default implementation only supports types which can be stored inside the Bundle. Please consider implementing a custom Saver for this class and pass it to rememberSaveable().
//            val stateId = rememberSaveable() {
//                derivedStateOf {
//                    navBackStackEntry.arguments
//                        ?.getString(SelectTargetDestination.UploadInProgress.getPathKey())
//                        ?: Transport.UNDEFINED_STATE_ID
//                }
//            }

            val stateId = navBackStackEntry.arguments
                ?.getString(SelectTargetDestination.UploadInProgress.getPathKey())
                ?: Transport.UNDEFINED_STATE

            LaunchedEffect(key1 = stateId) {
                Log.d(logTag, "... In upload root, launching effects for $stateId")
                accountListVM.pause()
                browseRemoteVM.pause()
            }

            UploadProgressList(uploadsVM) {
                postActivity(StateID.fromId(stateId), AppNames.ACTION_CANCEL)
            }
        }
    }
}

@Composable
fun SelectTargetApp(content: @Composable () -> Unit) {
    CellsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
