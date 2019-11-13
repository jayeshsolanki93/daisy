package org.mozilla.reference.browser.history.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.storage.VisitInfo
import mozilla.components.feature.session.SessionUseCases
import org.mozilla.reference.browser.history.usecases.HistoryUseCases
import org.mozilla.reference.browser.utils.SingleLiveEvent

/**
 * @author Ravjit Uppal
 */
class HistoryViewModel(
    private val historyUseCases: HistoryUseCases,
    private val sessionUseCases: SessionUseCases
) : ViewModel() {

    private val historyItems = MutableLiveData<List<VisitInfo>>().apply { value = emptyList() }

    internal val clearedHistory = SingleLiveEvent<Void>()

    init {
        fetchHistoryItems()
    }

    fun getHistoryItems(): LiveData<List<VisitInfo>> {
        return historyItems
    }

    fun onItemClicked(position: Int) {
        val historyItem = historyItems.value?.get(position)
        if (historyItem != null) {
            sessionUseCases.loadUrl(historyItem.url)
        }
    }

    fun clearHistoryClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            historyUseCases.clearAllHistory()
            historyItems.postValue(emptyList())
            clearedHistory.postCall()
        }
    }

    private fun fetchHistoryItems() {
        viewModelScope.launch(Dispatchers.IO) {
            historyItems.postValue(historyUseCases.getHistory())
        }
    }
}