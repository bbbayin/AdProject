package miku.ad.prophet;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

import miku.ad.adapters.FuseAdLoader;

public class JumpUtils {
    public static boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return packageInfo != null;
    }

    public static void jumptoGooglePlay(Context activity, String packageName, String campaign) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName + "&referrer=utm_source%3D" + FuseAdLoader.getConfiguration().getProphetId() + campaign + "%26utm_campaign%3D" + FuseAdLoader.getConfiguration().getProphetId() + campaign));
            intent.setPackage("com.android.vending");
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ae) {
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            } catch (Exception e) {
            }
        }
    }

    public static void junmptoBrowser(Context activity, String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    public static void junmptoExistApps(Context activity, String packageName) {
        Intent intent = getLaunchIntentForPackage(activity, packageName);
        activity.startActivity(intent);
    }

    public static Intent getLaunchIntentForPackage(Context activity, String packageName) {
        PackageManager packageManager = activity.getPackageManager();
        Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
        intentToResolve.addCategory(Intent.CATEGORY_INFO);
        intentToResolve.setPackage(packageName);
        List<ResolveInfo> ris = packageManager.queryIntentActivities(intentToResolve, 0);

        // Otherwise, try to find a main launcher activity.
        if (ris == null || ris.size() <= 0) {
            // reuse the intent instance
            intentToResolve.removeCategory(Intent.CATEGORY_INFO);
            intentToResolve.addCategory(Intent.CATEGORY_LAUNCHER);
            intentToResolve.setPackage(packageName); // <- 这里
            ris = packageManager.queryIntentActivities(intentToResolve, 0);
        }
        if (ris == null || ris.size() <= 0) {
            return null;
        }
        Intent intent = new Intent(intentToResolve);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(ris.get(0).activityInfo.packageName,
                ris.get(0).activityInfo.name);
        return intent;
    }
}
