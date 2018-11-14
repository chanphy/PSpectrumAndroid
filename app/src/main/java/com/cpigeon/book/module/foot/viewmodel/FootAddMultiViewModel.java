package com.cpigeon.book.module.foot.viewmodel;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;

import com.base.base.BaseViewModel;
import com.base.http.HttpErrorException;
import com.base.util.IntentBuilder;
import com.base.util.Lists;
import com.base.util.Utils;
import com.base.util.utility.StringUtil;
import com.cpigeon.book.R;
import com.cpigeon.book.model.FootAdminModel;
import com.cpigeon.book.model.entity.FootEntity;
import com.cpigeon.book.model.entity.SelectTypeEntity;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by Zhu TingYu on 2018/8/21.
 */

public class FootAddMultiViewModel extends BaseViewModel {

    private String footId;
    private String startFoot;
    public String count = "0";
    public String typeId;
    private String source;
    public String cityCode = "2";
    private String money;
    private String remark;

    public String sFootId;
    public String eFootId;
    public String sFootNumber;
    public String eFootNumber;

    public List<SelectTypeEntity> mSelectTypes_Foot_Ring;
    public List<SelectTypeEntity> mSelectTypes_Srouse;

    public MutableLiveData<String> addR = new MutableLiveData<>();
    public MutableLiveData<String> deleteR = new MutableLiveData<>();
    public MutableLiveData<FootEntity> mFootEntityLiveData = new MutableLiveData<>();

    public FootAddMultiViewModel(Activity activity) {
        footId = activity.getIntent().getStringExtra(IntentBuilder.KEY_DATA);
    }

    public void getFootInfo() {
        submitRequestThrowError(FootAdminModel.getTXGP_FootRing_Select(footId), r -> {
            if (r.isOk()) {
                mFootEntityLiveData.setValue(r.data);
                sFootId = String.valueOf(r.data.getFootRingID());
                eFootId = r.data.getEndFootRingID();
                sFootNumber = r.data.getFootRingNum();
                eFootNumber = r.data.getEndFootRingNum();
            } else throw new HttpErrorException(r);
        });
    }

    public void addMultiFoot() {
        submitRequestThrowError(FootAdminModel.addMultiFoot(startFoot, count
                , typeId, source, cityCode, money, remark), r -> {
            if (r.isOk()) {
                addR.setValue(r.msg);
            } else throw new HttpErrorException(r);
        });
    }

    public void deleteMultiFoot() {
        submitRequestThrowError(FootAdminModel.deleteFoots(sFootId, eFootId, sFootNumber, eFootNumber), r -> {
            if (r.isOk()) {
                deleteR.setValue(r.msg);
            } else throw new HttpErrorException(r);
        });
    }

    public Consumer<String> setStartFoot() {
        return s -> {
            startFoot = s;
            isCanCommit();
        };
    }

    public void setCount(String count) {
        this.count = count;
        isCanCommit();
    }

    public Consumer<String> setSource() {
        return s -> {
            source = s;
            isCanCommit();
        };
    }


    public Consumer<String> setMoney() {
        return s -> {
            money = s;
            isCanCommit();
        };
    }

    public Consumer<String> setRemark() {
        return s -> {
            remark = s;
            isCanCommit();
        };
    }

    public void isCanCommit() {
        isCanCommit(startFoot, count, startFoot, money);
    }

    public List<String> getFoots() {
        List<String> foot = Lists.newArrayList();
        if (StringUtil.isStringValid(startFoot)) {
            String[] foots = startFoot.split(Utils.getString(R.string.text_foots_divide));
            foot = Lists.newArrayList(foots);
        }
        return foot;
    }
}
