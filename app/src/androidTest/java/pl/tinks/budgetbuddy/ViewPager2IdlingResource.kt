package pl.tinks.budgetbuddy

import androidx.test.espresso.IdlingResource
import androidx.viewpager2.widget.ViewPager2

class ViewPager2IdlingResource(viewPager: ViewPager2) : IdlingResource {
    companion object {
        private const val NAME = "ViewPagerIdlingResource"
    }

    private var isIdle = true
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    init {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                isIdle = (state == ViewPager2.SCROLL_STATE_IDLE)

                if (isIdle && resourceCallback != null) resourceCallback!!.onTransitionToIdle()
            }
        })
    }

    override fun getName() = NAME

    override fun isIdleNow() = isIdle

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}
