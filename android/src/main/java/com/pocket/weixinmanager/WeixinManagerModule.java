package com.pocket.weixinmanager;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeixinManagerModule extends ReactContextBaseJavaModule implements IWXAPIEventHandler {

    private static IWXAPI api;
    private static final String appId = "wxe3b8085a714877e9";
    private static WeixinManagerModule module = null;

    public WeixinManagerModule(ReactApplicationContext context) {
        super(context);
        api = WXAPIFactory.createWXAPI(context, appId, true);
        api.registerApp(appId);
    }

    @Override
    public void initialize() {
        super.initialize();
        module = this;
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
    }

    public String getName() {
        return "WeixinManager";
    }

    @ReactMethod
    public void login() {
        System.out.println("Weixin login java ======= " + (api.isWXAppInstalled()) + (api.isWXAppSupportAPI()));
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "69room";
        api.sendReq(req);
    }

    public static void handleIntent(Intent intent) {
        System.out.println("handleIntent=====" + module);
        api.handleIntent(intent, module);
    }

    @ReactMethod
    public void installed(Callback callback) {
        callback.invoke(api.isWXAppInstalled());
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq req) {
        System.out.println("weixin callback === onReq" + req.toString());
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        System.out.println("weixin callback === onResp" + resp.toString());
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                System.out.println("ERR_OK");
                //发送成功
                SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                if (sendResp != null) {
                    this.handleResultData(sendResp.code, sendResp.state);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                System.out.println("ERR_USER_CANCEL");
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                System.out.println("ERR_AUTH_DENIED");
                //发送被拒绝
                break;
            default:
                //发送返回
                break;
        }

    }

    private void handleResultData(String code, String state) {
        RCTNativeAppEventEmitter emitter = getReactApplicationContext().getJSModule(RCTNativeAppEventEmitter.class);
        WritableMap map = Arguments.createMap();
        map.putString("code", code);
        map.putString("state", state);
        emitter.emit("Winxin_Resp", map);
    }
}
