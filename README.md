# 单刀双掷（Single-Pole Double-Throw）


![](https://i.imgur.com/RUP8U27.png)

## 背景

由于联运项目需要与多个合作方**APP**接入，具有多种产品风味 (Product Flavor)，每个**APP**相当于一个构建变体 (Varient) ，它们的代码、资源、构建方式会有差异。 **`SPDT`** 的作用就是管理不同变体之间的代码差异，使程序运行时选择不同的实现。


## 特性

- [x] 动态配置变体的名称和属性
- [x] 根据当前变体来选择接口的具体实现类
- [x] 支持注解注入
- [x] 根据当前变体动态替换资源


## 使用

### 变体定义

在项目依赖的其中 **一个** 模块中，添加变体的定义

```gradle
apply plugin: 'spdt'


spdt {
   
   ProductFlavorA {
       appid = "A_appid"
       
       resourceSuffix = "_a"
   }
   
   ProductFlavorB {
       appid = "B_appid"
       
       resourceSuffix = "_b"
   }
   
   WhateverNameYouDefined {
       appid = "what ever you want"
   }
   
   current = ProductFlavorB
}
```

在 `spdt{}` 代码块中，可以任意指定你的变体的名称，并在变体的代码块中添加变体特有的属性。`current` 属性需要指定当前变体。

在编译时，会用 `spdt{}` 的变体名称生成对应的Java类，也就是类 `ProductFlavorA` 、类 `ProductFlavorB` 和类 `WhateverNameYouDefined` ，它们都实现 `SpdtFlavor` 接口。

### 差异代码控制

管理代码差异的方法类似于Kotlin的[Expect/Actual机制](https://kotlinlang.org/docs/reference/platform-specific-declarations.html) 。接口与实现分离，不同的变体使用不同的实现类。
```kotlin
@SpdtExpect
interface Namer {
    
    fun myName(): String 
}

@SpdtActual(ProductFlavorA::class)
class ANamer: Namer {
    
    override fun myName(): String = "I am A."
}

@SpdtActual(ProductFlavorB::class)
class BNamer: Namer {
    
    override fun myName(): String = "I am Groot!!"
}
```
这里的 `ProductFlavorA::class` 和 `ProductFlavorB::class` 是由 `spdt{}` 代码块中定义的生成的类。

如果 `ANamer` 有且仅有一个父接口，那么父接口 `Namer` 可以省略 `@SpdtExpect` 注解。如果 `ANamer` 实现了多个接口，那么必须用 `@SpdtExpect` 来标记那个要有多实现的接口类。

在使用时 `Namer` 的实例时，需要把它代理给 `spdt` ：

```kotlin
object Demo {
    
    private val instance: Namer by spdtInject()
    
    fun main(vararg arg: String) {
        System.out.println(instance.myName())
    }
}
```

如果你到今时今日还不用 Kotlin，那么上面代码等价于下面的 Java：

```java
class Demo {
    
    private static Namer getInstance(){
        return Spdt.of(Namer.class);
    }
    
    public static void main(String[] args) {
        System.out.println(getInstance().myName())
    }
}
```

并不是所有的变体都需要有对应的实现类，比如接口 `Namer` 在 `WhateverNameYouDefined` 这个变体下就没有对应的实现类，所以获取到的实例是有可能为空的：

```kotlin
//if the current flavor is 'WhateverNameYouDefined',
//the field 'namer' will be null.
val namer: Namer? = Spdt.ofOrNull(Namer::class)
```

如果一个类中使用了多个由 `Spdt` 管理的接口，那么通过注入实例会更方便：

```kotlin
class MainActivity: Activity {

    @SpdtInject
    lateinit var instanceA: Namer
    
    @SpdtInject
    @JvmField
    var instanceB: Namer? = null

    fun onCreate(context: Context) {
        super.onCreate(context)
        Spdt.inject(this)
    }
}
```

注意 `@SpdtActual` 标记的实现类绝**不是**单例。
上面例子中的 `instanceA` 和 `instanceB` 虽然都是 `Namer` 类型，但它们并不会指向同一个对象。

### 资源差异化

在 gradle 文件中声明时，我们对 **ProductFlavorA** 的 `resourceSuffix` 声明为 `_a` ，对 **ProductFlavorB** 的 `resourceSuffix` 声明为 `_b` 。当资源定义上有：

```
<resources>
    <string name="i_am_string">我是一个默认字符串</string>
    <string name="i_am_string_a">我是一个ProductA的字符串</string>
    <string name="i_am_string_b">我是一个ProductB的字符串</string>
</resources>
```

然后在我们的代码中使用该资源定义：

```kotlin
// 根据当前变体不同，拿到的字符串会不同
val stringRes: String = Spdt.string(R.string.i_am_string)
```

如果当前的变体是定义为 **ProductB**

```
spdt {
   ProductFlavorB {
       appid = "B_appid"
       
       resourceSuffix = "_b"
   }
   
   current = ProductFlavorB
}
```

那么 `stringRes` 得到的字符串将会是 `'我是一个ProductB的字符串'` 。
同理，我们使用资源id来获取对应的图片或颜色资源：

```kotlin
val drawable = Spdt.drawable(R.drawable.i_am_drawable)
val color = Spdt.color(R.color.i_am_color)
```

那么 `drawable` 和 `color` 会先尝试去加载 `R.drawable.i_am_drawable_b` 和 `R.color.i_am_color_b` 这两个资源id，如果没有这样的id才会再加载 `R.drawable.i_am_drawable` 和 `R.color.i_am_color` 。

### 动态替换xml的资源

在需要替换xml资源的 `Activity` 上添加注解 `@SpdtSkin` ：

```kotlin
@SpdtSkin
class MainActivity: Activity() {
    
    override fun onCreate(saveIns: Bundle){
        setContentView(R.layout.activity_main)
    }
}
```

而在 activity_main.xml 中定义了一个 `TextView` 及其属性：

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@drawable/bg_item_selector"
    android:text="@string/main_page"
    android:textColor="@color/bg_preview_expand" />
```

这个 `TextView` 会在不同变体下表现出不同的样子：

|ProductFlavorA|ProductFlavorB|
|:---|:---|
| <TextView <br/> android:layout_width="wrap_content" <br/> android:layout_height="wrap_content" <br/> android:background="@drawable/bg_item_selector_a" <br/> android:text="@string/main_page_a" <br/> android:textColor="@color/bg_preview_expand_a" /> | <TextView <br/> android:layout_width="wrap_content" <br/> android:layout_height="wrap_content" <br/> android:background="@drawable/bg_item_selector_b" <br/> android:text="@string/main_page_b" <br/> android:textColor="@color/bg_preview_expand_b" /> |


## 安装

1. 在根项目的 `build.gradle` 中添加公司maven仓库的地址，注意 `classpath` 添加 `SPDT` 的路径，最新版本可以在 [这里](http://repo.yypm.com:8181/nexus/#nexus-search;quick~spdt) 查询。
    ```groovy
    buildscript {
        repositories {
            maven { url 'http://repo.yypm.com:8181/nexus/content/groups/public' }
        }
        dependencies {
            classpath "com.unionyy.mobile:spdt:${Version.spdt_version}"
        }
    }

    allprojects {
        repositories {
            maven { url 'http://repo.yypm.com:8181/nexus/content/groups/public' }
        }
    }
    ```
    
2. 在使用到 `SPDT` 的模块的 `build.gradle` 中应用插件
    ```groovy
    apply plugin: 'spdt'
    ```
    
3. 添加 `Proguard` 混淆规则
    ```proguard
    -keep @com.unionyy.mobile.spdt.annotation.SpdtKeep class * {*;}
    ```
