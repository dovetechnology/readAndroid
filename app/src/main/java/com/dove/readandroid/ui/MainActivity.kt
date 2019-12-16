package com.dove.readandroid.ui.shujia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.base.Navigator
import com.dove.readandroid.R
import com.dove.readandroid.ui.huodong.HuodongFragment
import com.dove.readandroid.ui.me.MeFragment
import com.dove.readandroid.ui.shucheng.ShuchengFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvcActivity() {
   lateinit var mshujia:ShujiaFragment
    lateinit var  huodongfragment: HuodongFragment
    lateinit var mefragment: MeFragment
    lateinit var shuchengfragment: ShuchengFragment
    lateinit var navigator: Navigator

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_main
    }

    override fun initView(mSavedInstanceState: Bundle?) {
        navigator= Navigator(supportFragmentManager,R.id.content)
        mshujia= ShujiaFragment()
        huodongfragment=HuodongFragment()
        mefragment= MeFragment()
        shuchengfragment= ShuchengFragment()
        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.main_shujia -> navigator.showFragment(mshujia)
                    R.id.main_shucheng ->navigator.showFragment(shuchengfragment)

                R.id.main_huodong ->navigator.showFragment(huodongfragment)

                R.id.main_my ->navigator.showFragment(mefragment)

            }
            return@setOnNavigationItemSelectedListener  true
        }
        navigation.setTextSize(12f)
        navigation.setTextVisibility(true)
        navigation.enableAnimation(true)
        navigation.enableItemShiftingMode(false)
        navigation.enableShiftingMode(false)

    }

}
