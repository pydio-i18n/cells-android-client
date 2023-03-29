package com.pydio.android.cells.ui.browse.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.pydio.android.cells.AppNames
import com.pydio.android.cells.db.nodes.RTransfer
import com.pydio.android.cells.services.NodeService
import com.pydio.android.cells.services.PreferencesService
import com.pydio.android.cells.services.TransferService
import com.pydio.android.cells.ui.core.LoadingState
import com.pydio.cells.transport.StateID
import kotlinx.coroutines.launch

/** Holds a list of recent file transfers for current session */
class TransfersVM(
    private val accountID: StateID,
    prefs: PreferencesService,
    private val transferService: TransferService,
    nodeService: NodeService,
) : AbstractBrowseVM(prefs, nodeService) {

    private val logTag = "TransfersVM"

    // Unused for the time being
    private val _loadingState = MutableLiveData(LoadingState.IDLE)
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun forceRefresh() {
        // DO nothing
    }

    // TODO only use flows (no more live data)
    private val livePrefs = listPrefs.asLiveData(viewModelScope.coroutineContext)
    val transfers: LiveData<List<RTransfer>>
        get() = livePrefs.switchMap {
            transferService.queryTransfersExplicitFilter(
                accountID,
                it.transferFilter,
                it.transferOrder
            )
        }

    suspend fun get(transferID: Long): RTransfer? =
        transferService.getRecord(accountID, transferID)

    fun pauseOne(transferID: Long) {
        viewModelScope.launch {
            // TODO improve this
            transferService.cancelTransfer(accountID, transferID, AppNames.JOB_OWNER_USER)
        }
    }

    fun resumeOne(transferID: Long) {
        viewModelScope.launch {
            // TODO improve this
            transferService.uploadOne(accountID, transferID)
        }
    }

    fun cancelOne(transferID: Long) {
        viewModelScope.launch {
            transferService.cancelTransfer(accountID, transferID, AppNames.JOB_OWNER_USER)
        }
    }

    fun removeOne(transferID: Long) {
        Log.i(logTag, "About to delete $transferID @ $accountID")
        viewModelScope.launch {
            transferService.deleteRecord(accountID, transferID)
        }
    }

    fun clearTerminated() {
        Log.i(logTag, "About to empty transfer table for $accountID")
        viewModelScope.launch {
            transferService.clearTerminated(accountID)
        }
    }
}
