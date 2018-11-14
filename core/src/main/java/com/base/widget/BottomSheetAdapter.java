package com.base.widget;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.base.base.adpter.BaseRecyclerViewAdapter;
import com.base.base.BaseViewHolder;
import com.base.http.R;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration;

import java.util.List;

/**
 * 底部列表选项（hl 彷ios底部弹出选择器）
 */

public class BottomSheetAdapter extends BaseRecyclerViewAdapter<String>
        implements FlexibleDividerDecoration.ColorProvider, FlexibleDividerDecoration.SizeProvider {


    OnItemClickListener onItemClickListener;
    static BottomSheetDialog bottomSheetDialog;

    public static BottomSheetDialog createBottomSheet(Context context, List<String> list
            , OnItemClickListener onItemClickListener) {

        View view = View.inflate(context, R.layout.dailog_botton_layout, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        BottomSheetAdapter adapter = new BottomSheetAdapter(context);

        adapter.setList(list);
        adapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(adapter);


        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        Window window = bottomSheetDialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 0.9f;
//        window.setAttributes(lp);

        view.findViewById(R.id.btn).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
        return bottomSheetDialog;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int dividerColor(int position, RecyclerView parent) {
        return getColor(R.color.gray);
    }

    @Override
    public int dividerSize(int position, RecyclerView parent) {
        return 1;
    }

    public interface OnItemClickListener {
        void onItemClick(int p);
    }


    public BottomSheetAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater(R.layout.item_single_text_layout, parent));
    }

    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.textView.setText(getItem(position));
        holder.itemView.setOnClickListener(e -> {
            if (onItemClickListener != null)
                bottomSheetDialog.dismiss();
            onItemClickListener.onItemClick(position);
        });
    }


    private class ItemViewHolder extends BaseViewHolder {
        public TextView textView;
        public ImageView imageView;
        public View view;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.layout);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
            this.textView = (TextView) itemView.findViewById(R.id.text_view);
            this.textView.setGravity(Gravity.CENTER);
            this.imageView.setVisibility(View.GONE);
        }


    }
}