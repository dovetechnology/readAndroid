package com.dove.readandroid.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.dove.readandroid.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/2/28 13:10
 * ===============================
 */
public class SimpleVideoView extends StandardGSYVideoPlayer {
    public SimpleVideoView(@NonNull Context context) {
        super(context);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public int getLayoutId() {
        return R.layout.layout_my_video;
    }
}
