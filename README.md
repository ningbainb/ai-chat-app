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
