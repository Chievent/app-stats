# app-stats

这个库简单的封装了一下获取应用存储占用情况的类，实现机制与系统设置中查看应用存储情况一致。

## 通过gradle引入方法

1. 在project目录下的build.gradle中加入如下代码：

    ```
    allprojects {
        repositories {
            jcenter()
            maven { url 'https://jitpack.io' }
        }
    }
    ```
    
2. 在module目录下的build.gradle文件中加入如下代码：

    ```
    dependencies {
        compile 'com.github.chievent:app-stats:1.0'
    }
    ```

