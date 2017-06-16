package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.android.launcher3.compat.LauncherActivityInfoCompat;

public class IconPack {
    /*
    Useful Links:
    https://github.com/teslacoil/Example_NovaTheme
    http://stackoverflow.com/questions/7205415/getting-resources-of-another-application
    http://stackoverflow.com/questions/3890012/how-to-access-string-resource-from-another-application
     */
    private Map<String, String> icons = new ArrayMap<>();
    private Map<String, Drawable> memoryCache = new ArrayMap<>();
    private String packageName;
    private Context mContext;

    public IconPack(Map<String, String> icons, Context context, String packageName) {
        this.icons = icons;
        this.packageName = packageName;
        mContext = context;
    }


    public Drawable getIcon(LauncherActivityInfoCompat info) {
        return getIcon(info.getComponentName());
    }

    public Drawable getIcon(ActivityInfo info) {
        return getIcon(new ComponentName(info.packageName, info.name));
    }

    public Drawable getIcon(ComponentName name) {
        return getDrawable(icons.get(name.toString()));
    }

    private Drawable getDrawable(String name) {
        if (memoryCache.containsKey(name)) {
            Drawable d = memoryCache.get(name);
            return d;
        }
        File cachePath = new File(mContext.getCacheDir(), "iconpack/" + name);
        if (cachePath.exists()) {
            Bitmap b = BitmapFactory.decodeFile(cachePath.toString());
            if (b != null) {
                Drawable d = new FastBitmapDrawable(b);
                memoryCache.put(name, d);
                return d;
            }
        }
        Resources res;
        try {
            res = mContext.getPackageManager().getResourcesForApplication(packageName);
            int resourceId = res.getIdentifier(name, "drawable", packageName);
            if (0 != resourceId) {
                Bitmap b = BitmapFactory.decodeResource(res, resourceId);
                saveBitmapToFile(cachePath, b);
                Drawable drawable = new FastBitmapDrawable(b);
                memoryCache.put(name, drawable);
                return drawable;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean saveBitmapToFile(File imageFile, Bitmap bm) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.close();

            return true;
        } catch (IOException e) {
            Log.e("IconPack", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }
}
