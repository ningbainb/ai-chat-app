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

## GitHub 云端打包 APK（推荐）

仓库已提供 GitHub Actions 工作流：`.github/workflows/android-build.yml`。

### 使用方式
1. 把代码推送到 GitHub 仓库。
2. 打开仓库 **Actions** 页面。
3. 选择 **Android CI Build** 工作流并点击 **Run workflow**。
4. 默认会在该次运行的 **Artifacts** 里生成 `app-debug-apk` 可下载（现在会自动匹配 `app/build/outputs/apk/**/*.apk`，避免固定文件名导致上传失败）。
5. 如果希望在仓库首页也直接看到 APK，请在手动运行时把 `publish_release` 选成 `true`，工作流会自动创建 GitHub Release 并上传 APK。

### 产物位置
- Debug APK：`app/build/outputs/apk/debug/app-debug.apk`

### 说明
- 工作流固定使用 **JDK 17** 与 **Gradle 8.7**，避免本地 JDK 版本不一致导致的构建失败。
- 这是 Debug 包，适合测试与演示。
- `publish_release=true` 时，APK 会出现在仓库 **Releases** 页面，便于你“直接看见并下载”。



### 如果 Actions 页面“看不到工作流/看不到运行记录”
请按下面逐条检查：
1. 你是否已经把包含工作流文件的提交 push 到 GitHub（任意分支都可以）。
2. 仓库是否启用了 Actions：`Settings -> Actions -> General -> Allow all actions`。
3. 在 Actions 页左侧点击 **Android CI Build**，再点 **Run workflow** 手动触发。
4. 如果你是 fork 仓库，首次运行可能需要在 Actions 页点击启用。
5. 触发后进入单次运行详情页，在底部 **Artifacts** 下载 `app-debug-apk`。

> 说明：当前工作流会在任意分支的 push、PR，以及手动 `workflow_dispatch` 下触发。


### 如果看到 “BUILD FAILED / exit code 1” 是什么意思
这表示云端编译失败了（不是成功产出 APK），常见原因有：
- 依赖下载失败（网络或仓库访问问题）
- Kotlin/Gradle 编译错误
- Android SDK 组件缺失

现在工作流会额外上传以下排障文件：
- `build-debug-log`（完整构建日志）
- `gradle-reports`（Gradle 报告目录）

请在失败的那次 Actions 运行页下载这两个 Artifact，把最后 80~120 行日志发我，我可以直接定位到具体报错行。


### 如果 `Publish APK to GitHub Release` 显示空白
常见原因是这次运行没有可上传的 APK 文件，或你没有在手动运行时把 `publish_release` 设为 `true`。

排查顺序：
1. 先看同一次运行里 `Upload APK artifact(s)` 是否成功。
2. 到 Artifacts 下载 `app-debug-apk`，确认 APK 确实已生成。
3. 手动运行工作流时，确认 `publish_release=true`。
4. 再看 `Publish APK to GitHub Release` 步骤日志，确认发布条件与上传路径。
5. 如果你点了 **Re-run jobs**，现在也不会因为 tag 重名导致发布失败（tag 已改为 `run_id + run_attempt` 唯一值）。

> 现在工作流使用通配符路径上传 APK，并通过条件判断仅在手动触发且 `publish_release=true` 时发布 Release。


### 如果工作流“0 秒直接失败”
这通常是工作流表达式/触发器配置问题（例如触发上下文里引用了不存在的字段）。

本仓库已修复为：
- `push` / `pull_request` 使用显式空对象写法（`push: {}`、`pull_request: {}`）
- Release 条件改为 `github.event.inputs.publish_release == "true"`，避免在非手动触发时读取不到 `inputs` 导致异常

如果你还遇到 0 秒失败，请把那次 run 的最上方红色报错原文贴我（通常会写明 YAML 哪一行有问题）。
