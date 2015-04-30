# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/fengjigang/Developer/tool/android-sdk-macosx/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify
-verbose
-dontwarn
-dontskipnonpubliclibraryclassmembers
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#-libraryjars libs/ormlite-android-4.48.jar
#-libraryjars libs/ormlite-core-4.48.jar
#-libraryjars ../../external-libs/analytics-5.4.2.jar
#-libraryjars ../../External-libs/gson-2.3.1.jar
-libraryjars ../library
-libraryjars ../umeng_social_sdk_library/libs/httpmime-4.1.3.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_AtFriends.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_instagram.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_QQZone_1.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_QQZone_2.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_QQZone_3.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_renren_1.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_renren_2.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_Sina.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_tencentWB_1.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_tencentWB_2.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_tencentWB_3.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_tumblr.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_UserCenter.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_WeiXin_1.jar
-libraryjars ../umeng_social_sdk_library/libs/SocialSDK_WeiXin_2.jar
-libraryjars ../umeng_social_sdk_library/libs/umeng_social_sdk.jar

-dontwarn android.**
-dontwarn com.google.gson.**
-dontwarn android.support.**
-dontwarn com.etsy.android.grid.**
-dontwarn com.j256.ormlite.**
-dontwarn org.apache.http.**
-dontwarn com.umeng.socialize.**
-dontwarn com.tencent.**
-dontwarn com.renn.**
-dontwarn com.sina.**
-dontwarn com.nostra13.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-keepnames class * implements java.io.Serializable
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class **.R$* {
    *;
}
#umeng 更新混淆相关
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.news.yazhidao.R$*{
    public static final int *;
}
-keep public class com.umeng.fb.ui.ThreadView {
}
-keep public class * extends com.umeng.**
-keep class com.umeng.** { *; }


-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.news.yazhidao.entity.** { *; }
-keep class org.json.** {*;}
-keep class com.google.gson.** {*;}
-keep class com.nostra13.**{*;}
-keep class com.sina.**{*;}
-keep class com.renn.**{*;}
-keep class com.tencent.**{*;}
-keep class org.apache.http.**{*;}
-keep class com.etsy.android.grid.**{*;}
-keep class com.umeng.socialize.**{*;}
-keep class com.j256.ormlite.**  {*;}
-keep class android.webkit.**{*;}
-keep class android.**{*;}
-keep class android.support.**{*;}