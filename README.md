# Wonderful Memories (Multi) Language

此项目是 Wonderful Memories 系列的一部分，同时也是 [SakitinSU](https://github.com/OOM-WG/SakitinSU) 项目的一部分

WMLang 是基于 kotlin 开发的支持 Jetpack Compose 与常规方式的多语言框架，其特点是基于 [FVV](https://github.com/OOM-WG/FVV) 而不是传统的 xml 存放文本，并且实现了通过 Gradle 插件自动生成 kotlin 文件，可以通过配置来让指定语言的文本以 KDoc 形式为文本注释，通过 IDE 即可快速查看文本具体内容

并且 Jetpack Compose 方式实现了使用 [Pangu Text](https://betterandroid.github.io/PanguText/zh-cn/) 来达成优化中英文字符间距，常规方案请自行使用 Pangu Text

## 使用方法

确保`repositories`内有<https://jitpack.io>

``` groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

添加 WMLang 的 Gradle 插件

``` groovy
dependencies {
    classpath 'dev.oom-wg.WMLang:dev.oom-wg.wmlang.gradle.plugin:wm-SNAPSHOT'
}
```

在项目中引入并配置内容

``` groovy
plugins {
    id 'dev.oom-wg.wmlang'
}

WMLang {
    configDir 'dir' // 存放 FVV 文本的目录路径
    baseLang 'zh' // 默认语言，例如 en 、 en_US
    base true // 常规方式
    compose true // Jetpack Compose 方式
    // 常规方式 与 Jetpack Compose 方式 必须启用其中至少一种
}
```

## 编写多语言文件

在目录下创建例如`zh.fvv`

``` fvv
Hello = "你好"
Home = {
    Welcome = "欢迎%s"
}
```

以上内容会自动生成如下 kotlin 代码 (如果启用了常规方式和 Jetpack Compose 方式)

``` kotlin
object WMLang {
    fun init() {
        WMLangZH()
    }

    internal val _Hello = WMLangBase("你好")
    /** 你好
     * @suppress compose */
    val Hello get() = "$_Hello"
    /** 你好
     * @suppress non-compose */
    @Composable
    fun Hello(vararg args: Any?) = "${PanguText.format(_Hello.get().format(*args))}"
    object Home {
        internal val _Welcome = WMLangBase("欢迎%s")
        /** 欢迎%s
         * @suppress compose */
        val Welcome get() = "$_Welcome"
        /** 欢迎%s
         * @suppress non-compose */
        @Composable
        fun Welcome(vararg args: Any?) = "${PanguText.format(_Welcome.get().format(*args))}"
    }
}
```

由于要兼顾常规方式与 Jetpack Compose 方式两个场景，WMLang以`getter`调用为常规方式，函数调用为 Jetpack Compose 方式，这可能并不是一个太好的解决方案，请在使用时留意调用的到底是哪一个

如果要调用文本，只需要调用`WMLang.Hello`(常规方式)或`WMLang.Home.Welcome()`(Jetpack Compose 方式)即可

调用 Jetpack Compose 方式时，可以直接传入内容来实现格式化文本，并且输出后的文本默认经过 Pangu Text 处理

使用 WMLang 时，会自动通过 AndroidX 的 App Startup 来调用`WMLang.init()`函数来初始化多语言数据，无需显式调用初始化

为了确保性能，WMLang 会将当前系统语言列表缓存，在使用 Jetpack Compose 方式时，缓存会自动刷新，但常规方式的缓存需要手动调用`WMLangProvider.clearCache()`

``` kotlin
class AppActivity : AppCompatActivity() {
    override fun onConfigurationChanged(newConfig: Configuration) =
        super.onConfigurationChanged(newConfig).also { WMLangProvider.clearCache() }
}
```

## 注意

开发者本身没有任何 Gradle 插件开发经验，可能有所欠缺，请见谅

以后打算要实现的功能:

- 通过`lazy`实现按需初始化文本
- 在 KDoc 里显示未翻译的语言
