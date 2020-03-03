# FUKSYStreamerDemoDroid 快速接入文档

FUKSYStreamerDemoDroid 是集成了 Faceunity 美颜和贴纸道具功能和 金山云直播推流 的 Demo。

本文是 FaceUnity SDK 对接 金山云直播推流 的技术说明，关于 `FaceUnity SDK` 的详细说明，请参看 **[FULiveDemoDroid](https://github.com/Faceunity/FULiveDemoDroid/)**。

### 一、导入 SDK

将 faceunity  文件夹添加到工程中，下面是一些文件说明。

- jniLibs 文件夹下 libnama.so 和 libfuai.so 是人脸跟踪及道具绘制核心静态库
- libs 文件夹下 nama.jar 是供应用层调用的 JNI 接口
- assets 文件夹下 AI_model/ai_face_processor.bundle 人脸识别数据包（自 6.6.0 版本起，v3.bundle 不再使用）
- assets 文件夹下 face_beautification.bundle 美颜功能数据包
- assets 文件夹下 normal 中的 \*.bundle 文件是特效贴纸文件，自定义特效贴纸制作的文档和工具，请联系技术支持获取。

### 二、全局配置

在 `FURenderer` 类 的  `initFURenderer` 静态方法是对 Faceunity SDK 一些全局数据初始化的封装，可以在 Application 中调用，也可以在工作线程调用，仅需初始化一次即可。

### 三、使用 SDK

#### 1. 初始化

在 `FURenderer` 类 的  `onSurfaceCreated` 方法是对 Faceunity SDK 每次使用前数据初始化的封装。

#### 2. 图像处理

在 `FURenderer` 类 的  `onDrawFrame` 方法是对 Faceunity SDK 图像处理方法的封装，该方法有许多重载方法适用于不同的数据类型需求。

在 demo 中，`FaceunityFilter` 类是实现金山 SDK 自定义滤镜接口的类，可以作为参考。

#### 3. 销毁

在 `FURenderer` 类 的  `onSurfaceDestroyed` 方法是对 Faceunity SDK 数据销毁的封装。

#### 4. 切换相机

调用 `FURenderer` 类 的  `onCameraChange` 方法，用于重新为 SDK 设置参数。

### 四、切换贴纸道具及调整美颜参数

`FURenderer` 类实现了 `OnFaceUnityControlListener` 接口，而 `OnFaceUnityControlListener` 接口是对切换贴纸道具及调整美颜参数等一系列操作的封装。在 demo 中，`BeautyControlView` 用于实现用户交互，调用了 `OnFaceUnityControlListener` 的方法实现功能。

**PS:** 本 Demo 只是简单集成了 FaceUnity SDK。关于金山云直播推流 SDK 的使用，请参考相关的文档。

**至此快速集成完毕，关于 FaceUnity SDK 的更多详细说明，请参看 [FULiveDemoDroid](https://github.com/Faceunity/FULiveDemoDroid/)**