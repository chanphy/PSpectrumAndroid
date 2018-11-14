package com.cpigeon.book.module.menu.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.base.base.BaseViewModel;
import com.base.http.HttpErrorException;
import com.cpigeon.book.model.AnnouncementNoticeModel;
import com.cpigeon.book.model.entity.AnnouncementNoticeEntity;
import com.cpigeon.book.model.entity.MsgCountEntity;
import com.cpigeon.book.model.entity.NoticeMsgInfoEntity;

import java.util.List;

/**
 * 公告通知
 * Created by Administrator on 2018/8/9.
 */
public class AnnouncementNoticeViewModel extends BaseViewModel {

    public int pi = 1;
    public int ps = 50;

    public int changePosition;

    public MutableLiveData<List<AnnouncementNoticeEntity>> announcementNoticeData = new MutableLiveData<>();
    public MutableLiveData<MsgCountEntity> mMsgCountData = new MutableLiveData<>();//统计
    public MutableLiveData<Object> mDetailData = new MutableLiveData<>();
    public MutableLiveData<List<NoticeMsgInfoEntity>> mNoticeMsgInfoData = new MutableLiveData<>();

    //获取  公告通知列表
    public void getTXGP_GetGongGaoData() {
        submitRequestThrowError(AnnouncementNoticeModel.getTXGP_GetGongGao(pi, ps), r -> {
            if (r.isOk()) {
                listEmptyMessage.setValue(r.msg);
                announcementNoticeData.setValue(r.data);
            } else throw new HttpErrorException(r);
        });
    }

    //获取  公告通知统计
    public void getTXGP_GongGao_CountData() {
        submitRequestThrowError(AnnouncementNoticeModel.getTXGP_GongGao_Count(), r -> {
            if (r.isOk()) {
                mMsgCountData.setValue(r.data);
            } else throw new HttpErrorException(r);
        });
    }

    //获取  公告通知详细
    public void getTXGP_GongGao_DetailData(String detailId) {
        submitRequestThrowError(AnnouncementNoticeModel.getTXGP_GongGao_Detail(detailId), r -> {
            if (r.isOk()) {
                mDetailData.setValue(r.msg);
            } else throw new HttpErrorException(r);
        });
    }

    //获取公告通知和鸽友消息
    public void getTXGP_GetMSGInfoData() {
        submitRequestThrowError(AnnouncementNoticeModel.getTXGP_GetMSGInfo(), r -> {
            if (r.isOk()) {
                mNoticeMsgInfoData.setValue(r.data);
            } else throw new HttpErrorException(r);
        });
    }

}
