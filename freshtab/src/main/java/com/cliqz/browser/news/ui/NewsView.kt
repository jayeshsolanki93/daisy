package com.cliqz.browser.news.ui

import android.animation.LayoutTransition
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.cliqz.browser.freshtab.R
import com.cliqz.browser.news.data.NewsItem
import com.cliqz.browser.news.data.Result
import kotlin.math.pow

class NewsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val expandNewsIcon = AppCompatResources.getDrawable(context, R.drawable.ic_action_expand)
    private val collapseNewsIcon = AppCompatResources.getDrawable(context, R.drawable.ic_action_collapse)
    private val newsItemHeight = resources.getDimensionPixelSize(R.dimen.three_line_list_item_height)

    private val view = LayoutInflater.from(context).inflate(R.layout.news_layout, this, true)

    private val newsLabelView = view.findViewById<TextView>(R.id.news_label)
    private val topNewsListView = view.findViewById<LinearLayout>(R.id.topnews_list)

    var presenter: Presenter? = null

    init {
        // For the animation when news item views are added to the container
        topNewsListView.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)

        newsLabelView.setOnClickListener {
            topNewsListView.layoutTransition.disableTransitionType(LayoutTransition.APPEARING)
            toggleNewsView()
            toggleNewsLabelIcon()
        }
    }

    fun displayNews(newsList: List<NewsItem>, isNewsViewExpanded: Boolean) {
        view.visibility = View.GONE
        if (newsList.isNullOrEmpty()) {
            return
        }
        showNewsWithoutToggleAnimation(isNewsViewExpanded, newsList.count())
        topNewsListView.removeAllViews()
        val inflater = LayoutInflater.from(context)
        for (newsItem in newsList) {
            val view = inflater.inflate(R.layout.three_line_list_item_layout, topNewsListView, false)
            presenter?.let { NewsItemViewHolder(view, it).bind(newsItem) }
            topNewsListView.addView(view)
        }
        toggleNewsLabelIcon()
        view.visibility = View.VISIBLE
    }

    fun hideNews() {
        view.visibility = View.GONE
    }

    private fun showNewsWithoutToggleAnimation(isNewsViewExpanded: Boolean, count: Int) {
        val collapsedHeight = newsItemHeight * COLLAPSED_NEWS_NO
        val expandedHeight = newsItemHeight * count
        val viewHeight = if (isNewsViewExpanded) expandedHeight else collapsedHeight
        topNewsListView.setViewHeight(viewHeight)
    }

    private fun showNewsView(isNewsViewExpanded: Boolean) {
        val count = topNewsListView.childCount
        val collapsedHeight = newsItemHeight * COLLAPSED_NEWS_NO
        val expandedHeight = newsItemHeight * count
        if (isNewsViewExpanded) {
            getToggleAnimation(topNewsListView, collapsedHeight, expandedHeight, count).start()
        } else {
            getToggleAnimation(topNewsListView, expandedHeight, collapsedHeight, count).start()
        }
    }

    private fun toggleNewsView() {
        presenter?.let {
            it.isNewsViewExpanded = !it.isNewsViewExpanded
            showNewsView(it.isNewsViewExpanded)
        }
    }

    private fun getToggleAnimation(
        view: View,
        startHeight: Int,
        endHeight: Int,
        childCount: Int
    ): ValueAnimator {
        val durationFactor = 45F
        val animationDuration = (durationFactor * childCount).toLong()
        val animator = ValueAnimator.ofInt(startHeight, endHeight)

        if (startHeight < endHeight) {
            // Expanding animation motion is the easing function 'easeInQuint'
            animator.interpolator = TimeInterpolator { (it * it * it * it * it) }
        } else {
            // Collapsing animation motion is the easing function 'easeOutQuint'
            animator.interpolator = TimeInterpolator { (1 - (1 - it).toDouble().pow(5.0)).toFloat() }
        }

        animator.addUpdateListener { view.setViewHeight(it.animatedValue as Int) }
        animator.duration = animationDuration
        return animator
    }

    private fun toggleNewsLabelIcon() {
        presenter?.let {
            val newsLabelIcon = if (it.isNewsViewExpanded) collapseNewsIcon else expandNewsIcon
            newsLabelView.setCompoundDrawablesWithIntrinsicBounds(null, null, newsLabelIcon, null)
        }
    }

    private fun View.setViewHeight(height: Int) {
        val params = layoutParams as LayoutParams
        params.height = height
        this.layoutParams = params
    }

    companion object {
        private const val COLLAPSED_NEWS_NO = 2
    }
}

interface Presenter {

    var isNewsViewExpanded: Boolean

    suspend fun getNews(): Result<List<NewsItem>>

    fun onOpenInNormalTab(item: NewsItem)

    fun onOpenInNewNormalTab(item: NewsItem)

    fun onOpenInPrivateTab(item: NewsItem)

    fun loadNewsItemIcon(view: ImageView, url: String)
}