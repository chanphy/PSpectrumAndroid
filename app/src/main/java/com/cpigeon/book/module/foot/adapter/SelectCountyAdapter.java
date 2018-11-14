package com.cpigeon.book.module.foot.adapter;

import android.view.View;
import android.widget.TextView;

import com.base.base.BaseViewHolder;
import com.base.base.adpter.BaseLetterSelectAdapter;
import com.base.util.IntentBuilder;
import com.base.util.Lists;
import com.base.util.utility.StringUtil;
import com.cpigeon.book.R;
import com.cpigeon.book.model.entity.CountyEntity;
import com.cpigeon.book.module.foot.FootAdminSingleFragment;
import com.cpigeon.book.module.foot.SelectCountyAreaFragment;

/**
 * Created by Zhu TingYu on 2018/8/20.
 */

public class SelectCountyAdapter extends BaseLetterSelectAdapter<CountyEntity, BaseViewHolder> {

    public SelectCountyAdapter() {
        super(R.layout.item_select_county, Lists.newArrayList());
    }

    @Override
    protected void convert(BaseViewHolder helper, CountyEntity item) {
        TextView tvCode = helper.getView(R.id.tvCountyCode);
        if(StringUtil.isStringValid(item.getCode())){
            tvCode.setVisibility(View.VISIBLE);
            tvCode.setText(item.getCode());
        }else {
            tvCode.setVisibility(View.GONE);
        }

        helper.setText(R.id.tvCountyName, item.getCountry());

        helper.itemView.setOnClickListener(v -> {
            CountyEntity entity = getItem(helper.getAdapterPosition());

            if(StringUtil.isStringValid(entity.getFootCodeID())){
                IntentBuilder.Builder()
                        .putExtra(IntentBuilder.KEY_DATA, entity)
                        .finishForResult(getBaseActivity());
            }else {
                IntentBuilder.Builder()
                        .putExtra(IntentBuilder.KEY_DATA, entity.getSort())
                        .startParentActivity(getBaseActivity(), SelectCountyAreaFragment.class, FootAdminSingleFragment.CODE_SELECT_COUNTY);
            }
        });
    }
}
