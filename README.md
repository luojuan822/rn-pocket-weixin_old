rn-pocket-weixin

注意事项：
ios回调通知是通过openURL方法
android回调是通过新添加一个exported属性为true的Activity来实现的。

1.android如果没法启动微信，可能是微信公众平台app设置的android相关信息不对，例如签名
2.android需要创建一个android:exported="true"的Activity来接收微信的回调，该Activity的名字为.wxapi.WXEntryActivity


详见http://www.jianshu.com/p/ce5439dd1f52
