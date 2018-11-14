package com.cpigeon.book.module.menu.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.base.base.BaseViewModel;
import com.base.http.HttpErrorException;
import com.cpigeon.book.model.LogbookModel;
import com.cpigeon.book.model.entity.LogbookEntity;

import java.util.List;

/**
 * Created by Administrator on 2018/8/8.
 */

public class LogbookViewModel extends BaseViewModel {

    public int pi = 1;
    public int ps = 50;

    public MutableLiveData<List<LogbookEntity>> logbookData = new MutableLiveData<>();


    //获取  操作日志列表
    public void getZGW_Users_GetLogData() {
        submitRequestThrowError(LogbookModel.getZGW_Users_GetLog(pi, ps), r -> {
            if (r.isOk()) {
                listEmptyMessage.setValue(r.msg);
                logbookData.setValue(r.data);
            } else throw new HttpErrorException(r);
        });
    }

}
