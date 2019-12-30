package com.dove.readandroid.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.ui.common.Constants
import com.safframework.ext.click
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.fragment_search_tag.*
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SearchTagFragment() : BaseMvcFragment() {
    lateinit var next: (String) -> Unit
    var isVideo = false

    companion object {
        fun getInstance(isVideo: Boolean = false, next: (b: String) -> Unit): SearchTagFragment {
            val fragment = SearchTagFragment()
            fragment.next = next
            fragment.isVideo = isVideo
            return fragment
        }
    }

    private var tags = arrayListOf<String>()
    lateinit var adapter: TagAdapter<String>

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_search_tag
    }

    override fun initView() {
        ivDelete.click {
            tags.clear()
            PreferenceUtils.setPrefString(mContext, Constants.HISTORY, "")
            adapter.notifyDataChanged()
        }

        tags = ArrayList()
        var history = PreferenceUtils.getPrefString(mContext, Constants.HISTORY, "")

        var list = history.split(",").toList()

        tags.addAll(list.subList(0, list.size - 1))
        val mInflater = LayoutInflater.from(mContext)
        adapter = object : TagAdapter<String>(tags) {
            override fun getView(parent: FlowLayout, position: Int, s: String): View {
                val textView = mInflater.inflate(R.layout.search_recycler_tag_item, parent, false) as TextView
                textView.text = s
                return textView
            }
        }
        tflTag.adapter = adapter
        tflTag.setOnTagClickListener { view, position, parent ->

            next(tags.get(position))
            return@setOnTagClickListener true
        }
    }

    override fun getLoadingTargetView(): View? {
        return null
    }


}
