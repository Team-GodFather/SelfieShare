package com.godfather.selfieshare.utils;

import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.godfather.selfieshare.AppMain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class FileHelper {
    private static final String LOG_TAG = "FileUtils";
    private static final String _DATA = "_data";

    /**
     * Returns the real path of the given URI string.
     * If the given URI string represents a content:// URI, the real path is retrieved from the media store.
     *
     * @param uriString the URI string of the audio/image/video
     * @return the full path to the file
     */
    @SuppressWarnings("deprecation")
    public static String getRealPath(String uriString, AppMain appMain) {
        String realPath = null;

        if (uriString.startsWith("content://")) {
            String[] proj = {_DATA};
            Cursor cursor = appMain.getCurrentActivity().managedQuery(Uri.parse(uriString), proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(_DATA);
            cursor.moveToFirst();
            realPath = cursor.getString(column_index);
            if (realPath == null) {
//                LOG.e(LOG_TAG, "Could get real path for URI string %s", uriString);
            }
        } else if (uriString.startsWith("file://")) {
            realPath = uriString.substring(7);
            if (realPath.startsWith("/android_asset/")) {
//                LOG.e(LOG_TAG, "Cannot get real path for URI string %s because it is a file:///android_asset/ URI.", uriString);
                realPath = null;
            }
        } else {
            realPath = uriString;
        }

        return realPath;
    }

    /**
     * Returns the real path of the given URI.
     * If the given URI is a content:// URI, the real path is retrieved from the media store.
     *
     * @param uri the URI of the audio/image/video
     * @return the full path to the file
     */
    public static String getRealPath(Uri uri, AppMain appMain) {
        return FileHelper.getRealPath(uri.toString(), appMain);
    }

    /**
     * Returns an input stream based on given URI string.
     *
     * @param uriString the URI string from which to obtain the input stream
     * @return an input stream into the data at the given URI or null if given an invalid URI string
     * @throws IOException
     */
    public static InputStream getInputStreamFromUriString(String uriString, AppMain appMain) throws IOException {
        if (uriString.startsWith("content")) {
            Uri uri = Uri.parse(uriString);
            return appMain.getCurrentActivity().getContentResolver().openInputStream(uri);
        } else if (uriString.startsWith("file://")) {
            int question = uriString.indexOf("?");
            if (question > -1) {
                uriString = uriString.substring(0, question);
            }
            if (uriString.startsWith("file:///android_asset/")) {
                Uri uri = Uri.parse(uriString);
                String relativePath = uri.getPath().substring(15);
                return appMain.getCurrentActivity().getAssets().open(relativePath);
            } else {
                return new FileInputStream(getRealPath(uriString, appMain));
            }
        } else {
            return new FileInputStream(getRealPath(uriString, appMain));
        }
    }

    /**
     * Removes the "file://" prefix from the given URI string, if applicable.
     * If the given URI string doesn't have a "file://" prefix, it is returned unchanged.
     *
     * @param uriString the URI string to operate on
     * @return a path without the "file://" prefix
     */
    public static String stripFileProtocol(String uriString) {
        if (uriString.startsWith("file://")) {
            uriString = uriString.substring(7);
        }
        return uriString;
    }

    public static String getMimeTypeForExtension(String path) {
        String extension = path;
        int lastDot = extension.lastIndexOf('.');
        if (lastDot != -1) {
            extension = extension.substring(lastDot + 1);
        }
        // Convert the URI string to lower case to ensure compatibility with MimeTypeMap (see CB-2185).
        extension = extension.toLowerCase(Locale.getDefault());
        if (extension.equals("3ga")) {
            return "audio/3gpp";
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * Returns the mime type of the data specified by the given URI string.
     *
     * @param uriString the URI string of the data
     * @return the mime type of the specified data
     */
    public static String getMimeType(String uriString, AppMain appMain) {
        String mimeType = null;

        Uri uri = Uri.parse(uriString);
        if (uriString.startsWith("content://")) {
            mimeType = appMain.getCurrentActivity().getContentResolver().getType(uri);
        } else {
            mimeType = getMimeTypeForExtension(uri.getPath());
        }

        return mimeType;
    }
}