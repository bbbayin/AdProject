# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\doriscoco\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-ignorewarnings

-keepattributes Signature,*Annotation*

-keep public class * extends android.myApplication.Activity
-keep public class * extends android.myApplication.Application
-keep public class * extends android.myApplication.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.myApplication.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.polestar.videodownloader.reading.*
-keep public class com.google.** { public *;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.myApplication.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  *;
  public static final android.os.Parcelable$Creator *;
}

-keep public class miku.ad.AdConfig {*;}
-keep public class miku.ad.SDKConfiguration {*;}
-keep public class miku.ad.SDKConfiguration$* {*;}
-keep public class miku.firebase.BaseDataReportUtils {*;}
-keep public class miku.firebase.BaseDataReportUtils$* {*;}
-keep public class miku.ad.AdViewBinder$* {*;}
-keep public class miku.ad.AdViewBinder {*;}
-keep public class miku.ad.adapters.FuseAdLoader{*;}
-keep public class miku.ad.adapters.FuseAdLoader$*{*;}
-keep public class miku.ad.AdConstants$*{*;}
-keep public class miku.ad.AdConstants{*;}
-keep public class miku.ad.adapters.AdBaseListener{*;}
-keep public class miku.ad.adapters.IAdAdapter{*;}
-keep public class miku.ad.adapters.IAdLoadListener{*;}


-keepattributes SourceFile,LineNumberTable
-keep class com.inmobi.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{
     public *;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{
     public *;
}
# skip the Picasso library classes
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.okhttp.**
# skip Moat classes
-keep class com.moat.** {*;}
-dontwarn com.moat.**
# skip IAB classes
-keep class com.iab.** {*;}
-dontwarn com.iab.**