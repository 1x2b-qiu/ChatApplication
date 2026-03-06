############ Hilt (Hilt 默认会自动处理大部分规则，显式保留关键类) ############
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *

############ Retrofit & Gson (核心：保护模型和注解) ############
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

# 保留 Gson 相关注解（非常重要，如果你用了 @SerializedName）
-keep class com.google.gson.annotations.** { *; }

# 保留 Retrofit 接口及其方法签名
-keep interface com.example.chatapplication.data.remote.retrofit.api.** { *; }

# 核心：保留所有网络交互的数据模型 (DTO)
# 使用 { *; } 确保保留所有字段名、方法和构造函数
-keep class com.example.chatapplication.data.remote.retrofit.response.** { *; }
-keep class com.example.chatapplication.data.remote.retrofit.request.** { *; }
-keep class com.example.chatapplication.data.remote.socket.dto.** { *; }

# 业务模型（如果 UI 层直接使用了 domain 里的模型，也建议保留）
-keep class com.example.chatapplication.domain.model.** { *; }

# 额外保护：如果模型中有 Enum（枚举），必须保留
-keepclassmembers enum * { *; }

############ Socket.IO & OkHttp ############
-keep class io.socket.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# Socket.io 依赖的 JSON 库
-keep class org.json.** { *; }

############ Room (Room 编译后会生成实现类，需要保护) ############
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}

############ 其他通用规则 ############
# 保留所有包含 @Keep 注解的类/成员
-keep @androidx.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}