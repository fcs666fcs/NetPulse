# NetPulse

NetPulse 是按照《NetPulse / 高动态网速测试软件 · 详细开发文档》实现的 Android 网速测试项目。核心路线是 Kotlin + Jetpack Compose + Material 3 + Canvas，测速链路为 Server Selection → Ping → Download → Upload → Analysis → Result。

## 已实现

- Android 8.0+，minSdk 26
- Material 3 Google 风格 Dashboard
- 大数字速度 Hero + EMA 显示平滑
- Canvas 实时曲线，固定窗口 240 点
- 可降级粒子背景：HIGH / MEDIUM / LOW
- Ping / Jitter / Loss
- 多并发 Download / Upload 测速
- 原始采样与最终聚合分离
- Room 历史记录，支持查看和删除
- DataStore 设置：测试时长、动画等级、演示模式、服务端 API URL
- Result 页面与系统分享
- 真实测速后端 Node.js + Docker
- GitHub Actions：lint、unit test、debug APK

## 架构

```text
Compose UI
   ↓
AppViewModel / StateFlow
   ↓
SpeedTestEngine
   ├─ ServerRepository
   ├─ PingTester
   ├─ DownloadTester
   ├─ UploadTester
   └─ ResultAggregator
   ↓
OkHttp / Retrofit

History: ViewModel → HistoryRepository → Room
Settings: ViewModel → SettingsRepository → DataStore
```

## 本地打开

建议使用 Android Studio 当前稳定版导入项目。工程使用 Java 17；Android Gradle Plugin 9.4.0 对应 Gradle 9.6.0。

需要 Android SDK 37 以及 Build Tools 36.x。开发阶段可将 Android Studio 的 Gradle JDK 设为 17。

因为当前生成环境没有安装 Android SDK/Gradle，本目录没有伪造一个无法运行的本地构建结果。导入 Android Studio 后同步 Gradle 即可。

## 开发模式

首次运行默认是 **Demo Mode**。它不访问任何真实测速服务，而是在本地生成连续的 Download/Upload/Ping 样本，用来先验证 Compose、Canvas、状态机和动画。

进入 Settings：

1. 关闭 Demo Mode。
2. 把 `Server API base URL` 设置为你的控制服务地址，例如 `https://speed.example.com/`。
3. 该地址需要提供 `/api/v1/servers`。
4. 返回 Home 后启动测试。

## 服务端

进入 `server/`：

```bash
npm install
PUBLIC_BASE_URL=https://speed.example.com npm start
```

Docker：

```bash
docker build -t netpulse-server ./server
docker run --rm -p 8080:8080 \
  -e PUBLIC_BASE_URL=http://localhost:8080 \
  netpulse-server
```

本地控制地址为 `http://10.0.2.2:8080/`（Android 模拟器）或你电脑所在局域网 IP（真机）。生产环境应使用 HTTPS，并把测试节点部署在带宽足够的服务器出口上。

## API

`GET /api/v1/servers`

```json
{
  "version": 1,
  "servers": [
    {
      "id": "tokyo-01",
      "name": "Tokyo 01",
      "region": "JP",
      "baseUrl": "https://speed.example.com",
      "weight": 1
    }
  ]
}
```

节点接口：

| Endpoint | Method | 用途 |
|---|---|---|
| `/empty?seq=1` | GET | Ping / RTT |
| `/download?bytes=16777216` | GET | 不可缓存下载数据 |
| `/upload` | POST | 上传 payload |
| `/health` | GET | 健康检查 |
| `/meta` | GET | 服务端元信息 |

## CI

仓库中的 workflow：

- `.github/workflows/android-check.yml`：lint + unit test
- `.github/workflows/android-build.yml`：debug APK
- `.github/workflows/android-release.yml`：release 占位流程，接入签名 Secret 后启用

这符合文档中“本地重点做 Preview / 真机调试，完整 APK 构建交给 CI”的方向。

## 重要测量约束

1. UI 显示的平滑速度不会写回最终结果。
2. Download / Upload 最终值使用有效采样窗口的平均值，peak 作为辅助信息。
3. Jitter 使用相邻 RTT 差值的平均绝对值。
4. Loss = failed / sent × 100。
5. 网络测速结果不是线路绝对速度，会受到设备、Wi‑Fi、服务器出口、TLS、代理、中间设备等影响。
