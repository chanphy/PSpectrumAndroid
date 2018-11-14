package com.cpigeon.book.module.select;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.BaseFragment;
import com.base.base.pinyin.LetterSortModel;
import com.base.util.IntentBuilder;
import com.base.widget.recyclerview.XRecyclerView;
import com.cpigeon.book.R;
import com.cpigeon.book.base.BaseSearchActivity;
import com.cpigeon.book.base.SearchFragmentParentActivity;
import com.cpigeon.book.model.entity.LoftEntity;
import com.cpigeon.book.module.select.adpter.SearchLoftAdapter;
import com.cpigeon.book.module.select.viewmodel.SelectAssViewModel;
import com.gjiazhe.wavesidebar.WaveSideBar;

/**
 * 选择公棚
 * Created by Administrator on 2018/9/5.
 */

public class PlayOrgLoftFragment extends BaseFragment {

    SearchLoftAdapter mAdapter;

    XRecyclerView mRecyclerView;
    WaveSideBar mWaveSideBar;
    LetterSortModel<LoftEntity> mModel = new LetterSortModel<>();
    SelectAssViewModel mViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mViewModel = new SelectAssViewModel();
        initViewModel(mViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_with_wave_bar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = findViewById(R.id.list);
        mWaveSideBar = findViewById(R.id.side_bar);

        mAdapter = new SearchLoftAdapter();
        mAdapter.bindToRecyclerView(mRecyclerView.getRecyclerView());

        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            try {
                Intent intent = new Intent();
                intent.putExtra(IntentBuilder.KEY_DATA, mAdapter.getData().get(position));
                getBaseActivity().setResult(Activity.RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        setProgressVisible(true);
        mViewModel.getLoftList();

        SearchFragmentParentActivity activity = (SearchFragmentParentActivity) getBaseActivity();
        activity.setSearchHint(R.string.text_search_loft);
        activity.setSearchClickListener(v -> {
            SearchLoftActivity.start(getBaseActivity(), SearchLoftActivity.class, null);
        });

    }

    @Override
    protected void initObserve() {
        mViewModel.liveLoft.observe(this, assEntities -> {
            setProgressVisible(false);
            mModel.setData(assEntities);
            mAdapter.initWave(mModel, mWaveSideBar);
            mAdapter.initHead(getBaseActivity());
            mAdapter.setNewData(mModel.getData());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BaseSearchActivity.CODE_SEARCH) {
                IntentBuilder.Builder()
                        .putExtra(IntentBuilder.KEY_DATA, (LoftEntity) data.getParcelableExtra(IntentBuilder.KEY_DATA))
                        .finishForResult(getBaseActivity());
            }
        }
    }
}
