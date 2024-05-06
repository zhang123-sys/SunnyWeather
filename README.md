# SunnyWeather
## logo
![sunny_weather_logo](./logo/sunny_weather_logo.png)
### 实现搜索全球城市数据功能
### 加入显示天气信息的功能
### 新增切换城市和手动更新天气的功能
### 修改APP的图标

## 生成 APK
### 使用命令行：
你可以打开终端（在 Android Studio 中，你可以通过 View -> Tool Windows -> Terminal 打开），然后导航到你的项目根目录并运行以下命令：
```bash
./gradlew assembleRelease
```
这将为你的 release 构建变体生成 APK。

## 检查密钥别名和密码：
如果你不确定密钥别名或密钥密码是否正确，你可以使用 `keytool` 命令来列出密钥库中的条目并验证它们。在命令行中运行类似以下命令（替换为你的密钥库路径和密码）：

```bash
keytool -list -v -keystore D:\AndroidPractice\keystore\zhangwenhao.jks
```
当你运行这个命令时，系统会提示你输入密钥库密码。输入正确的密码后，你将看到密钥库中所有的条目及其别名。确保你在 build.gradle 文件中使用的别名和密码与这里列出的一致。