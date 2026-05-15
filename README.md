# ai-chat-app

本仓库现已初始化为一个 **Android 本地版 AI 聊天助手**（无后端、无 VIP）。

## 功能（MVP）
- 从相册选择聊天截图
- 直接调用多模态 OpenAI 兼容接口分析截图
- 根据风格和目标生成多条回复建议
- 一键复制建议文案
- 本地保存历史记录（Room）
- 本地保存 API 配置（EncryptedSharedPreferences）

## 技术栈
- Kotlin + Jetpack Compose
- MVVM + Repository
- Retrofit + OkHttp + Kotlinx Serialization
- Room
- EncryptedSharedPreferences (AndroidX Security)

## 使用步骤
1. 使用 Android Studio 打开项目。
2. 同步 Gradle。
3. 运行 App。
4. 前往「设置」页填写：
   - Base URL（OpenAI 兼容地址，如 `https://api.openai.com`）
   - API Key
   - Vision Model（如 `gpt-4o-mini`）
5. 回到首页选择截图，填写目标和风格，点击生成。

## 注意
- 本 Demo 不会自动发送消息，只提供复制。
- 用户需自行确保调用的模型支持图片输入与 JSON 输出。

## 使用 GitHub Actions 构建并上传 APK

仓库内已包含工作流：`.github/workflows/build-apk.yml`。

### 1) GitHub 构建环境说明
- 运行环境：`ubuntu-latest`
- Java：Temurin JDK 17（Android Gradle 常用版本）
- Gradle：8.7（通过 `gradle/actions/setup-gradle` 安装与缓存）
- 构建任务：
  - `gradle :app:assembleDebug`
  - `gradle :app:assembleRelease`

### 2) 触发方式
- **手动触发**：Actions 页面选择 `Build Android APK`，点击 `Run workflow`
- **推送 main 分支**：自动构建
- **提交 PR 到 main**：自动构建（用于校验）
- **推送 tag（`v*`）**：构建后会自动创建 GitHub Release 并上传 APK

示例 tag：`v1.0.0`

### 3) APK 上传位置
- 普通构建（手动 / push / PR）：
  - 在该次工作流运行页的 **Artifacts** 下载：`apk-<run_number>`
- tag 构建（如 `v1.0.0`）：
  - 除 Artifacts 外，还会在仓库 **Releases** 页面创建对应发布并附带 APK 文件

### 4) 推荐发布流程
1. 本地确认代码后提交并推送到 `main`。
2. 打版本 tag：
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
3. 等待 Actions 完成后，到 `Releases` 下载 APK。

### 5) 可选：正式签名（当前未启用）
当前工作流可直接产出 release APK，但默认通常是调试或未注入你个人 keystore 的签名配置。
如需“可对外分发”的正式包，建议后续增加：
- GitHub Secrets：`SIGNING_KEYSTORE_BASE64`、`SIGNING_STORE_PASSWORD`、`SIGNING_KEY_ALIAS`、`SIGNING_KEY_PASSWORD`
- 工作流中解码 keystore 并在 Gradle 中读取环境变量进行 release signing。
