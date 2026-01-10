# [PureJoy - FYTxt](https://app.niggergo.work/docs/purejoy/fytxt)

> [!TIP]
> 此项目隶属于[欢律遗愉系列](https://oom-wg.dev/projects)，是 [SSU](https://ssu.oom-wg.dev) 的衍生项目

FYTxt 是支持 **Kotlin MultiPlatform** 以及 **Compose MultiPlatform** 的多语言框架

完全做到了 _方便易用_、_高性能_、_全平台支持_，可以监听系统语言变化，还支持锁定语言列表以便于自定义语言列表，
并且有翻译率统计与语言组设定

FYTxt 基于 [`FVV`](https://app.niggergo.work/docs/fw/) 而不是传统的 xml 存放文本，
原理是通过 Gradle 插件自动生成 Kotlin 文件，
可以通过配置来让指定语言的文本以 `KDoc` 形式为文本注释，通过 IDE 即可快速查看文本具体内容

并且 Android 平台的 **Compose** 方式实现了默认使用
[**Pangu Text**](https://betterandroid.github.io/PanguText/zh-cn/) 来达成优化中英文字符间距
