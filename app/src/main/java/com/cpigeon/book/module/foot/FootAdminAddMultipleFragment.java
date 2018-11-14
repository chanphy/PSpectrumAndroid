package com.cpigeon.book.module.foot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.util.IntentBuilder;
import com.base.util.Lists;
import com.base.util.RxUtils;
import com.base.util.Utils;
import com.base.util.dialog.DialogUtils;
import com.base.util.picker.PickerUtil;
import com.base.util.utility.StringUtil;
import com.base.widget.BottomSheetAdapter;
import com.cpigeon.book.R;
import com.cpigeon.book.base.BaseBookFragment;
import com.cpigeon.book.base.BaseInputDialog;
import com.cpigeon.book.event.FootUpdateEvent;
import com.cpigeon.book.model.entity.SelectTypeEntity;
import com.cpigeon.book.module.foot.viewmodel.FootAddMultiViewModel;
import com.cpigeon.book.module.foot.viewmodel.SelectTypeViewModel;
import com.cpigeon.book.util.TextViewUtil;
import com.cpigeon.book.widget.InputBoxView;
import com.cpigeon.book.widget.LineInputListLayout;
import com.cpigeon.book.widget.LineInputView;

import org.greenrobot.eventbus.EventBus;

import cn.qqtheme.framework.picker.OptionPicker;

/**
 * 详情 多个足环  fragment
 * Created by Administrator on 2018/8/10.
 */

public class FootAdminAddMultipleFragment extends BaseBookFragment {

    public static final int CODE_SELECT_COUNTY = 0x123;

    FootAddMultiViewModel mViewModel;
    SelectTypeViewModel mPublicViewModel;
    private LineInputListLayout mLlRoot;
    private LineInputView mLvCity;
    private LineInputView mLvFoot;
    private LineInputView mLvCount;
    private LineInputView mLvCategory;
    private LineInputView mLvSource;
    private LineInputView mLvMoney;
    private InputBoxView mEdRemark;
    private TextView mTvOk;


    public static void start(Activity activity) {
        IntentBuilder.Builder()
                .startParentActivity(activity, FootAdminAddMultipleFragment.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mViewModel = new FootAddMultiViewModel(getBaseActivity());
        mPublicViewModel = new SelectTypeViewModel();
        initViewModels(mViewModel, mPublicViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_multiple_foot, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(R.string.text_input_foot_numbers);
        mLlRoot = findViewById(R.id.llRoot);
        mLvCity = findViewById(R.id.lv_city);
        mLvFoot = findViewById(R.id.lv_foot);
        mLvCount = findViewById(R.id.lv_count);
        mLvCategory = findViewById(R.id.lv_category);
        mLvSource = findViewById(R.id.lv_source);
        mLvMoney = findViewById(R.id.lv_money);
        mEdRemark = findViewById(R.id.edRemark);
        mTvOk = findViewById(R.id.tvOk);

        composite.add(RxUtils.delayed(50, aLong -> {
            mLlRoot.setLineInputViewState(false);
        }));

        mLvCity.setRightImageVisible(false);

        bindUi(RxUtils.textChanges(mLvFoot.getEditText()), mViewModel.setStartFoot());
        bindUi(RxUtils.textChanges(mLvCount.getEditText()), s -> {
            if (!StringUtil.isStringValid(s)) return;
            int count = Integer.valueOf(s);
            if (count > 10000) {
                s = "10000";
                DialogUtils.createHintDialog(getBaseActivity(), Utils.getString(R.string.text_input_foot_numbers_count_limit));
            }
            mViewModel.setCount(s);
        });
        bindUi(RxUtils.textChanges(mLvSource.getEditText()), mViewModel.setSource());
        bindUi(RxUtils.textChanges(mLvMoney.getEditText()), mViewModel.setMoney());
        bindUi(RxUtils.textChanges(mEdRemark.getEditText()), mViewModel.setRemark());

        mLvFoot.setOnRightClickListener(lineInputView -> {
            InputSingleFootDialog.show(getFragmentManager(), mLvFoot.getContent(), true, null, foot -> {
                mLvFoot.setRightText(foot);
            });
        });

        mLvCategory.setOnClickListener(v -> {
            if (!Lists.isEmpty(mViewModel.mSelectTypes_Foot_Ring)) {
                BottomSheetAdapter.createBottomSheet(getBaseActivity()
                        , SelectTypeEntity.getTypeNames(mViewModel.mSelectTypes_Foot_Ring), p -> {
                            mViewModel.typeId = mViewModel.mSelectTypes_Foot_Ring.get(p).getTypeID();
                            mLvCategory.setContent(mViewModel.mSelectTypes_Foot_Ring.get(p).getTypeName());
                            mViewModel.isCanCommit();
                        });
            } else {
                mPublicViewModel.getSelectType_Foot_Ring();
            }
        });

        mLvSource.setOnRightClickListener(lineInputView -> {
            BaseInputDialog.show(getBaseActivity().getSupportFragmentManager()
                    , R.string.text_foot_source, mLvSource.getContent(), 0, content -> {
                        mLvSource.setRightText(content);
                    }, () -> {
                        if (Lists.isEmpty(mViewModel.mSelectTypes_Srouse)) {
                            mPublicViewModel.getSelectType_Source();
                        } else {
                            PickerUtil.showItemPicker(getBaseActivity(), SelectTypeEntity.getTypeNames(mViewModel.mSelectTypes_Srouse)
                                    , 0, new OptionPicker.OnOptionPickListener() {
                                        @Override
                                        public void onOptionPicked(int index, String item) {
                                            mLvSource.setRightText(item);
                                        }
                                    });
                        }
                    });
        });

        mTvOk.setOnClickListener(v -> {
            setProgressVisible(true);
            mViewModel.addMultiFoot();
        });

        mPublicViewModel.getSelectType_Source();
        mPublicViewModel.getSelectType_Foot_Ring();
    }

    @Override
    protected void initObserve() {

        mViewModel.isCanCommit.observe(this, aBoolean -> {
            TextViewUtil.setEnabled(mTvOk, aBoolean);
        });

        mPublicViewModel.mSelectType_Foot_Ring.observe(this, selectTypeEntities -> {
            //类别
            mViewModel.mSelectTypes_Foot_Ring = selectTypeEntities;

            mViewModel.typeId = mViewModel.mSelectTypes_Foot_Ring.get(0).getTypeID();
            mLvCategory.setContent(mViewModel.mSelectTypes_Foot_Ring.get(0).getTypeName());

        });

        mPublicViewModel.mSelectType_Foot_Source.observe(this, selectTypeEntities -> {
            //来源
            mViewModel.mSelectTypes_Srouse = selectTypeEntities;
            mLvSource.setRightText(mViewModel.mSelectTypes_Srouse.get(0).getTypeName());
        });

        mViewModel.addR.observe(this, s -> {
            DialogUtils.createSuccessDialog(getBaseActivity(), s, sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                EventBus.getDefault().post(new FootUpdateEvent());
                finish();
            });
        });
    }
}
