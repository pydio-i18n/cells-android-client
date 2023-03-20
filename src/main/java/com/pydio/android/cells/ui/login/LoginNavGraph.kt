package com.pydio.android.cells.ui.login

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pydio.android.cells.ui.core.lazySkipVerify
import com.pydio.android.cells.ui.core.lazyStateID
import com.pydio.android.cells.ui.login.models.LoginVM
import com.pydio.android.cells.ui.login.screens.AskServerUrl
import com.pydio.android.cells.ui.login.screens.P8Credentials
import com.pydio.android.cells.ui.login.screens.ProcessAuth
import com.pydio.android.cells.ui.login.screens.SkipVerify
import com.pydio.android.cells.ui.login.screens.StartingLoginProcess

private const val logTag = "loginNavGraph"

fun NavGraphBuilder.loginNavGraph(
    helper: LoginHelper,
    loginVM: LoginVM,
//    isExpandedScreen: Boolean,
) {

    composable(LoginDestinations.Starting.route) { nbsEntry ->
        val stateID = lazyStateID(nbsEntry)
        Log.e(logTag, "... Starting login activity with $stateID")
        StartingLoginProcess()
    }

    composable(LoginDestinations.Done.route) { nbsEntry ->
        val stateID = lazyStateID(nbsEntry)
        Log.e(logTag, "... Starting done activity for $stateID")
        StartingLoginProcess()
    }

    composable(LoginDestinations.AskUrl.route) { nbsEntry ->
        val stateID = lazyStateID(nbsEntry)
        Log.e(logTag, "... Starting AskUrl activity for $stateID")
        AskServerUrl(helper = helper, loginVM = loginVM)
    }

    composable(LoginDestinations.SkipVerify.route) { nbsEntry ->
        val stateID = lazyStateID(nbsEntry)
        Log.e(logTag, "... Starting SkipVerify activity for $stateID")
        SkipVerify(stateID, helper = helper, loginVM = loginVM)
    }

    composable(LoginDestinations.P8Credentials.route) { nbsEntry ->
        val stateID = lazyStateID(nbsEntry)
        val skipVerify = lazySkipVerify(nbsEntry)
        Log.e(logTag, "... Starting P8Credentials activity for $stateID")
        P8Credentials(
            stateID = stateID,
            skipVerify = skipVerify,
            helper = helper,
            loginVM = loginVM,
        )
    }

    composable(LoginDestinations.ProcessAuth.route) { nbsEntry ->
        val stateID = lazyStateID(nbsEntry)
        val skipVerify = lazySkipVerify(nbsEntry)
        Log.e(logTag, "... Starting ProcessAuth activity for $stateID")
        ProcessAuth(
            stateID = stateID,
            skipVerify = skipVerify,
            loginVM = loginVM,
            helper = helper,
        )
    }
}