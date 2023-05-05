package com.pydio.android.cells.ui.browse.models

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pydio.android.cells.CellsApp
import com.pydio.android.cells.services.FileService
import com.pydio.android.cells.services.NodeService
import com.pydio.android.cells.services.OfflineService
import com.pydio.android.cells.services.TransferService
import com.pydio.android.cells.ui.core.LoadingState
import com.pydio.android.cells.utils.DEFAULT_FILE_PROVIDER_ID
import com.pydio.cells.api.SDKException
import com.pydio.cells.transport.StateID
import com.pydio.cells.utils.Str
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**  Centralize methods to manage a TreeNode */
class NodeActionsVM(
    private val nodeService: NodeService,
    private val fileService: FileService,
    private val transferService: TransferService,
    private val offlineService: OfflineService,
) : ViewModel() {

    private val logTag = "NodeActionsVM"

    private val _loadingState = MutableLiveData(LoadingState.STARTING)
    private val _errorMessage = MutableLiveData<String?>()
    val loadingState: LiveData<LoadingState> = _loadingState
    val errorMessage: LiveData<String?> = _errorMessage

    private fun launchProcessing() {
        _loadingState.value = LoadingState.PROCESSING
        _errorMessage.value = null
    }

    /* Pass a non-empty err parameter when the process has terminated with an error*/
    private suspend fun done(err: String? = null, userMsg: String? = null) =
        withContext(Dispatchers.Main) {
            if (Str.notEmpty(err)) {
                Log.e(logTag, "$userMsg, cause: ${err ?: "-"}")
                _errorMessage.value = userMsg
            }
            _loadingState.value = LoadingState.IDLE
        }

    private fun failed(msg: String) {
        _loadingState.value = LoadingState.IDLE
        _errorMessage.value = msg
    }


    // Fire and forget in viewModelScope
    fun createFolder(parentID: StateID, name: String) {
        viewModelScope.launch {
            val errMsg = nodeService.createFolder(parentID, name)
            done(errMsg, "Could not create folder $name at $parentID")
        }
    }

    fun rename(srcID: StateID, name: String) {
        viewModelScope.launch {
            val errMsg = nodeService.rename(srcID, name)
            done(errMsg, "Could not rename $srcID to $name")
        }
    }

    fun delete(stateID: StateID) {
        viewModelScope.launch {
            val errMsg = nodeService.delete(stateID)
            done(errMsg, "Could not delete node at $stateID")
        }
    }

    fun copyTo(stateID: StateID, targetParentID: StateID) {
        // TODO better handling of scope and error messages
        CellsApp.instance.appScope.launch {
            // TODO what do we store/show?
            //   - source files
            //   - target files
            //   - processing
            val errMsg = nodeService.copy(listOf(stateID), targetParentID)
            done(errMsg, "Could not copy node $stateID to $targetParentID")
        }
    }

    fun moveTo(stateID: StateID, targetParentID: StateID) {
        // TODO better handling of scope and error messages
        CellsApp.instance.appScope.launch {
            // TODO what do we store/show?
            //   - source files
            //   - target files
            //   - processing
            val errMsg = nodeService.move(listOf(stateID), targetParentID)
            done(errMsg, "Could not move node $stateID to $targetParentID")
        }
    }

    fun emptyRecycle(stateID: StateID) {
        viewModelScope.launch {
            val errMsg = nodeService.delete(stateID)
            done(errMsg, "Could not delete node at $stateID")
        }
    }

    fun download(stateID: StateID, uri: Uri) {
        CellsApp.instance.appScope.launch {
            try {
                transferService.saveToSharedStorage(stateID, uri)
                done()
            } catch (e: SDKException) {
                done("#${e.code} - ${e.message}", "Could not save $stateID to share storage")
            }
        }
    }

    fun importFiles(stateID: StateID, uris: List<Uri>) {
        CellsApp.instance.appScope.launch {
            try {
                for (uri in uris) {
                    transferService.enqueueUpload(stateID, uri)
                }
                done()
            } catch (e: SDKException) {
                done("#${e.code} - ${e.message}", "Could import files at $stateID")
            }
        }
    }

    private var _targetForPhoto: Pair<StateID, Uri>? = null

    suspend fun preparePhoto(context: Context, parentID: StateID): Uri? =
        withContext(Dispatchers.IO) {
            val photoFile: File? = try {
                fileService.createImageFile(parentID)
            } catch (ex: IOException) {
                Log.e(logTag, "Cannot create picture file")
                ex.printStackTrace()
                // Error occurred while creating the File
                null
            }

            photoFile?.also { // Continue only if the File was successfully created
                val uri: Uri = FileProvider.getUriForFile(
                    context,
                    DEFAULT_FILE_PROVIDER_ID,
                    it
                )
                withContext(Dispatchers.Main) { // We keep the state in the current VM
                    _targetForPhoto = Pair(parentID, uri)
                }
                return@withContext uri
            }
            return@withContext null
        }

    fun uploadPhoto() {
        _targetForPhoto?.let {
            viewModelScope.launch {
                transferService.enqueueUpload(it.first, it.second)
            }
        }
    }

    fun cancelPhoto() {
        _targetForPhoto = null
    }

    fun toggleBookmark(stateID: StateID, newState: Boolean) {
        viewModelScope.launch {
            try {
                nodeService.toggleBookmark(stateID, newState)
            } catch (e: Exception) {
                Log.e(
                    logTag,
                    "Unhandled error when flaging bookmark=$newState for $stateID, cause:  ${e.message}"
                )
                e.printStackTrace()
            }
        }
    }

    fun toggleOffline(stateID: StateID, newState: Boolean) {
        viewModelScope.launch {
            offlineService.toggleOffline(stateID, newState)
        }
    }

    fun createShare(stateID: StateID) {
        viewModelScope.launch {
            nodeService.createShare(stateID)
        }
    }


//    suspend fun createShare(stateID: StateID) {
//        // FIXME
////        viewModelScope.async {
////            try {
////                nodeService.createShare(stateID)
////            } catch (e: SDKException) {
////                Log.e(logTag, "#${e.code}: ${e.message}, cause: ${e.cause?.message}")
////            }
////        }.await()
//    }

    fun removeShare(stateID: StateID) {
        viewModelScope.launch {
            nodeService.removeShare(stateID)
        }
    }

    fun restoreFromTrash(stateID: StateID) {
        viewModelScope.launch {
            nodeService.restoreNode(stateID)
        }
    }

    suspend fun getShareLink(stateID: StateID): String? = withContext(Dispatchers.IO) {
        nodeService.getNode(stateID)?.getShareAddress()
    }
}
