package com.cpigeon.book.module.menu.feedback.adpter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.base.base.BaseViewHolder;
import com.base.util.Lists;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.cpigeon.book.R;
import com.cpigeon.book.model.UserModel;
import com.cpigeon.book.model.entity.FeedbackDetailEntity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Zhu TingYu on 2018/8/23.
 */

public class FeedbackDetailsAdapter extends BaseMultiItemQuickAdapter<FeedbackDetailEntity, BaseViewHolder> {

    public FeedbackDetailsAdapter() {
        super(Lists.newArrayList());
        addItemType(FeedbackDetailEntity.TYPE_FEEDBACK, R.layout.item_feedback_details_feedback);
        addItemType(FeedbackDetailEntity.TYPE_REPLY, R.layout.item_feedback_details_reply);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedbackDetailEntity item) {

        helper.setText(R.id.tvTime, item.getDatetime());

        Glide.with(mContext)
                .load(UserModel.getInstance().getUserData().touxiangurl)
                .placeholder(mContext.getResources().getDrawable(R.mipmap.ic_system_head))
                .into((CircleImageView) helper.getView(R.id.imgHead));

        switch (helper.getItemViewType()) {
            case FeedbackDetailEntity.TYPE_FEEDBACK:
                helper.setText(R.id.tvContent, item.getContent());
                RecyclerView imgs = helper.getView(R.id.imgList);
                if (!Lists.isEmpty(item.getImglist())) {
                    imgs.setVisibility(View.VISIBLE);
                    imgs.setLayoutManager(new GridLayoutManager(mContext, 4) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    });
                    FeedbackDetailsImageAdapter adapter = (FeedbackDetailsImageAdapter) imgs.getAdapter();
                    if (adapter == null) {
                        adapter = new FeedbackDetailsImageAdapter(imgs);
                    }
                    imgs.setAdapter(adapter);
                    adapter.setNewData(item.getImglist());
                    imgs.setFocusableInTouchMode(false);
                } else {
                    imgs.setVisibility(View.GONE);
                }

                break;
            case FeedbackDetailEntity.TYPE_REPLY:
                helper.setText(R.id.tvTime, item.getReplytime());
                helper.setText(R.id.tvContent, item.getReplycontent());
                break;
        }
    }
}
