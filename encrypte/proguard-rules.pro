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

# 保留 BouncyCastle 的所有类及其成员，防止混淆
# org.bouncycastle.**：匹配 org.bouncycastle 包下的所有类及其子包中的类。
# { *; }：表示保留类中的所有字段和方法。
-keep class org.bouncycastle.** { *; }

# 忽略与 BouncyCastle 相关的警告
# -dontwarn org.bouncycastle.**