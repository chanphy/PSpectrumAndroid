package com.cpigeon.book.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.base.adpter.BaseQuickAdapter;
import com.base.util.IntentBuilder;
import com.base.util.Lists;
import com.base.util.db.AppDatabase;
import com.base.util.db.DbEntity;
import com.base.util.utility.KeyboardUtils;
import com.base.widget.recyclerview.XRecyclerView;
import com.cpigeon.book.R;
import com.cpigeon.book.model.UserModel;
import com.cpigeon.book.model.entity.SearchHistoryEntity;
import com.cpigeon.book.widget.SearchTextView;

import java.util.List;

/**
 * Created by Zhu TingYu on 2018/8/6.
 */

public abstract class BaseSearchActivity extends BaseBookActivity {

    public static final int CODE_SEARCH = 0x987;

    protected XRecyclerView mRecyclerView;
    protected RecyclerView mRecyclerViewHistory;
    protected SearchTextView mSearchTextView;
    protected RelativeLayout mRlHistory;
    protected TextView mTvCleanHistory;
    protected BaseQuickAdapter mAdapter;

    protected SearchHistoryAdapter mSearchHistoryAdapter;

    protected List<DbEntity> history;

    public static <A extends BaseSearchActivity> void start(Activity activity, Class<A> aClass, Bundle mBundle) {
        Intent intent = new Intent();
        intent.setClass(activity, aClass);
        if(mBundle != null){
            intent.putExtras(mBundle);
        }
        activity.startActivityForResult(intent, CODE_SEARCH);
        activity.overridePendingTransition(R.anim.bottom_out, R.anim.anim_no);
    }

    public static <A extends BaseSearchActivity> void start(Activity activity, Class<A> aClass,int requestCode, Bundle mBundle) {
        Intent intent = new Intent();
        intent.setClass(activity, aClass);
        if(mBundle != null){
            intent.putExtras(mBundle);
        }
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.bottom_out, R.anim.anim_no);
    }

    public static <A extends BaseSearchActivity> void start(Activity activity, Class<A> aClass) {
        start(activity, aClass, null);
    }


    @Override
    protected int getContentView() {
        return R.layout.fragment_base_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        history = getHistory();

        mRecyclerView = findViewById(R.id.list);
        mSearchTextView = findViewById(R.id.key);
        mRlHistory = findViewById(R.id.rlHistory);
        mTvCleanHistory = findViewById(R.id.tvCleanHistory);
        mRecyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        mSearchHistoryAdapter = new SearchHistoryAdapter();
        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        mRecyclerViewHistory.setAdapter(mSearchHistoryAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        mRecyclerView.addItemDecorationLine();
        mAdapter = getResultAdapter();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mRlHistory.setVisibility(View.GONE);
                KeyboardUtils.hideSoftInput(getBaseActivity());
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        if (!Lists.isEmpty(history)) {
            mRlHistory.setVisibility(View.VISIBLE);
            mSearchHistoryAdapter.setNewData(AppDatabase.getDates(history, SearchHistoryEntity.class));
        } else {
            mRlHistory.setVisibility(View.GONE);
        }

        mTvCleanHistory.setOnClickListener(v -> {
            AppDatabase.getInstance(getBaseActivity()).deleteAll(history);
            mSearchHistoryAdapter.getData().clear();
            mRlHistory.setVisibility(View.GONE);
        });

        mSearchHistoryAdapter.setOnDeleteClickListener(p -> {
            AppDatabase.getInstance(getBaseActivity()).DbEntityDao().delete(history.get(p));
            if (mSearchHistoryAdapter.getData().isEmpty()) {
                mRlHistory.setVisibility(View.GONE);
            }
        });

        KeyboardUtils.showSoftInput(mSearchTextView.getEdSearch());

    }

    protected abstract List<DbEntity> getHistory();

    protected abstract BaseQuickAdapter getResultAdapter();

    @Override
    public void finish() {
        super.finish();
        KeyboardUtils.hideSoftInput(mSearchTextView.getEdSearch());
        overridePendingTransition(R.anim.anim_no, R.anim.bottom_in);
    }

    protected void saveHistory(String key, String type) {
        SearchHistoryEntity historyEntity = new SearchHistoryEntity();
        historyEntity.searchTitle = key;
        AppDatabase.getInstance(getBaseActivity()).saveData(historyEntity
                , type, UserModel.getInstance().getUserId());
    }

    public void setSearchHint(@StringRes int resId) {
        mSearchTextView.setHint(resId);
    }

    public void goneHistroy() {
        mRlHistory.setVisibility(View.GONE);
    }


}

