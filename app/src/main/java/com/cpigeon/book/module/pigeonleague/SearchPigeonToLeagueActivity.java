package com.cpigeon.book.module.pigeonleague;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.base.base.adpter.BaseQuickAdapter;
import com.cpigeon.book.model.entity.PigeonEntity;
import com.cpigeon.book.module.basepigeon.BaseSearchPigeonActivity;
import com.cpigeon.book.module.pigeonleague.adpter.SelectPigeonToLeagueAdapter;

/**
 * 信鸽赛绩  搜索足环列表
 * Created by Zhu TingYu on 2018/9/14.
 */

public class SearchPigeonToLeagueActivity extends BaseSearchPigeonActivity {

    @Override
    protected BaseQuickAdapter getResultAdapter() {
        mAdapter = new SelectPigeonToLeagueAdapter();

        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            try {
                PigeonEntity mBreedPigeonEntity = mAdapter.getData().get(position);
                PigeonMatchDetailsActivity.start(getBaseActivity(), mBreedPigeonEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return mAdapter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setListPadding(0, 0, 0, 0);
    }

    @Override
    protected void initData() {
        super.initData();
        SEARCH_HISTORY_KEY = "search_history_pigeon_to_league";
    }

}
