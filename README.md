记录Xposed学习过程

1、什么是xposed
Xposed框架是一款可以在不修改APK的情况下影响程序运行(修改系统)的框架服务，基于它可以制作出许多功能强大的模块，且在功能不冲突的情况下同时运作。Xposed理论上能够hook到系统任意一个Java进程，由于是从底层hook，所以需要root权限，并且每次更新都要重新启动
Xposed官方git上面有几个开源项目，包括XposedInstaller、Xposed、XposedBridge、XposedTools，附上官方git地址：https://github.com/rovo89
没有root的手机可以使用VirtualXposed进行操作，https://vxposed.com/   https://virtualxposed.com/

2、Android逆向-脱壳，dumpDex， 一个开源的 Android 脱壳插件工具，需要在 Xposed 环境中使用，支持市面上大多数加密壳，参考：http://liteng1220.com/blog/articles/dumpdex-principle/

3、Xposed详解：https://www.infoq.cn/article/android-in-depth-xposed/

4、查找Android应用当前界面所对应的activity名称
adb shell dumpsys activity | grep "Running activities" -A 7

5、接上手机，打开 monitor ，新版的AS貌似连入口都 隐藏了，你需要到 sdk的tools目录 下找下，windows下的是 monitor.bat，接着找到对应的进程，跟踪一波方法调用。

6、使用Jadx反编译apk。

7、ListView纯粹的想用代码实现点击，listview自己有一个方法performItemClick（）；我们只需要调用这个方法就好了。
listView.performItemClick(listView.getChildAt(click_position), click_position, listView.getItemIdAtPosition(click_position));
listView.performItemClick(listView.getAdapter().getView(click_position, null, null),click_position,listView.getItemIdAtPosition(click_position));

8、dumpsys package [packagename] 命令查看debug状态。
