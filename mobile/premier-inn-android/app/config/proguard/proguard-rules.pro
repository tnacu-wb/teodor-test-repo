# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/peppasc/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
# Crashlytics
-keepattributes *Annotation*                      # Keep Crashlytics annotations
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

-keep class com.google.firebase.crashlytics.* { *; }
-dontwarn com.google.firebase.crashlytics.**
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** v(...);
#}

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

# GSON Specific
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
-keepclassmembers,allowoptimization enum com.whitbread.premierinn.** {
    <fields>;
}
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class android.support.v7.app.AppCompatViewInflater{ <init>(...); }

-keepclassmembers class com.whitbread.premierinn.notifications.NotificationsInfo {
    <fields>;
}
#
#-keepclassmembers class com.whitbread.premierinn.domain.account.AccountDynamicLinks {
#    <fields>;
#}
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
 -keepclasseswithmembers class * {
    <init>(...);
    @com.google.gson.annotations.SerializedName <fields>;
 }
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-keep class com.whitbread.premierinn.data.remote.graphql.contracts.** { *; }
-keep class com.whitbread.premierinn.businessbooker.** { *; }

# For all Graphql data class request model to be not obfuscated
-keep class  com.whitbread.premierinn.domain.graphql.requestBodyModels.** { *; }
-keep class  com.whitbread.premierinn.domain.** { *; }

-keepclassmembers class org.threeten.bp.Ser { <init>(); }

-dontwarn javax.imageio.spi.**

-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }