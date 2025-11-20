# Add project specific ProGuard rules here.

# TensorFlow Lite
-keep class org.tensorflow.** { *; }
-keepclassmembers class org.tensorflow.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }

# Compose
-dontwarn androidx.compose.**

# Keep data classes
-keep class com.spywarescanner.app.data.model.** { *; }
