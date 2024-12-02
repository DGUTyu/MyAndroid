package cn.example.designpattern.mvp.http;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.example.base.utils.StringUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public abstract class ResponseObserver<T> implements Observer<Response<JsonObject>> {
    private Type classType;
    private Disposable disposable;

    public ResponseObserver() {
        getClassType();
    }

    private void getClassType() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            classType = ((ParameterizedType) type).getActualTypeArguments()[0];
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        disposable = d;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        onFail(e.getMessage());
        dispose();
    }


    @Override
    public void onNext(Response<JsonObject> response) {
        try {
            if (response.isSuccessful() && response.body() != null) {
                getCookie(response);
                JsonObject responseBody = response.body();
                doNext(responseBody);
            } else {
                onFail("RESULT_READING_ERROR");
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }


    private void doNext(JsonObject gsonResult) {
        T t = parseData(gsonResult);
        onSuccess(t);
    }


    private void doNext2(JsonObject gsonResult) {
        String resultStr = gsonResult.toString();
        JsonObject jsonObject = parseJsonString(resultStr);

        try {
            if (jsonObject == null) {
                onFail("Invalid JSON response");
                return;
            }

            T t = null;
            if (!resultStr.contains("result")) {
                //验签解密结果
                JsonObject js = responseData(resultStr, secretKey);
                if (js != null) {
                    jsonObject = js;
                } else {
                    onFail("Invalid JSON RESULT_INTERNAL_ERROR");
                    return;
                }
            }
            String result = jsonObject.get("result").getAsString();
            Log.e("mPaaS", "****\n url:" + url + "\n新响应：" + jsonObject.toString());
            if ("200".equals(result)) {
                if (jsonObject.get("message").isJsonArray()) {
                    JsonArray dataArray = jsonObject.get("message").getAsJsonArray();
                    t = parseData(dataArray);
                } else if (jsonObject.get("message").isJsonObject()) {
                    JsonObject data = jsonObject.get("message").getAsJsonObject();
                    t = parseData(data);
                } else {
                    String dataStr = jsonObject.get("message").getAsString();
                    t = parseData(dataStr);
                }
                onSuccess(t);
            } else {
                handleErrorCode(jsonObject, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private T parseData(Object data) {
        if (classType == String.class) {
            return (T) data.toString();
        }
        return new Gson().fromJson(data.toString(), classType);
    }

    private void handleErrorCode(JsonObject jsonObject, String result) {
        String code = jsonObject.get("result").getAsString();
        switch (code) {
            case "401":
                onFail("RESULT_NEED_LOGIN");
                break;
            case "406":
                onFail("RESULT_TOKEN_OVERDUE");
                break;
            default:
                onFail("RESULT_INTERNAL_ERROR");
                break;
        }
    }


    @Override
    public void onComplete() {
        dispose();
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private static void getCookie(Response response) {
        try {
            String cookieString = response.headers().get("Set-Cookie");
            if (!StringUtil.isEmptyOrNull(cookieString)) {
                String arrCookies[] = cookieString.split("\\;");
                String vc_cookie = arrCookies[0];
                //MainApplication.setCookie(vc_cookie.split("\\=")[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static JsonObject responseData(String resultStr, String shareKey) {
        try {
            JsonObject json = parseJsonString(resultStr);
            String retData = json.get("retData").getAsString();
            String retSign = json.get("retSign").getAsString();
            //sm3HexCheckSign = SpaySMUtil.SM3HexCheckSign(retData, retSign, shareKey);
            boolean sm3HexCheckSign = true;

            if (sm3HexCheckSign) {
                //验签通过
                if (!StringUtil.isEmptyOrNull(retData) && retData.contains("result")) {
                    JsonObject data = parseJsonString(retData);
                    return data;
                } else {
                    //还原 String data = SpaySMUtil.SM4DecryptNative(retData, shareKey);
                    String data = resultStr;
                    if (!StringUtil.isEmptyOrNull(data)) {
                        JsonObject js = parseJsonString(data);
                        return js;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JsonObject parseJsonString(String jsonString) {
        try {
            JsonParser jsonParser = new JsonParser();
            return jsonParser.parse(jsonString).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract void onSuccess(T data);

    public abstract void onFail(String errorMessage);

    private String url;
    private String secretKey;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
