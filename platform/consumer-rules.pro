# Amazon SDK response handling uses reflection. Keep vendor SDK internals in minified builds.
-keep class com.amazon.** { *; }
-dontwarn com.amazon.**
