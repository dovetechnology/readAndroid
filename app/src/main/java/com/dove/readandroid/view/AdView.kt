package com.dove.readandroid.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.StyleRes
import androidx.cardview.widget.CardView
import com.dove.readandroid.R
import com.dove.readandroid.ui.OpenTypeHandler
import com.dove.readandroid.ui.model.AdData
import com.safframework.ext.click

class AdView : FrameLayout {

    constructor(@NonNull context: Context) : super(context) {}

    @JvmOverloads
    constructor(
        @NonNull context: Context, @Nullable attrs: AttributeSet,
        @AttrRes defStyleAttr: Int = 0, @StyleRes defStyleRes: Int = 0
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }
    init {
        initView()
    }

    lateinit var ad: AdData
    lateinit var view: View

    fun setData(ad: AdData) {
        this.ad = ad
    }

    fun initView() {
        view = View.inflate(context, R.layout.view_ad, this)
        view.findViewById<ImageView>(R.id.iv_close)
            .click {
                view.visibility = View.GONE
            }
        view.findViewById<CardView>(R.id.card_view).click {
            OpenTypeHandler(ad.openType, context, ad.forwardUrl).handle()

        }
       // addView(view)
    }

    fun getImageView(): ImageView {
        return view.findViewById(R.id.iv_ad)
    }
}
