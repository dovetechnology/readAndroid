package com.dove.rea

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.appbaselib.base.BaseMvcFragment
import com.dove.readandroid.R
import com.dove.readandroid.ui.SearchActivity
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_shucheng.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class ShuchengFragment : BaseMvcFragment() {

    private lateinit var tabData: ArrayList<CustomTabEntity>

    lateinit var jingxuanFragment: JingxuanFragment;
    lateinit var paihangFragment: PaihangFragment
    lateinit var fenleiFragment: FenleiFragment
    public var fragments = arrayListOf<Fragment>()


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_shucheng
    }

    override fun initView() {
        jingxuanFragment = JingxuanFragment(){
            vpView.setCurrentItem(2)
        }
        fenleiFragment = FenleiFragment()
        paihangFragment = PaihangFragment()
        fragments.add(jingxuanFragment)
        fragments.add(fenleiFragment)
        fragments.add(paihangFragment)

        vpView!!.offscreenPageLimit = 3
        tabData = ArrayList<CustomTabEntity>(3)
        tabData.add(TabData("精选"))
        tabData.add(TabData("分类"))
        tabData.add(TabData("排行"))
        ctlTab.setTabData(tabData)

        ctlTab!!.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                vpView!!.currentItem = position
            }

            override fun onTabReselect(position: Int) {

            }
        })
        vpView!!.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                ctlTab!!.currentTab = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        vpView.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> return jingxuanFragment
                    1 -> return fenleiFragment
                    2 -> return paihangFragment

                }
                return jingxuanFragment
            }

            override fun getCount(): Int {
                return 3
            }
        }
        etSearch.click {

            start(SearchActivity::class.java)

        }
    }

    inner class TabData constructor(private var title: String) : CustomTabEntity {

        override fun getTabTitle(): String {
            return title
        }

        override fun getTabSelectedIcon(): Int {
            return 0
        }

        override fun getTabUnselectedIcon(): Int {
            return 0
        }

    }
}