# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#Retrofit
    # Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
-keep class retrofit2.** { *; }
-keep class okio.** { *; }
-dontwarn retrofit2.**
-dontwarn okio.**
    # Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}


# support constraint
-dontwarn android.support.constraint.**
-keep class android.support.constraint.** { *; }
-keep interface android.support.constraint.** { *; }
-keep public class android.support.constraint.R$* { *; }

# gson
#--------------------

#okhttp3
    # OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform


#AWS
-keep class org.apache.commons.logging.**               { *; }
-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class org.codehaus.**                             { *; }
-keepattributes Signature,*Annotation*

-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn com.fasterxml.**


#com.google.android.material
-keep class com.google.android.material.** { *; }