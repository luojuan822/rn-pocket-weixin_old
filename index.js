import {NativeModules, Platform, NativeAppEventEmitter, NativeEventEmitter} from 'react-native';
//console.log(NativeModules)
var invariant = require('invariant');
const { WeixinManager } = NativeModules;

console.log("WeixinManager===========");
console.log(WeixinManager);

var savedCallback = undefined;

if (Platform.OS === 'ios') {
    invariant(WeixinManager, 'ios failure');
    const myNativeEvt = new NativeEventEmitter(WeixinManager);  //创建自定义事件接口  
    myNativeEvt.addListener('Winxin_Resp', resp => {
        console.log('savedCallback====addListener====begin===', savedCallback);
        const callback = savedCallback;
        savedCallback = undefined;
        callback && callback(resp);
        console.log('savedCallback====addListener====end===', resp);
    });

} else if (Platform.OS === 'android') {
    invariant(WeixinManager, 'android failure');

    NativeAppEventEmitter.addListener('Winxin_Resp', resp => {
        console.log('savedCallback====addListener====begin===', savedCallback);
        const callback = savedCallback;
        savedCallback = undefined;
        callback && callback(resp);
        console.log('savedCallback====addListener====end===', resp);
    });
} else {
    invariant(WeixinManager, "Invalid platform");
}


function waitForResponse() {
    return new Promise((resolve, reject) => {
        console.log('savedCallback====waitForResponse====begin===', savedCallback);
        if (savedCallback) {
            savedCallback('User canceled.');
        }
        savedCallback = r => {
            savedCallback = undefined;
            console.log('savedCallback====waitForResponse====resolve===', r);
            resolve(r);
        };
    });
}

export async function loginByWeixin(){
    console.log("index.js ====login")
    WeixinManager.login();
    return await waitForResponse();
}

export function weixinInstalled(callback) {
    WeixinManager.installed(callback);
}