# OpenAi Code Review 使用说明

## 一、快速开始

### 1. 复制工作流文件

把项目根目录下的 `code-review-template.yml` 复制到你自己的项目 `.github/workflows/` 目录下，重命名为 `code-review.yml`。

### 2. 修改两处地址

打开 `code-review.yml`，修改以下两个地方：

```yaml
# ① 把下载地址改成你的 SDK 所在仓库
- run: curl -L -o ./libs/xxx.jar https://github.com/你的用户名/openai-code-review/releases/download/v2.0/openai-code-review-sdk-2.0.0.jar

# ② 把日志仓库地址改成你自己的
env:
  GITHUB_REVIEW_LOG_URI: https://github.com/你的用户名/你的日志仓库名
```

### 3. 在 GitHub 上配置 Secrets

去你的 GitHub 仓库页面：
**Settings → Secrets and variables → Actions → New repository secret**

需要配置的 Secret 如下：

| Secret 名称 | 必需？ | 说明 |
|---|---|---|
| `CODE_TOKEN` | 必需 | GitHub Token，用于 push 评审日志到日志仓库 |
| `DEEPSEEK_API_KEY` | 必需 | DeepSeek 的 API 密钥 |
| `WEIXIN_APPID` | 可选 | 微信公众号 AppID，不配则不推送微信通知 |
| `WEIXIN_SECRET` | 可选 | 微信公众号 Secret |
| `WEIXIN_TOUSER` | 可选 | 接收通知的用户 OpenID |
| `WEIXIN_TEMPLATE_ID` | 可选 | 微信模板消息的模板 ID |

---

## 二、GitHub Token 配置

`CODE_TOKEN` 用于让你的项目能把评审日志推送到日志仓库。

### 1. 生成 Token

1. 打开 GitHub → 右上角头像 → **Settings**
2. 左侧菜单 → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
3. 点击 **Generate new token (classic)**
4. 勾选权限：
   - `repo`（全部勾上）— 访问私有仓库
5. 点击 **Generate token**
6. **复制生成的 Token**（关掉页面就看不到了）

### 2. 保存到 Secrets

1. 回到你的项目仓库 → **Settings → Secrets and variables → Actions**
2. 点击 **New repository secret**
3. Name: `CODE_TOKEN`
4. Value: 粘贴刚才复制的 Token
5. 点击 **Add secret**

---

## 三、DeepSeek API 密钥配置

### 1. 获取 API Key

1. 访问 [DeepSeek 开放平台](https://platform.deepseek.com/)
2. 注册/登录账号
3. 进入 **API Keys** 页面
4. 点击 **Create API key**，复制生成的 Key

### 2. 保存到 Secrets

同样在 GitHub Secrets 中新增 `DEEPSEEK_API_KEY`，值为刚才复制的 Key。

---

## 四、微信通知配置（可选）

如果不想要微信通知，跳过这一步即可，程序不会报错。

### 1. 前置条件

要使用微信模板消息通知，你需要有一个**微信服务号**（订阅号没有模板消息功能）。

### 2. 获取 AppID 和 Secret

1. 登录 [微信公众平台](https://mp.weixin.qq.com/)
2. 左侧菜单 → **设置与开发** → **基本配置**
3. 可以看到 **AppID** 和 **AppSecret**
4. 如果没生成过 Secret，点击 **重置** 生成一个新的

### 3. 新建模板消息

1. 左侧菜单 → **广告与服务** → **模板消息**
2. 如果是第一次使用，需要先开通模板消息功能
3. 点击 **从模板库添加**
4. 搜索并选用一个合适的模板，或者自定义模板
5. 模板内容建议如下：

```
代码评审通知

项目名称：{{repo_name.DATA}}
分支名称：{{branch_name.DATA}}
提交者：{{commit_author.DATA}}
提交信息：{{commit_message.DATA}}

点击查看详情
```

> 模板中的变量名必须与代码中定义的 `TemplateKey` 一致：
> - `repo_name` — 项目名称
> - `branch_name` — 分支名称
> - `commit_author` — 提交者
> - `commit_message` — 提交信息

### 4. 获取模板 ID

模板添加成功后，在模板消息页面可以看到该模板的 **模板ID**，复制下来。

### 5. 获取用户 OpenID

要给你的微信发通知，需要知道你的微信 OpenID：

1. 先让你的微信关注这个公众号
2. 在微信公众平台 → **用户管理** 中可以看到关注者的列表
3. 找到你的微信号，点击查看详情，复制 **OpenID**

### 6. 保存到 Secrets

在 GitHub Secrets 中新增以下 4 个值：

| Secret 名称 | 值 |
|---|---|
| `WEIXIN_APPID` | 从基本配置获取的 AppID |
| `WEIXIN_SECRET` | 从基本配置获取的 AppSecret |
| `WEIXIN_TOUSER` | 关注者的 OpenID |
| `WEIXIN_TEMPLATE_ID` | 模板消息的模板 ID |

---

## 五、日志仓库说明

评审结果会推送到一个专门的日志仓库中，文件按日期归档：

```
日志仓库/
├── 2026-05-26/
│   ├── my-project-main-author-1716800000.md
│   └── another-project-develop-user2-1716900000.md
├── 2026-05-27/
│   └── ...
```

### 创建日志仓库

1. 在 GitHub 上新建一个仓库（比如 `openai-code-review-log`）
2. **不需要初始化**（不要勾选 README、.gitignore 等）
3. 确保 `GITHUB_TOKEN` 有推送到这个仓库的权限

---

## 六、完整配置清单

```
GitHub Secrets 配置：         微信公众平台配置：
──────────────────────        ─────────────────────
CODE_TOKEN         必需     开通模板消息功能
DEEPSEEK_API_KEY   必需     新建消息模板
                                  ↓
WEIXIN_APPID       可选    拿到 template_id
WEIXIN_SECRET      可选        ↓
WEIXIN_TOUSER      可选    拿到 AppID + Secret
WEIXIN_TEMPLATE_ID 可选      ↓
                              拿到用户 OpenID
```

---

## 七、常见问题

**Q：为什么评审没跑？**
A：检查 YML 文件是否放在 `.github/workflows/` 目录下，且分支名是否匹配。

**Q：微信没收到通知？**
A：确认 4 个 `WEIXIN_*` Secrets 都已正确配置，且用户已关注公众号。

**Q：报错 "token is null"？**
A：`CODE_TOKEN` 没有配置或配置错误，重新检查 Secrets。

**Q：下载 JAR 包报 404？**
A：检查 JAR 下载链接中的仓库地址是否正确，Release 版本是否存在。
