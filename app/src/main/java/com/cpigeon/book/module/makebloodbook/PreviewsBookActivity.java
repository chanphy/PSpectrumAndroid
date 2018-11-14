package com.cpigeon.book.module.makebloodbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.util.IntentBuilder;
import com.base.util.RxUtils;
import com.base.util.Utils;
import com.base.util.glide.GlideUtil;
import com.base.util.utility.ImageUtils;
import com.base.util.utility.StringUtil;
import com.base.util.utility.ToastUtils;
import com.base.widget.photoview.PhotoView;
import com.cpigeon.book.R;
import com.cpigeon.book.base.BaseBookActivity;
import com.cpigeon.book.base.BaseBookFragment;
import com.cpigeon.book.model.UserModel;
import com.cpigeon.book.model.entity.PigeonEntity;
import com.cpigeon.book.model.entity.PigeonEntryEntity;
import com.cpigeon.book.module.basepigeon.InputPigeonFragment;
import com.cpigeon.book.module.breedpigeon.SelectPigeonToAddBreedFragment;
import com.cpigeon.book.module.breedpigeon.viewmodel.BookViewModel;
import com.cpigeon.book.widget.family.FamilyTreeView;

/**
 * Created by Zhu TingYu on 2018/9/10.
 */

public class PreviewsBookActivity extends BaseBookActivity {

    private static final int CODE_CHOOSE_TEMPLATE = 0x123;
    public static final int CODE_ADD_PIGEON = 0x234;

    private LinearLayout mLlImage;
    private ImageView mImgHead;
    private FamilyTreeView mFamilyTreeView;
    private FamilyTreeView mPrintFamilyTreeView;
    private LinearLayout mLlTextV;
    private RelativeLayout mLlTextH;
    private CheckBox mCheckbox;
    private TextView mTvOk;
    private PhotoView mImageView;
    private TextView mTvFootNumber;


    private int bookType = SelectTemplateFragment.TYPE_H;

    private ImageView mImgPrintHead;
    private TextView mTvPrintNumber;
    private RelativeLayout mLlPrintTextV;
    private LinearLayout mLlPrintTextH;

    private BookViewModel mViewModel;


    public static void start(Activity activity, PigeonEntity entity) {
        IntentBuilder.Builder(activity, PreviewsBookActivity.class)
                .putExtra(IntentBuilder.KEY_DATA, entity.getFootRingID())
                .putExtra(IntentBuilder.KEY_DATA_2, entity.getPigeonID())
                .putExtra(IntentBuilder.KEY_TITLE, entity.getFootRingNum())
                .startActivity();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_preview_book;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new BookViewModel(getBaseActivity());
        initViewModel(mViewModel);

        String footNumber = getBaseActivity().getIntent().getStringExtra(IntentBuilder.KEY_TITLE);
        setTitle(footNumber);
        setToolbarRight(R.string.text_choose_template, item -> {
            SelectTemplateFragment.start(getBaseActivity(), bookType, CODE_CHOOSE_TEMPLATE);
            return false;
        });


        mTvFootNumber = findViewById(R.id.tvFootNumber);
        mLlImage = findViewById(R.id.llImage);
        mImgHead = findViewById(R.id.imgHead);
        mFamilyTreeView = findViewById(R.id.familyTreeView);
        mPrintFamilyTreeView = findViewById(R.id.printFamilyTreeView);
        mLlTextV = findViewById(R.id.llTextV);
        mLlTextH = findViewById(R.id.llTextH);
        mCheckbox = findViewById(R.id.checkbox);
        mTvOk = findViewById(R.id.tvOk);
        mImageView = findViewById(R.id.img);


        mImgPrintHead = findViewById(R.id.imgPrintHead);
        mTvPrintNumber = findViewById(R.id.tvPrintNumber);
        mLlPrintTextV = findViewById(R.id.llPrintTextV);
        mLlPrintTextH = findViewById(R.id.llPrintTextH);

        GlideUtil.setGlideImageView(getBaseActivity(), UserModel.getInstance().getUserData().touxiangurl
                , mImgHead);

        mTvFootNumber.setText(UserModel.getInstance().getUserData().pigeonHouseEntity.getXingming());

        GlideUtil.setGlideImageView(getBaseActivity(), UserModel.getInstance().getUserData().touxiangurl
                , mImgPrintHead);


        mCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mLlImage.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                mTvPrintNumber.setText(UserModel.getInstance().getUserData().pigeonHouseEntity.getXingming());
                mImgPrintHead.setVisibility(View.VISIBLE);
            } else {
                mTvPrintNumber.setText(Utils.getString(R.string.text_blood_book_count, footNumber));
                mImgPrintHead.setVisibility(View.GONE);
            }
        });

        mTvOk.setOnClickListener(v -> {
            getBookView();
        });

        mImageView.setOnViewTapListener((view1, x, y) -> {
            mImageView.setVisibility(View.GONE);
        });

        mCheckbox.setChecked(true);

        getBaseActivity().setOnActivityFinishListener(() -> {
            if (mImageView.getVisibility() == View.VISIBLE) {
                mImageView.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        setProgressVisible(true);
        composite.add(RxUtils.delayed(250, aLong -> {
            mFamilyTreeView.setHorizontal(true);
            mFamilyTreeView.setTypeMove(FamilyTreeView.TYPE_IS_CAN_MOVE_H);
            mFamilyTreeView.initView();

            mPrintFamilyTreeView.setHorizontal(true);
            mPrintFamilyTreeView.setTypeMove(FamilyTreeView.TYPE_IS_CAN_MOVE_H);
            mPrintFamilyTreeView.setShowLine(false);
            mPrintFamilyTreeView.initView();
            mViewModel.isNeedMatch = true;
            mViewModel.getBloodBook();

            initObserve();
        }));

        mFamilyTreeView.setOnFamilyClickListener(new FamilyTreeView.OnFamilyClickListener() {
            @Override
            public void add(int x, int y) {

                PigeonEntity breedPigeonEntity = null;
                if (mFamilyTreeView.getSon(x, y) != null) {
                    breedPigeonEntity = mFamilyTreeView.getSon(x, y).getData();
                }

                boolean isMale = FamilyTreeView.isMale(y);
                if (isMale) {
                    SelectPigeonToAddBreedFragment.start(getBaseActivity()
                            , breedPigeonEntity == null ? StringUtil.emptyString() : breedPigeonEntity.getFootRingID()
                            , breedPigeonEntity == null ? StringUtil.emptyString() : breedPigeonEntity.getPigeonID()
                            , CODE_ADD_PIGEON
                            , PigeonEntity.ID_MALE, PigeonEntity.ID_NONE_SEX);
                } else {
                    SelectPigeonToAddBreedFragment.start(getBaseActivity()
                            , breedPigeonEntity == null ? StringUtil.emptyString() : breedPigeonEntity.getFootRingID()
                            , breedPigeonEntity == null ? StringUtil.emptyString() : breedPigeonEntity.getPigeonID()
                            , CODE_ADD_PIGEON
                            , PigeonEntity.ID_FEMALE, PigeonEntity.ID_NONE_SEX);
                }

            }

            @Override
            public void showInfo(int x, int y, PigeonEntity breedPigeonEntity) {
                String sex = StringUtil.emptyString();
                if (x != mFamilyTreeView.getStartGeneration()) {
                    sex = FamilyTreeView.isMale(y) ? InputPigeonFragment.TYPE_SEX_MALE : InputPigeonFragment.TYPE_SEX_FEMALE;
                }

                InputPigeonFragment.start(getBaseActivity()
                        , breedPigeonEntity != null ? breedPigeonEntity.getPigeonID() : StringUtil.emptyString()
                        , StringUtil.emptyString()
                        , StringUtil.emptyString()
                        , sex
                        , breedPigeonEntity.getPigeonSexID()
                        , CODE_ADD_PIGEON);
            }
        });

        initObserve();
    }

    public void getBookView() {
        mViewModel.addBloodnum();
        try {
            RelativeLayout view = findViewById(R.id.rlImage);
            setProgressVisible(true);
            composite.add(RxUtils.delayed(100, aLong -> {
                Bitmap bitmap = ImageUtils.view2Bitmap(view);
                ImageUtils.saveImageToGallery(getBaseActivity(), bitmap);
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(bitmap);
                setProgressVisible(false);
                ToastUtils.showLong(getBaseActivity(), R.string.text_save_blood_book_yet);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initObserve() {
        mViewModel.mBookLiveData.observe(this, bloodBookEntity -> {
            setProgressVisible(false);
            mFamilyTreeView.setData(bloodBookEntity);
            mPrintFamilyTreeView.setData(bloodBookEntity);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == CODE_CHOOSE_TEMPLATE) {
            int type = data.getIntExtra(IntentBuilder.KEY_DATA, bookType);
            bookType = type;

            if (bookType == SelectTemplateFragment.TYPE_H) {

                mFamilyTreeView.setHorizontal(true);
                mFamilyTreeView.setTypeMove(FamilyTreeView.TYPE_IS_CAN_MOVE_H);
                mFamilyTreeView.initView();
                mFamilyTreeView.setData(mViewModel.mBloodBookEntity);

                RelativeLayout.LayoutParams treeP = new RelativeLayout.LayoutParams(2480, 3270);
                treeP.addRule(RelativeLayout.CENTER_IN_PARENT);
                mPrintFamilyTreeView.setLayoutParams(treeP);
                mPrintFamilyTreeView.setHorizontal(true);
                mPrintFamilyTreeView.setTypeMove(FamilyTreeView.TYPE_IS_CAN_MOVE_H);
                mPrintFamilyTreeView.setShowLine(false);
                mPrintFamilyTreeView.setLayoutParams(treeP);

                composite.add(RxUtils.delayed(200, aLong -> {
                    mPrintFamilyTreeView.initView();
                    mPrintFamilyTreeView.setData(mViewModel.mBloodBookEntity);
                }));


                mLlPrintTextH.setVisibility(View.VISIBLE);
                mLlPrintTextV.setVisibility(View.GONE);

            } else if (bookType == SelectTemplateFragment.TYPE_V) {
                mFamilyTreeView.setTypeMove(FamilyTreeView.TYPE_IS_CAN_MOVE_V);
                mFamilyTreeView.setHorizontal(false);
                mFamilyTreeView.initView();
                mFamilyTreeView.setData(mViewModel.mBloodBookEntity);


                mPrintFamilyTreeView.setHorizontal(false);
                mPrintFamilyTreeView.setTypeMove(FamilyTreeView.TYPE_IS_CAN_MOVE_V);
                mPrintFamilyTreeView.setShowLine(true);
                RelativeLayout.LayoutParams treeP = new RelativeLayout.LayoutParams(2480, 2330);
                treeP.addRule(RelativeLayout.CENTER_IN_PARENT);
                mPrintFamilyTreeView.setLayoutParams(treeP);
                composite.add(RxUtils.delayed(200, aLong -> {
                    mPrintFamilyTreeView.initView();
                    mPrintFamilyTreeView.setData(mViewModel.mBloodBookEntity);
                }));

                mLlPrintTextH.setVisibility(View.GONE);
                mLlPrintTextV.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == CODE_ADD_PIGEON) {
            if (!StringUtil.isStringValid(mViewModel.foodId)) {
                PigeonEntryEntity entity = data.getParcelableExtra(IntentBuilder.KEY_DATA);
                mViewModel.foodId = entity.getFootRingID();
                mViewModel.pigeonId = entity.getPigeonID();
            }
            setProgressVisible(true);
            mViewModel.getBloodBook();
        }
    }
}
