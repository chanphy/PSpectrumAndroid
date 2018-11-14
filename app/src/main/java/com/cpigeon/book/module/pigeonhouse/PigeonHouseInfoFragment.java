package com.cpigeon.book.module.pigeonhouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.base.util.IntentBuilder;
import com.base.util.Lists;
import com.base.util.LocationFormatUtils;
import com.base.util.PermissionUtil;
import com.base.util.PictureSelectUtil;
import com.base.util.RxUtils;
import com.base.util.Utils;
import com.base.util.dialog.DialogUtils;
import com.base.util.map.AmapManager;
import com.base.util.map.PointToAddressManager;
import com.base.util.picker.AddressPickTask;
import com.base.util.picker.PickerUtil;
import com.base.util.utility.StringUtil;
import com.base.util.utility.ToastUtils;
import com.base.widget.BottomSheetAdapter;
import com.bumptech.glide.Glide;
import com.cpigeon.book.R;
import com.cpigeon.book.base.BaseBookFragment;
import com.cpigeon.book.base.SearchFragmentParentActivity;
import com.cpigeon.book.model.UserModel;
import com.cpigeon.book.model.entity.AssEntity;
import com.cpigeon.book.module.MainActivity;
import com.cpigeon.book.module.pigeonhouse.viewmodle.PigeonHouseViewModel;
import com.cpigeon.book.module.select.SelectAssFragment;
import com.cpigeon.book.module.select.SelectLocationByMapFragment;
import com.cpigeon.book.util.TextViewUtil;
import com.cpigeon.book.widget.LineInputListLayout;
import com.cpigeon.book.widget.LineInputView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Zhu TingYu on 2018/7/31.
 */

public class PigeonHouseInfoFragment extends BaseBookFragment {

    public static int CODE_ORGANIZE = 0x123;
    public static int CODE_LOCATION = 0x234;
    public static int CODE_AUTH = 0x345;

    private CircleImageView mImgHead;
    private TextView mTvName;
    private TextView mTvAuth;
    private TextView mTvOk;
    private ImageView mImgSex;

    private LineInputListLayout mLlLineInput;
    private LineInputView mLvHouseName;
    private LineInputView mLvPhone;
    private LineInputView mLvOrganize;
    private LineInputView mLvShedId;
    private LineInputView mLvJoinMatchId;
    private LineInputView mLvHouseLocation;
    private LineInputView mLvCity;
    private LineInputView mLvAddress;
    InputLocationFragment inputLocationFragment;

    private boolean mIsLook;
    private String mHeadImagePath;

    PigeonHouseViewModel mViewModel;
    PointToAddressManager mPointToAddressManager;

    public static void start(Activity activity, boolean isLook) {
        IntentBuilder.Builder()
                .putExtra(IntentBuilder.KEY_BOOLEAN, isLook)
                .startParentActivity(activity, PigeonHouseInfoFragment.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //第一次登录  获取鸽币
        mViewModel = new PigeonHouseViewModel();
        initViewModel(mViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pigon_house_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle(R.string.title_pigeon_house_info);

        setToolbarLeft(R.drawable.svg_back, v -> {
            if (UserModel.getInstance().getUserData() != null
                    && UserModel.getInstance().getUserData().pigeonHouseEntity != null) {
                finish();

            } else {
                DialogUtils.createDialog(getBaseActivity(), "提示！", "请完善鸽舍信息！", Utils.getString(R.string.btn_cancel)
                        , Utils.getString(R.string.btn_confirm), sweetAlertDialog -> {
                            sweetAlertDialog.dismiss();
                            finish();
                        }, sweetAlertDialog -> {
                            sweetAlertDialog.dismiss();
                        });
            }
        });

        mIsLook = getBaseActivity().getIntent().getBooleanExtra(IntentBuilder.KEY_BOOLEAN, false);
        composite.add(RxUtils.delayed(50, aLong -> {
            mLlLineInput.getChildViews();
            mLlLineInput.setLineInputViewState(mIsLook);
        }));


        PermissionUtil.getAppDetailSettingIntent(getBaseActivity());

        mViewModel.oneStartGetGeBi();//第一次登录

        mImgHead = findViewById(R.id.imgHead);
        mTvName = findViewById(R.id.tvName);
        mTvAuth = findViewById(R.id.tvAuth);


        mLlLineInput = findViewById(R.id.llLineInput);
        mLvHouseName = findViewById(R.id.lvHouseName);
        mLvPhone = findViewById(R.id.lvPhone);
        mLvOrganize = findViewById(R.id.lvOrganize);
        mLvShedId = findViewById(R.id.lvShedId);
        mLvJoinMatchId = findViewById(R.id.lvJoinMatchId);
        mLvHouseLocation = findViewById(R.id.lvHouseLocation);
        mLvCity = findViewById(R.id.lvCity);
        mLvAddress = findViewById(R.id.lvAddress);
        mTvOk = findViewById(R.id.tvOk);
        mImgSex = findViewById(R.id.imgSex);

        mTvAuth.setOnClickListener(v -> {
            IdCertificationFragment.start(getBaseActivity(), true, CODE_AUTH);
        });

        bindUi(RxUtils.textChanges(mLvHouseName.getEditText()), mViewModel.setPigeonHomeName());
        bindUi(RxUtils.textChanges(mLvPhone.getEditText()), mViewModel.setPigeonHomePhone());
        bindUi(RxUtils.textChanges(mLvOrganize.getEditText()), mViewModel.setPigeonISOCID());
        bindUi(RxUtils.textChanges(mLvShedId.getEditText()), mViewModel.setUsePigeonHomeNum());
        bindUi(RxUtils.textChanges(mLvJoinMatchId.getEditText()), mViewModel.setPigeonMatchNum());
        bindUi(RxUtils.textChanges(mLvAddress.getEditText()), mViewModel.setPigeonHomeAdds());
        bindUi(RxUtils.textChanges(mLvHouseLocation.getEditText()), s -> {
            mViewModel.isCanCommit();
        });

        mLvPhone.setRightText(UserModel.getInstance().getUserData().handphone);

        mLvOrganize.setOnRightClickListener(lineInputView -> {
            SearchFragmentParentActivity.start(getBaseActivity(), SelectAssFragment.class, CODE_ORGANIZE, null);
        });

        String[] chooseWays = getResources().getStringArray(R.array.array_choose_photo);
        mImgHead.setOnClickListener(v -> {
            BottomSheetAdapter.createBottomSheet(getBaseActivity(), Lists.newArrayList(chooseWays), p -> {
                String way = chooseWays[p];
                if (Utils.getString(R.string.text_open_gallery).equals(way)) {
                    PictureSelectUtil.showChooseHeadImage(getBaseActivity());
                } else if (Utils.getString(R.string.text_open_camera).equals(way)) {
                    PictureSelectUtil.openCamera(getBaseActivity(), true);
                }
            });
        });

        mLvHouseLocation.setOnRightClickListener(lineInputView -> {
            inputLocationFragment = new InputLocationFragment();
            inputLocationFragment.setOnSureClickListener(new InputLocationFragment.OnInputLocationClickListener() {
                @Override
                public void sure(String lo, String la) {

                    mLvHouseLocation.setContent(getString(R.string.text_location_lo_la, LocationFormatUtils.strToDMS(lo)
                            , LocationFormatUtils.strToDMS(la)));

                    LatLng latLng = new LatLng(LocationFormatUtils.Aj2GPSLocation(Double.valueOf(lo))
                            , LocationFormatUtils.Aj2GPSLocation(Double.valueOf(la)));
                    LatLng c = AmapManager.converter(getContext(), latLng);

                    mViewModel.mLongitude = String.valueOf(c.longitude);
                    mViewModel.mLatitude = String.valueOf(c.latitude);

//                    mPointToAddressManager.setSearchPoint(new LatLonPoint(Double.valueOf(mViewModel.mLatitude)
//                            , Double.valueOf(mViewModel.mLongitude))).search();

                }

                @Override
                public void location() {
                    SelectLocationByMapFragment.start(getBaseActivity(), CODE_LOCATION);
                }
            });
            inputLocationFragment.show(getBaseActivity().getSupportFragmentManager());
        });

        mLvCity.setOnRightClickListener(lineInputView -> {
            PickerUtil.onAddress3Picker(getBaseActivity(), new AddressPickTask.Callback() {
                @Override
                public void onAddressInitFailed() {

                }

                @Override
                public void onAddressPicked(Province province, City city, County county) {
                    mLvCity.setContent(province.getName() + city.getName() + county.getName());
                    mViewModel.mProvince = province.getName();//省
                    mViewModel.mCity = city.getName();//市
                    mViewModel.mCounty = county.getName();//县
                }
            });
        });

        if (!mIsLook) {
            mTvOk.setVisibility(View.VISIBLE);
            mTvOk.setOnClickListener(v -> {
                setProgressVisible(true);
                mViewModel.setPigeonHouse();
            });
        } else {
            mLlLineInput.setOnInputViewGetFocusListener(() -> {
                mTvOk.setVisibility(View.VISIBLE);
            });
            setProgressVisible(true);
            mViewModel.getPigeonHouse();
            mTvOk.setText(Utils.getString(R.string.text_sure_commit));
            mTvOk.setOnClickListener(v -> {
                setProgressVisible(true);
                mViewModel.setPigeonHouse();
            });

        }

    }

    protected void initObserve() {

        mViewModel.isCanCommit.observe(this, aBoolean -> {
            TextViewUtil.setEnabled(mTvOk, aBoolean);
        });

        UserModel.getInstance().mUserLiveData.observe(this, userEntity -> {
            Glide.with(getBaseActivity()).load(userEntity.touxiangurl).into(mImgHead);
        });

        mViewModel.oneStartHintStr.observe(this, r -> {
            ToastUtils.showLong(getActivity(), r);
        });

        mViewModel.setHeadUrlR.observe(this, s -> {

        });

        mViewModel.addR.observe(this, s -> {
            setProgressVisible(false);


            mLlLineInput.setFocusable(true);
            mLlLineInput.setFocusableInTouchMode(true);
            mLlLineInput.requestFocus();

            mLlLineInput.getChildViews();
            mLlLineInput.setLineInputViewState(true);
            mTvOk.setVisibility(View.GONE);
            if (UserModel.getInstance().getUserData().pigeonHouseEntity == null) {
                MainActivity.start(getBaseActivity());
            }
            UserModel.getInstance().setIsHaveHouseInfo(true);
        });

        mViewModel.mHouseEntityInfo.observe(this, r -> {
            setProgressVisible(false);
            if (r == null) {
                return;
            }
            Glide.with(getBaseActivity()).load(r.getTouxiangurl()).into(mImgHead);
            mTvName.setText(r.getXingming());
            mLvPhone.setContent(r.getPigeonHomePhone());
            mLvHouseName.setContent(r.getPigeonHomeName());
            mLvOrganize.setContent(r.getPigeonISOCID());
            mLvShedId.setContent(r.getUsePigeonHomeNum());
            mLvJoinMatchId.setContent(r.getPigeonMatchNum());
            mLvHouseLocation.setContent(getString(R.string.text_location_lo_la
                    , LocationFormatUtils.loLaToDMS(r.getLongitude())
                    , LocationFormatUtils.loLaToDMS(r.getLatitude())));

            mViewModel.mLongitude = String.valueOf(r.getLongitude());
            mViewModel.mLatitude = String.valueOf(r.getLatitude());

            mLvCity.setContent(r.getProvince() + r.getCity() + r.getCounty());
            mLvAddress.setContent(r.getPigeonHomeAdds());

            mViewModel.mProvince = r.getProvince();//省
            mViewModel.mCity = r.getCity();//市
            mViewModel.mCounty = r.getCounty();//县


            if (StringUtil.isStringValid(r.getXingming())) {
                mTvAuth.setText(getString(R.string.text_yet_auth));
                mTvAuth.setBackgroundResource(R.drawable.shape_btn_stroke_blue);
                mTvAuth.setTextColor(Utils.getColor(R.color.color_scale_blue_data));
                mTvAuth.setOnClickListener(v -> {
                    IdCertificationFragment.start(getBaseActivity(), false, CODE_AUTH);
                });

                if (Utils.getString(R.string.text_male).equals(r.getXingbie())) {
                    mImgSex.setImageResource(R.mipmap.ic_male);
                } else if (Utils.getString(R.string.text_female).equals(r.getXingbie())) {
                    mImgSex.setImageResource(R.mipmap.ic_female);
                }
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == PictureMimeType.ofImage()) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            mViewModel.mHeadUrl = selectList.get(0).getCutPath();
            mViewModel.setUserFace();
        } else if (requestCode == CODE_ORGANIZE) {
            String organize = ((AssEntity) data.getParcelableExtra(IntentBuilder.KEY_DATA)).getISOCName();
            mViewModel.mPigeonISOCID = organize;
            mLvOrganize.setContent(organize);
        } else if (requestCode == CODE_LOCATION) {

            RegeocodeAddress address = data.getParcelableExtra(IntentBuilder.KEY_DATA);
            LatLonPoint point = data.getParcelableExtra(IntentBuilder.KEY_DATA_2);

            mLvHouseLocation.setContent(getString(R.string.text_location_lo_la
                    , LocationFormatUtils.loLaToDMS(point.getLongitude())
                    , LocationFormatUtils.loLaToDMS(point.getLatitude())));

            mViewModel.mLongitude = String.valueOf(point.getLongitude());
            mViewModel.mLatitude = String.valueOf(point.getLatitude());

            bindAddress(address);
        } else if (requestCode == CODE_AUTH) {
            mTvAuth.setText(getString(R.string.text_yet_auth));
            mTvAuth.setBackgroundResource(R.drawable.shape_btn_stroke_blue);
            mTvAuth.setTextColor(Utils.getColor(R.color.colorPrimary));
            mTvAuth.setOnClickListener(v -> {
                IdCertificationFragment.start(getBaseActivity(), false, CODE_AUTH);
            });
            mViewModel.getPigeonHouse();
        }
    }

    private void bindAddress(RegeocodeAddress mAddress) {
        String address = mAddress.getProvince() + mAddress.getCity() + mAddress.getDistrict();
        mLvCity.setContent(address);
        mViewModel.mProvince = mAddress.getProvince();
        mViewModel.mCity = mAddress.getCity();
        mViewModel.mCounty = mAddress.getDistrict();
    }
}
