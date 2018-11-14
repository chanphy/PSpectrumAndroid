package com.cpigeon.book.module.foot.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.base.base.BaseViewModel;
import com.base.http.HttpErrorException;
import com.cpigeon.book.model.FootAdminModel;
import com.cpigeon.book.model.entity.FootEntity;
import com.cpigeon.book.model.entity.FootRingStatEntity;

import java.util.List;

/**
 * Created by Zhu TingYu on 2018/8/19.
 */

public class FootAdminListViewModel extends BaseViewModel {
    public int pi = 1;
    public int ps = 20;
    public String year = "";
    public String typeid = "";
    public String stateid = "";
    public String key = "";

    public MutableLiveData<List<FootEntity>> footAdminListData = new MutableLiveData<>();
    public MutableLiveData<FootRingStatEntity> mDataFootStat = new MutableLiveData<>();

    //足环号码 列表
    public void getFoodList() {
        submitRequestThrowError(FootAdminModel.getTXGP_FootRing_SelectKeyAll(pi, ps, year, typeid, stateid, key), r -> {
            if (r.isOk()) {
                listEmptyMessage.setValue(r.msg);
                footAdminListData.setValue(r.data);
            } else throw new HttpErrorException(r);
        });
    }

    public void getFootRingStat() {
        submitRequestThrowError(FootAdminModel.getFootRingStat(), r -> {
            if(r.isOk()){
                mDataFootStat.setValue(r.data);
            }else throw new HttpErrorException(r);
        });
    }

    public void resetData() {
        pi = 1;
        ps = 20;
        year = "";
        typeid = "";
        stateid = "";
        key = "";
    }
}
