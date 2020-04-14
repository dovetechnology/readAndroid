package com.dove.readandroid.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.dove.readandroid.network.SimpleDownloadListener;
import com.dove.readandroid.ui.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;

/**
 * @author hht
 * @Description: TODO
 * @date 2017/2/9 0009
 */
public class FileUtlis {
    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if (files == null) {
            return 0;
        }
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(File dir) {
        long fileS = getDirSize(dir);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static boolean deleFile(File dir) {
        if (dir == null) {
            return true;
        }
        if (!dir.isDirectory()) {
            return true;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleFile(file);
            }
        }
        return true;
    }

    /**
     * 文件转base64字符串
     *
     * @return
     */
    public static String fileToBase64(File mFile) {
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(mFile);
        } catch (FileNotFoundException mE) {
            mE.printStackTrace();
        }
        byte[] buffer = new byte[(int) mFile.length()];
        try {
            inputFile.read(buffer);
        } catch (IOException mE) {
            mE.printStackTrace();
        }
        try {
            inputFile.close();
        } catch (IOException mE) {
            mE.printStackTrace();
        }
        return "data:image/" + getSuffix(mFile) + ";base64," + Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    public static String fileToBase64(Uri mUri) {
        File mFile = null;
        try {
            mFile = new File(new URI(mUri.toString()));
        } catch (URISyntaxException mE) {
            mE.printStackTrace();
        }

        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(mFile);
        } catch (FileNotFoundException mE) {
            mE.printStackTrace();
        }
        byte[] buffer = new byte[(int) mFile.length()];
        try {
            inputFile.read(buffer);
        } catch (IOException mE) {
            mE.printStackTrace();
        }
        try {
            inputFile.close();
        } catch (IOException mE) {
            mE.printStackTrace();
        }
        return "data:image/" + getSuffix(mFile) + ";base64," + Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    public static String getSuffix(File mFile) {
        String fileName = mFile.getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 通过 uri获取文件地址
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        ;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(App.instance.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将asset文件写入缓存
     */
    public static String copyAssetAndWrite(String fileName) {
        try {
            File cacheDir = App.instance.getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    return "";
                }
            } else {
                if (outFile.length() > 10) {//表示已经写入一次
                    return outFile.getAbsolutePath();
                }
            }
            InputStream is = App.instance.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return outFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    public static String getPathDir(String path) {
        //获取文件名之前的目录
        String directoryStr = path.substring(0, path.lastIndexOf(File.separator));

        System.out.println("File.separator = " + File.separator);
        System.out.println("path.length() = " + path.length() + "\npath.lastIndexOf(File.separator) = " + path.lastIndexOf(File.separator));
        return directoryStr;
    }


    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param file
     */
    public static void writeFile(InputStream inputString, File file) {
        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("未创建文件", "-----");
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            inputString.close();
            fos.close();

        } catch (FileNotFoundException e) {
            Log.e("文件未找到", "-----");

        } catch (IOException e) {
            Log.e("IO", "-----");

        }

    }

    public static void writeFile2(InputStream inputString, long length, File file, SimpleDownloadListener mSimpleDownloadListener) {
        //创建文件
        long totalLength = length;
        long currentLength = 0;
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("未创建文件", "-----");
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
                currentLength += len;
                mSimpleDownloadListener.onProgress((int) (100 * currentLength / totalLength));
            }
            inputString.close();
            fos.close();

        } catch (FileNotFoundException e) {
            Log.e("文件未找到", e.getMessage());

        } catch (IOException e) {
            Log.e("IO", e.getMessage());

        }

    }


    private static final String TAG = "DownLoadManager";

    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";

    private static String PNG_CONTENTTYPE = "image/png";

    private static String JPG_CONTENTTYPE = "image/jpg";

    private static String fileSuffix = "";

    public static boolean writeResponseBodyToDisk(Context context,ResponseBody body, File file) {

        Log.d("File", "contentType:>>>>" + body.contentType().toString());

        String type = body.contentType().toString();

        String path = context.getExternalFilesDir(null) + File.separator + System.currentTimeMillis() + fileSuffix;


        if (type.equals(APK_CONTENTTYPE)) {

            fileSuffix = ".apk";
        } else if (type.equals(PNG_CONTENTTYPE)) {
            fileSuffix = ".png";
        }

        // 其他类型同上 自己判断加入.....

        try {
            File futureStudioIconFile = new File(path);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("File", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();


                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
