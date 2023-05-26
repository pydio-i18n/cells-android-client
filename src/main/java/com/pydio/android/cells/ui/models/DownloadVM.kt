package com.pydio.android.cells.ui.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.pydio.android.cells.AppNames
import com.pydio.android.cells.db.nodes.RTransfer
import com.pydio.android.cells.db.nodes.RTreeNode
import com.pydio.android.cells.services.TransferService
import com.pydio.android.cells.ui.core.AbstractCellsVM
import com.pydio.cells.api.SDKException
import com.pydio.cells.transport.StateID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class DownloadVM(
    private val stateID: StateID,
    private val transferService: TransferService
) : AbstractCellsVM() {

    private val logTag = "DownloadVM"
    private val _rTreeNode = MutableStateFlow<RTreeNode?>(null)
    val treeNode: StateFlow<RTreeNode?> = _rTreeNode.asStateFlow()

    private val _transferID = MutableStateFlow(-1L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val transfer: Flow<RTransfer?> = _transferID.flatMapLatest { currID ->
        transferService.liveTransfer(stateID.account(), currID)
    }

    fun cancelDownload() {
        viewModelScope.launch {
            if (_transferID.value >= 0) {
                transferService.cancelTransfer(stateID, _transferID.value, AppNames.JOB_OWNER_USER)
            }
        }
    }

    suspend fun launchDownload() {
        try {
            val transferID = transferService.prepareDownload(stateID, AppNames.LOCAL_FILE_TYPE_FILE)
            _transferID.value = transferID
            transferService.runDownloadTransfer(stateID.account(), transferID, null)
        } catch (se: SDKException) {
            val msg = "Cannot download file for $stateID"
            Log.e(logTag, "$msg, cause: ${se.message ?: "-"} ")
            showError(ErrorMessage(msg, -1, listOf()))
        }
    }

    suspend fun viewFile(context: Context) {
        viewFile(context, stateID)
//        _rTreeNode.value?.let { node ->
//            nodeService.getLocalFile(node, true)?.let { file ->
//                externallyView(context, file, node)
//                return
//            } ?: run {
//                Log.e(logTag, "Could not open file for ${node.getStateID()}")
//            }
//        }
    }

    init {
        viewModelScope.launch {
            nodeService.getNode(stateID)?.let {
                _rTreeNode.value = it
            }
        }
    }
}
