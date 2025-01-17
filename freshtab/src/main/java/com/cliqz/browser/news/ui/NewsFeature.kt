package com.cliqz.browser.news.ui

import android.content.Context
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import com.cliqz.browser.news.domain.GetNewsUseCase
import kotlinx.coroutines.CoroutineScope
import mozilla.components.browser.icons.BrowserIcons
import mozilla.components.feature.session.SessionUseCases.LoadUrlUseCase
import mozilla.components.support.base.feature.LifecycleAwareFeature

class NewsFeature(
    context: Context,
    private val newsView: NewsView,
    scope: CoroutineScope,
    loadUrlUseCase: LoadUrlUseCase,
    newsUseCase: GetNewsUseCase,
    icons: BrowserIcons? = null
) : LifecycleAwareFeature {

    @VisibleForTesting
    internal var presenter = DefaultNewsPresenter(
        context,
        newsView,
        scope,
        loadUrlUseCase,
        newsUseCase,
        icons
    )

    override fun start() {
        presenter.start()
    }

    fun hideNews() {
        newsView.hideNews()
    }

    override fun stop() {
        presenter.stop()
    }
}
