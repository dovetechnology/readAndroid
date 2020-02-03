package com.dove.readandroid.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelLocaImgUtil {
    /**
     * 调用失败
     */
    public static final int CALLERROR = -1;
    /**
     * 调用正常
     */
    public static final int CALLNORMAL = 1;

    public static final String FILEPROVIDER = "com.dove.fileprovider";

    @TargetApi(23)
    private boolean getPersimmions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            addPermission(activity, permissions, Manifest.permission.READ_EXTERNAL_STORAGE);
            addPermission(activity, permissions, Manifest.permission.CAMERA);
            if (permissions.size() > 0) {
                activity.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    private boolean addPermission(Activity activity, ArrayList<String> permissionsList, String permission) {
        // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
        if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 启动相机获取图片
     *
     * @param context     引用上下文
     * @param savePath    图片保存路径
     * @param requestCode 处理返回requestCode
     */
    public int startCamera(Activity context, String savePath, int requestCode) {
        Uri uri = Uri.fromFile(new File(savePath));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, FILEPROVIDER, new File(savePath));
        }
        return startCamera(context, uri, requestCode);
    }

    /**
     * 启动相机获取图片
     *
     * @param context     引用上下文
     * @param savePath    图片保存路径
     * @param requestCode 处理返回requestCode
     */
    public int startCamera(Activity context, Uri savePath, int requestCode) {
        if (getPersimmions(context, requestCode)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.putExtra("outputFormat", "JPG");//返回格式
            try {
                context.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                return CALLERROR;
            }
        }
        return CALLNORMAL;
    }

    /**
     * 启动相机获取图片
     *
     * @param context     引用上下文
     * @param savePath    图片保存路径
     * @param requestCode 处理返回requestCode
     */
    public int startCamera(Fragment context, String savePath, int requestCode) {
        Uri uri = Uri.fromFile(new File(savePath));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context.getContext(), FILEPROVIDER, new File(savePath));
        }
        return startCamera(context, uri, requestCode);
    }

    /**
     * 启动相机获取图片
     *
     * @param context     引用上下文
     * @param savePath    图片保存路径
     * @param requestCode 处理返回requestCode
     */
    public int startCamera(Fragment context, Uri savePath, int requestCode) {
        if (getPersimmions(context.getActivity(), requestCode)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savePath);
            intent.putExtra("outputFormat", "JPG");//返回格式
            try {
                context.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                return CALLERROR;
            }
            return CALLNORMAL;
        }
        return CALLERROR;
    }

    /**
     * 启动图库选择图片
     *
     * @param context     引用上下文
     * @param requestCode 处理返回requestCode
     */
    public int startGallery(Activity context, int requestCode) {
        if (getPersimmions(context, requestCode)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //     intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*;image/*");
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/*","video/*"});
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                context.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                return CALLERROR;
            }
        }
        return CALLNORMAL;
    }

    /**
     * 启动图库选择图片
     *
     * @param context     引用上下文
     * @param requestCode 处理返回requestCode
     */
    public int startGallery(Fragment context, int requestCode) {
        if (getPersimmions(context.getActivity(), requestCode)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            try {
                context.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                return CALLERROR;
            }
        }
        return CALLNORMAL;
    }

    /**
     * 启动系统裁剪
     *
     * @param context     引用上下文
     * @param imgPath     需要裁剪图片路径
     * @param savePath    裁剪后输出路径
     * @param requestCode 操作标示
     */
    public int startTailor(Activity context, String imgPath, String savePath, int requestCode) {
        Uri uri = Uri.fromFile(new File(savePath)), uri1 = Uri.fromFile(new File(imgPath));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(savePath);
//            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
//            uri = FileProvider.getUriForFile(context, FILEPROVIDER, new File(savePath));
            uri1 = FileProvider.getUriForFile(context, FILEPROVIDER, new File(imgPath));
        } else {
            try {
                new File(savePath).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return startTailor(context, uri1, uri, requestCode);
    }

    /**
     * 启动系统裁剪
     *
     * @param context     引用上下文
     * @param imgPath     需要裁剪图片路径
     * @param savePath    裁剪后输出路径
     * @param requestCode 操作标示
     */
    public int startTailor(Activity context, Uri imgPath, Uri savePath, int requestCode) {
        if (getPersimmions(context, requestCode)) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(imgPath, "image/*");
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            // 宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 剪裁图片的宽高
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savePath);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            try {
                context.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                return CALLERROR;
            }
        }
        return CALLNORMAL;
    }

    /**
     * 调用图库获取图片
     *
     * @param context 引用上下文
     * @param uri     返回uri
     * @return 返回选择图片路径
     */
    public String getPath(final Context context, final Uri uri) {
        String imagePath = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    imagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                imagePath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                imagePath = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                imagePath = uri.getLastPathSegment();
            else
                imagePath = getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return isImageYpte(imagePath) ? imagePath : null;
    }

    public boolean isImageYpte(String path) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0)
            return false;
        //图片支持类型
//        addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
//        addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
//        addFileType("GIF", FILE_TYPE_GIF, "image/gif");
//        addFileType("PNG", FILE_TYPE_PNG, "image/png");
//        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
//        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");
//        TextUtils.equals(mType, "JPG") || TextUtils.equals(mType, "JPEG") || TextUtils.equals(mType, "GIF") || TextUtils.equals(mType, "PNG") || TextUtils.equals(mType, "BMP") || TextUtils.equals(mType, "WBMP")
        String type = path.substring(lastDot + 1).toUpperCase();
        if (TextUtils.isEmpty(type))
            return false;
        if (TextUtils.equals(type, "JPG") || TextUtils.equals(type, "JPEG")
                || TextUtils.equals(type, "GIF") ||
                TextUtils.equals(type, "PNG") || TextUtils.equals(type, "BMP")
                ||type.equalsIgnoreCase("mp4")    ||type.equalsIgnoreCase("3gp")    ||type.equalsIgnoreCase("avi")) {
            return true;
        }
        return false;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
