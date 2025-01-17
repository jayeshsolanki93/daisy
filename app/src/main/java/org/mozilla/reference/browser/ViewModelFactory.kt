package org.mozilla.reference.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.mozilla.reference.browser.history.ui.HistoryViewModel

/**
 * @author Ravjit Uppal
 *
 * Custom Viewmodel factory that takes care of instantiating view models with non-default constructors
 */
class ViewModelFactory(private val applicationContext: BrowserApplication) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == HistoryViewModel::class.java) {
            return HistoryViewModel(
                applicationContext.components.useCases.historyUseCases) as T
        }
        throw IllegalArgumentException("Unknown model class $modelClass")
    }

    companion object {
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: BrowserApplication): ViewModelFactory {
            if (INSTANCE == null) {
                INSTANCE = ViewModelFactory(context)
            }
            return INSTANCE!!
        }
    }
}