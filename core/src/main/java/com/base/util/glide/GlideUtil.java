package com.base.util.glide;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.base.util.regex.RegexUtils;
import com.base.util.utility.StringUtil;
import com.bumptech.glide.Glide;

import java.io.File;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Administrator on 2017/12/14.
 */

public class GlideUtil {

    /**
     * 清除内存缓存.
     */
    public static void clearMemoryCache(Context mContext) {
        // This method must be called on the main thread.
        Glide.get(mContext).clearMemory();
    }

    /**
     * 清除磁盘缓存.
     */
    public static void clearDiskCache(Context mContext) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // This method must be called on a background thread.
                Glide.get(mContext).clearDiskCache();
                return null;
            }
        };
    }

    public static void setGlideImageView(Context context, String url, ImageView imageView) {
        setGlideImageViewHaveRound(context, url, imageView, 0);
    }

    public static void setGlideImageViewHaveRound(Context context, String url, ImageView imageView, int radius) {
        if(!StringUtil.isStringValid(url)){
            return;
        }
        if (RegexUtils.isURL(url)) {
            Glide.with(context).load(url)
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0))
                    .into(imageView);
        } else {
            Glide.with(context).load(new File(url))
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0))
                    .into(imageView);
        }

    }

    public static void setGlideImageViewHaveRound(Context context, String url, ImageView imageView) {
        setGlideImageViewHaveRound(context, url, imageView, 5);

    }

}
