package com.cpigeon.book.module.trainpigeon;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.base.base.adpter.BaseQuickAdapter;
import com.base.util.db.AppDatabase;
import com.base.util.db.DbEntity;
import com.cpigeon.book.R;
import com.cpigeon.book.base.BaseSearchActivity;
import com.cpigeon.book.model.UserModel;
import com.cpigeon.book.module.trainpigeon.adpter.NewTrainAddPigeonAdapter;
import com.cpigeon.book.widget.SearchTextView;

import java.util.List;

/**
 * Created by Zhu TingYu on 2018/9/3.
 */

public class SearchTrainPigeonActivity extends BaseSearchActivity {

    NewTrainAddPigeonAdapter mAdapter;

    @Override
    protected List<DbEntity> getHistory() {
        return AppDatabase.getInstance(getBaseActivity()).DbEntityDao()
                .getDataByUserAndType(UserModel.getInstance().getUserId(), AppDatabase.TYPE_SEARCH_TRAIN_PIGEON);
    }

    @Override
    protected BaseQuickAdapter getResultAdapter() {
        mAdapter = new NewTrainAddPigeonAdapter();
        return mAdapter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mSearchTextView.setHint(R.string.text_input_foot_number_search);
        mSearchTextView.setOnSearchTextClickListener(new SearchTextView.OnSearchTextClickListener() {
            @Override
            public void search(String key) {
            }

            @Override
            public void cancel() {
                finish();
            }
        });
    }
}
