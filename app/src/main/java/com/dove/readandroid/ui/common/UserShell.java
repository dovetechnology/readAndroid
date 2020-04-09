package com.dove.readandroid.ui.common;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.appbaselib.app.AppManager;
import com.appbaselib.utils.PreferenceUtils;
import com.dove.readandroid.event.UserEvent;
import com.dove.readandroid.ui.App;
import com.dove.readandroid.ui.RetrofitHelper;
import com.dove.readandroid.ui.model.UserBean;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

/**
 * ===============================
 * 描    述：用户信息处理类
 * 作    者：pjw
 * 创建日期：2019/3/26 9:49
 * ===============================
 */
public class UserShell {


    private static class UserShellInstance {
        private static final UserShell INSTANCE = new UserShell();
    }

    private UserShell() {

        String u = getUserJson();
        if (!TextUtils.isEmpty(u)) {
            mUserBean = new Gson().fromJson(u, UserBean.class);
            isLogin = true;
        }

    }


    public static UserShell getInstance() {
        return UserShellInstance.INSTANCE;
    }

    private String token;
    private boolean isLogin = false;//是否登陆
    private UserBean mUserBean;

    /**
     * 返回用户是否登录
     *
     * @return 登录返回true
     */
    public boolean isLogin() {
        return isLogin;
    }

    public String getToken() {
        if (token == null) {
            token = PreferenceUtils.getPrefString(App.instance, Constants.TOKEN, "");
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        PreferenceUtils.setPrefString(App.instance, Constants.TOKEN, token);
    }

    public UserBean getUserBean() {
        return mUserBean;
    }

    public void setUserBean(UserBean userBean) {
        isLogin = true;
        mUserBean = userBean;
        setUserJson(new Gson().toJson(mUserBean));
    }

    private String getUserJson() {
        return PreferenceUtils.getPrefString(App.instance, Constants.USER, "");
    }

    private void setUserJson(String json) {
        PreferenceUtils.setPrefString(App.instance, Constants.USER, json);
    }

    //退出登录
    public void exitLogin() {
        //删除用户信息
        PreferenceUtils.clearDefaultPreference(App.instance);
        mUserBean = null;
        isLogin = false;
        token = "";
        //清除设置界面的用户信息
        EventBus.getDefault().post(new UserEvent());

    }
//
//    public void exitLoginButSavePre() {
//        mUserBean = null;
//        isLogin = false;
//        token = "";
//    }

}
