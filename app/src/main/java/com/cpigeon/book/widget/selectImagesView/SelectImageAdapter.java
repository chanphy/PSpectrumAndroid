package com.cpigeon.book.widget.selectImagesView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.base.base.BaseActivity;
import com.base.base.BaseViewHolder;
import com.base.util.Lists;
import com.base.util.glide.GlideUtil;
import com.base.util.system.ScreenTool;
import com.cpigeon.book.R;
import com.cpigeon.book.service.EventBusService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Created by Zhu TingYu on 2018/8/22.
 */

public class SelectImageAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int MAX_NUMBER = 6;
    protected final static int TYPE_ADD = 1;

    BaseActivity mBaseActivity;
    List<String> mImgData = Lists.newArrayList();

    int rootW;

    public SelectImageAdapter(BaseActivity activity) {
        mBaseActivity = activity;
        rootW = ScreenTool.getScreenWidth() / MAX_NUMBER;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD) {
            View view = LayoutInflater.from(mBaseActivity).inflate(R.layout.item_selecte_image_add_image, parent, false);
            return new AddViewHolder(view);
        } else {
            View view = LayoutInflater.from(mBaseActivity).inflate(R.layout.item_selecte_image_show, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ADD) {
            AddViewHolder viewHolder = (AddViewHolder) holder;
        } else {
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            viewHolder.bindData(getImagePosition(position));
        }
    }

    private int getImagePosition(int position) {
        int imagePosition = position;
        if (mImgData.size() < MAX_NUMBER) {
            imagePosition -= 1;
        }
        return imagePosition;
    }

    @Override
    public int getItemCount() {
        if (mImgData.size() < MAX_NUMBER) {
            return mImgData.size() + 1;
        } else {
            return mImgData.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mImgData.size() < MAX_NUMBER && position == 0) {
            return TYPE_ADD;
        } else {
            return super.getItemViewType(position);
        }
    }

    protected class ImageViewHolder extends BaseViewHolder {
        RelativeLayout rlRoot;
        public ImageView icon;
        public ImageView del;

        public ImageViewHolder(View itemView) {
            super(itemView);
            rlRoot = itemView.findViewById(R.id.rlRoot);
            icon = itemView.findViewById(R.id.img);
            icon.setVisibility(View.VISIBLE);
            del = itemView.findViewById(R.id.imgDel);

            RecyclerView.LayoutParams rootParams = new RecyclerView.LayoutParams(rootW, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlRoot.setLayoutParams(rootParams);
        }

        void bindData(int position) {

            GlideUtil.setGlideImageViewHaveRound(mBaseActivity, mImgData.get(position), icon);

            del.setVisibility(View.VISIBLE);
            del.setOnClickListener(v -> {

                EventBus.getDefault().post(EventBusService.BREED_PIGEON_IMG_TYPE);//通知数据

                mImgData.remove(position);
                notifyDataSetChanged();
                if (mOnSelectImageClickListener != null) {
                    mOnSelectImageClickListener.onImageDelete(position);
                }
            });
        }
    }

    class AddViewHolder extends BaseViewHolder {
        RelativeLayout rlRoot;
        ImageView addBtn;

        public AddViewHolder(View itemView) {
            super(itemView);

            rlRoot = itemView.findViewById(R.id.rlRoot);
            addBtn = itemView.findViewById(R.id.imgAdd);

            addBtn.setScaleType(ImageView.ScaleType.CENTER);
            addBtn.setImageResource(R.mipmap.ic_add);

            RecyclerView.LayoutParams rootParams = new RecyclerView.LayoutParams(rootW, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlRoot.setLayoutParams(rootParams);
            bindData();

        }

        public void bindData() {
            addBtn.setOnClickListener(view -> {
                if (mOnSelectImageClickListener != null) {
                    mOnSelectImageClickListener.onAddImage();
                }
            });
        }
    }

    public void addImage(List<String> urls) {
        mImgData.addAll(0, urls);
        notifyDataSetChanged();
    }

    public List<String> getImgData() {
        return mImgData;
    }

    public interface OnSelectImageClickListener {
        void onAddImage();

        void onImageDelete(int position);
    }

    private OnSelectImageClickListener mOnSelectImageClickListener;

    public void setOnSelectImageClickListener(OnSelectImageClickListener onSelectImageClickListener) {
        mOnSelectImageClickListener = onSelectImageClickListener;
    }
}
