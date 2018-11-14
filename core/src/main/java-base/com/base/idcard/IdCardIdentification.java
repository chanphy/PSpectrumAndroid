package com.base.idcard;

import com.youtu.Youtu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Zhu TingYu on 2017/11/30.
 */

public class IdCardIdentification {
    public static final int  TYPE_POSITIVE = 0;
    public static final int  TYPE_NOT_POSITIVE = 1;

    Youtu faceYoutu;

    public static final String APP_ID = "10144450";
    public static final String SECRET_ID = "AKIDSrArW3m3fClzADnSNDSddlrskSzUoOab";
    public static final String SECRET_KEY = "399y46zkKkz64rS3lMq9HE0ayCGA0jHd";
    public static final String USER_ID = "2851551317";

    public IdCardIdentification(){
        faceYoutu = new Youtu(APP_ID,SECRET_ID,SECRET_KEY,Youtu.API_YOUTU_END_POINT);
    }

    public void  IdCardOcr(final String path, final int type, Consumer<JSONObject> consumer){
        Observable.<JSONObject>create(observableEmitter -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = faceYoutu.IdCardOcr(path,type);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            observableEmitter.onNext(jsonObject);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);

    }
}
