# 一刻相册照片下载器

当前官方提供的页面选中下载只支持批量100张，如果照片很多的话下载很费劲，所以有了这个小工具。

## 下载流程

1. 登陆网页版的[一刻相册](https://photo.baidu.com/photo/web/home)。
2. 拿到Cookie和bdstoken，下载的时候需要这两个参数。![img.png](img.png)
3. 复制`.env.template`文件为`.env`并配置以下参数：
   - `COOKIE`: 登录后的Cookie
   - `BDSTOKEN`: 登录后的bdstoken
   - `TARGET_ROOT_PATH`: 目标下载路径（默认为"./pictures"）
   - `THREAD_COUNT`: 并发下载线程数（默认为200）
4. 运行Client的main方法就可以了，日志会输出当前运行到几个文件了。
5. 在目标路径会创建两个文件，__doneFsids.txt里面记录的是下载成功的fsid，__errorFsids.txt里面记录的是失败的fsid。

## 配置示例

```env
# .env文件示例
COOKIE=your_cookie_here
BDSTOKEN=your_bdstoken_here
TARGET_ROOT_PATH=./pictures
THREAD_COUNT=200
```

## 代码示例

```java
public class Client {

    public static void main(String[] args) throws IOException {
        // 从.env文件加载环境变量
        Dotenv dotenv = Dotenv.configure().load();
        
        // Cookie
        String cookie = dotenv.get("COOKIE");
        // bdstoken
        String bdstoken = dotenv.get("BDSTOKEN");
        // 目标路径
        String targetRootPath = dotenv.get("TARGET_ROOT_PATH", "./pictures");
        // 并发下载线程数
        int threadCount = Integer.parseInt(dotenv.get("THREAD_COUNT", "200"));

        log.info("开始下载, 请关注日志。");
        new CursorTraverser(
                threadCount,
                bdstoken,
                cookie,
                targetRootPath
        ).traverse();
    }
}
```

## 可调参数

1. 并发线程数，默认值可以轻松打满机器下行带宽。
2. 目标存储，目前只支持本地路径，未来支持smb协议/webdav协议。
