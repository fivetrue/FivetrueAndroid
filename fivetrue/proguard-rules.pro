# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kwonojin/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-dontwarn org.apache.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.internal.**
-dontwarn com.google.api.client.http.apache.**
-dontwarn okio.**



-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

-keep class com.google.firebase.** { *; }
-keep class android.support.** { *; }


-keep class com.fivetrue.fivetrueandroid.google.** { *; }
-keep class import com.android.volley.** { *; }
-keep class com.fivetrue.fivetrueandroid.net.BaseApiResponse { <fields>; }
-keep class com.fivetrue.fivetrueandroid.net.BaseApiRequest { <fields>; }
-keep class com.fivetrue.fivetrueandroid.utils.CustomWebViewClient { <fields>; }

