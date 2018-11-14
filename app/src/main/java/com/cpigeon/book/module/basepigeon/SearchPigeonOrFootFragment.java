package com.cpigeon.book.module.basepigeon;

import android.content.Intent;

import com.base.util.IntentBuilder;
import com.cpigeon.book.model.entity.PigeonEntity;
import com.cpigeon.book.module.homingpigeon.adapter.MyHomingPigeonAdapter;

/**
 * 选择鸽子 或  足环返回
 * Created by Administrator on 2018/10/20 0020.
 */

public class SearchPigeonOrFootFragment extends BaseFootListFragment {


    public  static final  int  CODE_SEARCH_PIGEON_ORFOOT = 0x000032;

    @Override
    protected void initData() {
        super.initData();
        setStartSearchActvity(SearchPigeonOrFootActivity.class);//搜索页面

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            IntentBuilder.Builder()
                    .putExtra(IntentBuilder.KEY_DATA, data.getSerializableExtra(IntentBuilder.KEY_DATA))
                    .finishForResult(getBaseActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
