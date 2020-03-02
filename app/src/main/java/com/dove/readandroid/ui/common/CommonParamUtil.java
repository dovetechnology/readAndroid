package com.dove.readandroid.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import com.dove.readandroid.ui.App;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * ===============================
 * 描    述：公共参数工具类
 * 作    者：pjw
 * 创建日期：2019/4/15 18:11
 * ===============================
 */
public class CommonParamUtil {

    private static String uuid;             //设备ID
    private static String imei;             //IMEI
    private static String imsi;             //运营商
    private static String strNetworkType;   //网络类型
    private static String versionName;      //版本名称
    private static String phoneModel;       //手机型号
    private static String phoneMfr;         //手机厂商
    private static String appSign;          //APP签名
    private static String screenSize;       //屏幕分辨率
    private static String versionSDK;       //手机系统版本

    private static String userAgent;        //Agent字符串

    /**
     * 设备唯一标识
     */
    public static String getUUID(Context context) {
        if (!TextUtils.isEmpty(uuid)) {
            return uuid;
        }
        String imei = "android";
        String mac = "android";
        try {
            //获取imei号
//            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//
//                imei = TelephonyMgr.getDeviceId();
//            }
            //获取mac地址
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mac = wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新组合的deviceId
        String deviceId = imei + mac;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
        m.update(deviceId.getBytes(), 0, deviceId.length());
        byte p_md5Data[] = m.digest();
        String uniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            if (b <= 0xF) {
                uniqueID += "0";
            }
            uniqueID += Integer.toHexString(b);
        }
        uniqueID = uniqueID.toUpperCase();
        if (uniqueID != null) {
            uuid = uniqueID;
            return uniqueID;
        }
        return UUID.randomUUID().toString();
    }

    public static void setUuid(String uuid) {
        uuid = uuid == null ? "" : uuid;
    }

    public static String getIMEI(Context context) {
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        try {
            //获取imei号
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = TelephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(imei)) {
            return getUUID(context);
        } else {
            return imei;
        }
    }

    /**
     * 获取用户当前使用网络类型
     */
    public static String getNetType(Context context, boolean notifyStatus) {
        if (!TextUtils.isEmpty(strNetworkType) && !notifyStatus) {
            return strNetworkType;
        }
        ConnectivityManager cmg = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cmg.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI网络";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                strNetworkType = "2G/3G/4G";
            } else {
                strNetworkType = "未知网络";
            }
        } else {
            strNetworkType = "离线网络";
        }
        return strNetworkType;
    }

    /**
     * 获取用户当前使用的运营商类型
     */
    public static String getIMSI(Context context) {
        if (!TextUtils.isEmpty(imsi)) {
            return imsi;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = telephonyManager.getSubscriberId();
            imsi = imsi.substring(imsi.length() - 2, imsi.length());
        } catch (Exception e) {
            imsi = null;
        }
        return ((imsi == null) ? "" : imsi);
    }

    /**
     * 版本名字
     */
    public static String getVersionName(Context context) {
        if (!TextUtils.isEmpty(versionName)) {
            return versionName;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = pInfo.versionName;
            return (versionName != null) ? versionName : "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 手机型号
     */
    public static String getPhoneModel() {
        if (!TextUtils.isEmpty(phoneModel)) {
            return phoneModel;
        }
        phoneModel = Build.MODEL;
        return (phoneModel != null) ? phoneModel : "";
    }

    /**
     * 手机厂商
     */
    public static String getPhoneManufacturer() {
        if (!TextUtils.isEmpty(phoneMfr)) {
            return phoneMfr;
        }
        phoneMfr = Build.MANUFACTURER;
        return (phoneMfr != null) ? phoneMfr : "杂牌";
    }

    /**
     * 手机系统版本
     */
    public static String getVersionSDK() {
        if (!TextUtils.isEmpty(versionSDK)) {
            return versionSDK;
        }
        versionSDK = Build.VERSION.SDK_INT + "";
        return (versionSDK != null) ? versionSDK : "未知";
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return "0.0.0.0";
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * APP的应用签名
     */
    public static String getAppSign(Context context, String appPackageName) {
        if (!TextUtils.isEmpty(appSign)) {
            return appSign;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageinfo = pm.getPackageInfo(appPackageName, PackageManager.GET_SIGNATURES);
            if (packageinfo != null) {
                appSign = signatureSHA1(packageinfo.signatures);
                return appSign;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static final char hexDigits[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(hexDigits[(b >> 4) & 0x0F]);
            hex.append(hexDigits[b & 0x0F]);
        }
        return hex.toString();
    }

    public static String signatureSHA1(Signature[] signatures) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            if (signatures != null) {
                for (Signature s : signatures)
                    digest.update(s.toByteArray());
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            return "";
        }
    }

    public static String userAgent() {
        if (!TextUtils.isEmpty(userAgent)) {
            return userAgent;
        }
        Context context = App.instance;
        userAgent = "ANDROID:" +
                CommonParamUtil.getVersionName(context) + ":" +//版本名称
                CommonParamUtil.getPhoneModel() + ":" +//手机型号
                CommonParamUtil.getPhoneManufacturer() + ":" +//手机厂商
                CommonParamUtil.getVersionSDK() + ":" +//手机系统版本
                CommonParamUtil.getUUID(context);//设备唯一标识
        return userAgent;
    }


}
