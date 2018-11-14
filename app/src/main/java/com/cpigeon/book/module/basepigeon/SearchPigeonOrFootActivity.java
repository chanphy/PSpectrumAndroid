package com.cpigeon.book.module.basepigeon;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.base.base.adpter.BaseQuickAdapter;
import com.base.util.IntentBuilder;
import com.cpigeon.book.model.entity.PigeonEntity;
import com.cpigeon.book.module.homingpigeon.adapter.MyHomingPigeonAdapter;

/**
 * Created by Administrator on 2018/10/20 0020.
 */

public class SearchPigeonOrFootActivity extends BaseSearchPigeonActivity {

    @Override
    protected BaseQuickAdapter getResultAdapter() {

        mAdapter = new MyHomingPigeonAdapter();
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            try {
                PigeonEntity mPigeonEntity = mAdapter.getData().get(position);
                IntentBuilder.Builder()
                        .putExtra(IntentBuilder.KEY_DATA, mPigeonEntity)
                        .finishForResult(getBaseActivity());
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
        SEARCH_HISTORY_KEY = "search_history_pigeon_or_foot";
    }

}
