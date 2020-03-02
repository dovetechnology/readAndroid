package com.dove.rea

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.view.RatioImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.ui.model.Fenlei
import com.dove.readandroid.ui.shucheng.FenLeiAdapter
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.shucheng.FenleiDetailActivity
import com.safframework.ext.dp2px
import com.safframework.ext.screenWidth
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class FenleiFragment : BaseRefreshFragment<Fenlei>() {
    var width = 0;
    override fun initView() {
        super.initView()
        // recyclerview.layoutManager = GridLayoutManager(mContext, 2)
        recyclerview.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy =
                    androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
//        mRecyclerview.viewTreeObserver.addOnGlobalLayoutListener {
//            mRecyclerview.viewTreeObserver.removeGlobalOnLayoutListener { this }
//
//
//        }
    }

    override fun onFirstUserVisible() {
        super.onFirstUserVisible()
        toggleShowLoading(true)
        width = (mRecyclerview.width - mContext.dp2px(4) * 4) / 5
        (mAdapter as FenLeiAdapter).width=width

        requestData()
    }

    override fun initAdapter() {
        mAdapter = FenLeiAdapter(R.layout.item_fenlei, mList)
        mAdapter.setOnItemClickListener { adapter, view, position ->

            start(FenleiDetailActivity::class.java, Bundle().apply {
                putString("data", mList.get(position).id)
                putString("title", mList.get(position).name)
            })
        }
    }

    override fun requestData() {
        http().mApiService.tag()
            .get3(next = {

                //                it?.forEachIndexed { index, fenlei ->
//                    when(index)
//                    {
//                        0-> it[index].url=R.drawable.dushi
//                    }
//                }
                loadComplete(it ?: null)
            }, err = {
                loadError(it)
            })
    }


}