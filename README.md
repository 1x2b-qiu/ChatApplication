ChatApplication
================

一个基于 **Jetpack Compose** 的 Android 即时通讯应用，支持注册登录、好友添加、会话列表、实时消息收发、本地持久化和系统通知跳转等完整聊天闭环。

---

## 功能简介

- **账号体系**
  - 手机号 + 密码注册、登录
  - 完善个人资料（头像、昵称、性别）
- **联系人与好友申请**
  - 搜索好友、发送好友申请
  - 处理我收到/我发出的好友申请
  - 好友同意后自动创建会话
- **聊天与会话**
  - 一对一聊天
  - 会话列表、最后一条消息、时间、未读数
  - 会话置顶、删除会话
- **实时与通知**
  - 基于 Socket.IO 的实时消息收发
  - 前台常驻服务维持长连接
  - 收到消息时更新未读数并发送系统通知
  - 点击通知直接跳转对应会话页面
- **本地数据与头像缓存**
  - 使用 Room 本地持久化用户、联系人、会话、消息
  - 使用 DataStore 存储登录态（userId）
  - 头像 URL 本地化缓存，并做版本清理避免堆积

---

## 技术栈与依赖

- **语言与基础**
  - Kotlin
  - Android SDK 24+（`minSdk = 24`，`targetSdk = 36`）
- **UI**
  - Jetpack Compose
  - Material 3
  - Navigation Compose
  - SplashScreen API
- **架构与依赖注入**
  - MVVM + Repository 分层
  - Hilt（`@HiltAndroidApp`、`@AndroidEntryPoint`）
- **数据持久化**
  - Room（会话、消息、联系人、申请等）
  - DataStore Preferences（登录态）
- **网络与实时通信**
  - Retrofit + OkHttp（REST API）
  - Socket.IO Client（实时消息、好友事件）
- **其他**
  - Coil（加载本地/网络头像）
  - Baseline Profile + ProfileInstaller（启动性能优化）
  - Foreground Service + NotificationChannel（前台服务与消息通知）

依赖版本统一由 `gradle/libs.versions.toml` 管理。

---

## 核心架构设计

### 分层结构

- `ui/`：Compose 界面与导航（`RootNavGraph`、`authNavGraph`、`mainNavGraph`、`settingsNavGraph` 等）
- `ui/screen/**`：各业务页面与对应 ViewModel
- `data/repository/**`：业务仓库接口与实现（Chat、Conversation、Contact、ContactApply、Profile、Login、Register、Session）
- `data/local/**`：Room 数据库、Dao、Entity、关系对象
- `data/remote/retrofit/**`：Retrofit API 接口与请求/响应模型
- `data/remote/socket/**`：Socket 管理与 DTO、事件分发
- `notification/**`：通知构建与权限封装
- `service/ChatService`：前台服务，维持 Socket 长连接与仓库初始化
- `di/**`：Hilt Module（Repository、Network、Database 等）

### 实时消息链路

1. 用户登录成功后，`SessionRepository` 持久化 `userId`。
2. `ChatService` 作为前台服务启动，订阅 `SessionRepository.observeUserId()`：
   - 有 `userId` 时通过 `SocketManager.connect(userId)` 建立/恢复连接；
   - 登出时断开连接并停止服务。
3. `SocketManager` 建立 Socket.IO 连接，监听服务端事件：
   - `message`：新消息
   - `contact_accepted`：好友申请被接受
   - `contact_profile_updated`：好友资料变更
   并通过 `SharedFlow<SocketEvent>` 将事件广播给各仓库。
4. `ChatRepository` 订阅 `SocketManager.eventFlow`：
   - 保存消息到 Room（`MessageDao`）
   - 更新会话表（`ConversationDao`）的最后一条消息和未读数
   - 若当前不在该会话页面，则通过 `NotificationDispatcher` 发送系统通知
5. 用户点击通知后唤起 `MainActivity`，通过 `RootNavGraph` 携带 `conversationId/targetName/targetId` 导航到对应聊天页。

### 联系人与好友申请

- `LoginRepositoryImpl` / `RegisterRepositoryImpl`
  - 登录/注册成功后，持久化当前用户信息与联系人列表到本地。
- `ContactRepositoryImpl`
  - 登录后/Socket 连接成功时从服务端拉取联系人列表，并将头像保存到本地文件再入库。
  - 订阅 `SocketEvent.ContactProfileUpdated`，实时更新本地联系人名称、头像、性别。
- `ContactApplyRepositoryImpl`
  - 搜索手机号添加好友、发送申请、处理申请（同意/拒绝）。
  - 订阅 `SocketEvent.ContactAccepted`，在本地插入联系人数据与会话条目。

---

## 本地持久化与头像缓存策略

- 所有业务数据都通过 Room 持久化，UI 通过 Flow 订阅 Dao，数据变化自动触发 UI 更新。
- 头像统一做“**URL → 本地文件**”转换：
  - 从 URL 中提取文件名作为版本号，命名为 `avatar_{userId}_{fileName}`。
  - 若本地已有同版本文件则直接复用，避免重复下载。
  - 下载新版本时会删除该用户旧版本头像文件，控制存储占用。

---

## 运行与调试

### 环境要求

- Android Studio（建议使用最新稳定版）
- JDK 11（项目 `compileOptions` 已配置 Java 11）
- Android SDK 24 及以上

### 运行步骤

1. 克隆本仓库到本地：

   ```bash
   git clone <your-repo-url>
   ```

2. 使用 Android Studio 打开项目根目录 `ChatApplication`。
3. 同步 Gradle，并等待依赖下载完成。
4. 选择运行配置：
   - `appDebug`：日常开发调试
   - `appReleaseDebug`：接近 release 配置的可调试版本（已开启混淆/资源压缩/签名）
5. 连接真机或启动模拟器，点击 Run 运行。

> 注意：项目使用了自定义的 release 签名配置，生产环境打包前请根据实际情况更换为自己的签名文件与密码。

---

## 性能与发布相关

- **Baseline Profile**：启用了 Baseline Profile 相关模块，用于优化冷启动与关键路径性能。
- **ProGuard/R8**：在 `release` 构建中开启了代码混淆与资源压缩。
- **releaseDebug 构建类型**：
  - 继承 `release` 的全部优化配置；
  - 但保持 `debuggable = true` 方便对接近线上环境进行调试和网络排查。

---

## 后续可优化方向

- 引入单元测试与 UI 测试，覆盖核心仓库与关键页面。
~- 完善错误状态展示与重试机制（网络异常、Socket 断线等）。~ (todo)
- 增加消息类型（图片、语音、文件）、多端登录状态同步等能力。

---

## 简短英文概述

ChatApplication is an Android IM app built with **Jetpack Compose**, **Hilt**, **Room**, **DataStore**, **Retrofit/OkHttp**, and **Socket.IO**.  
It supports user registration & login, contacts and friend requests, conversations, real-time messaging with a foreground service, local persistence, and notification deep-linking into specific chats.
