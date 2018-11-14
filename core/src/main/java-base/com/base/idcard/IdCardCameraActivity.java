package com.base.idcard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.RelativeLayout;

import com.base.base.BaseActivity;
import com.base.http.R;
import com.base.util.IntentBuilder;
import com.base.util.cache.CacheUtils;
import com.base.util.dialog.DialogUtils;
import com.base.util.http.GsonUtil;
import com.base.util.system.ScreenTool;
import com.base.util.utility.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Zhu TingYu on 2017/11/30.
 */

public class IdCardCameraActivity extends AppCompatActivity {

    public static final int CODE_ID_CARD_P = 123;
    public static final int CODE_ID_CARD_N = 234;

    public static final int TYPE_P = 0; //身份证正面
    public static final int TYPE_N = 1; //身份证反面

    public static final String IMAGE_P_PATH = "IMAGE_P_PATH";
    public static final String IMAGE_N_PATH = "IMAGE_N_PATH";

    private SurfaceView surfaceview;
    private Camera camera;
    private AppCompatImageView take;
    private AppCompatImageView frame;

    private int screenW;
    private int screenH;

    private int photoH;
    private int photoW;

    private static final double RATIO_PHOTO_W = 1.5f;
    private static final double RATIO_SCREN_W = (3f / 5f);


    private IdCardIdentification idCardIdentification;

    SweetAlertDialog mLoadingSweetAlertDialog;

    SweetAlertDialog dialogPrompt;

    AppCompatTextView hint;

    int type;

    Context context;

    public static void start(BaseActivity baseActivity, int type) {
        IntentBuilder builder = IntentBuilder.Builder(baseActivity, IdCardCameraActivity.class)
                .putExtra(IntentBuilder.KEY_TYPE, type);
        if(type == TYPE_N){
            builder.startActivity(CODE_ID_CARD_N);
        }else {
            builder.startActivity(CODE_ID_CARD_P);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        type = getIntent().getIntExtra(IntentBuilder.KEY_TYPE,0);

        screenH = ScreenTool.getScreenHeight();
        screenW = ScreenTool.getScreenWidth();

       /*photoW = ScreenTool.dip2px(400);
       photoH = ScreenTool.dip2px(260);*/

        idCardIdentification = new IdCardIdentification();

        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_id_card_camera_layout);

        new RxPermissions(this).request(Manifest.permission.CAMERA).subscribe(aBoolean -> {
            if(aBoolean){
                try {
                    initView();
                } catch (Exception e) {
                    DialogUtils.createHintDialog(this,"缺少相机权限请去 设置->应用信息—>权限管理");
                }
            }else{
                DialogUtils.createHintDialog(this,"缺少相机权限请去 设置->应用信息—>权限管理");
            }
        });
    }

    public void initView() {
        take = findViewById(R.id.take);
        frame = findViewById(R.id.frame);
        frame.setBackgroundResource(R.drawable.background_blue);
        hint = findViewById(R.id.tv_hint);

        if(type == TYPE_P){
            hint.setText("请拍摄身份证正面");
        }else {
            hint.setText("请拍摄身份证反面");
        }

        //设置选择框的大小

        int w = (int) (screenW * RATIO_SCREN_W); // 宽为屏幕宽带的 3/5
        int h = (int) (w / RATIO_PHOTO_W);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, h);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        frame.setLayoutParams(layoutParams);

        surfaceview = findViewById(R.id.surfaceview);
        SurfaceHolder holder = surfaceview.getHolder();
        holder.setFixedSize(screenW, screenH);// 设置分辨率
        holder.setKeepScreenOn(true);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // SurfaceView只有当activity显示到了前台，该控件才会被创建     因此需要监听surfaceview的创建
        holder.addCallback(new MySurfaceCallback());

        take.setOnClickListener(v -> {
            try {
                takepicture();
            } catch (Exception e) {
                DialogUtils.createHintDialog(context,"缺少相机权限请去 设置->应用信息—>权限管理");
            }
        });

    }

    //拍照的函数
    public void takepicture() {
        /*
         * shutter:快门被按下
         * raw:相机所捕获的原始数据
         * jpeg:相机处理的数据
         */
        camera.takePicture(null, null, new MyPictureCallback());
    }
    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {

                Bitmap bitmap = Bytes2Bimap(data);
                Matrix m = new Matrix();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                //m.setRotate(90);
                //将照片右旋90度
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m,
                        true);

                int wId = (int) (width * RATIO_SCREN_W);
                int hId = (int) (wId / RATIO_PHOTO_W);

                Log.d("TAG", "width " + width);
                Log.d("TAG", "height " + height);

                //截取透明框内照片(身份证)
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap
                        , (width  - wId) / 2
                        , (height - hId) / 2
                        , wId
                        , hId);



                data = Bitmap2Bytes(bitmap1);
                File file;
                if(type == TYPE_P){
                    file = new File(CacheUtils.getInstance().getIdCardPath(true));
                }else {
                    file = new File(CacheUtils.getInstance().getIdCardPath(false));
                }


                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();

                file.canRead();
                showLoad();
                if(type == TYPE_P){
                    idCardIdentification.IdCardOcr(file.getPath(),IdCardIdentification.TYPE_POSITIVE,jsonObject -> {
                        showLoad();
                        if (!jsonObject.isNull("errorcode")) {
                            camera.startPreview();
                            IdCardPInfoEntity idCardPInfoEntity = GsonUtil.fromJson(jsonObject.toString(),new TypeToken<IdCardPInfoEntity>(){}.getType());
                            if(idCardPInfoEntity.errorcode == 0){
                                idCardPInfoEntity.frontimage = file.getPath();

                                CacheUtils.getInstance().put(IdCardCameraActivity.IMAGE_P_PATH,file.getPath());

                                Intent intent = new Intent();
                                intent.putExtra(IntentBuilder.KEY_DATA, idCardPInfoEntity);
                                setResult(RESULT_OK, intent);
                                ToastUtils.showLong(getApplicationContext(),"识别成功");
                                finish();
                            }else {
                                error();
                            }
                        }else {
                            error();
                        }
                    });
                }else {
                    idCardIdentification.IdCardOcr(file.getPath(),IdCardIdentification.TYPE_NOT_POSITIVE,jsonObject -> {
                        showLoad();
                        if (!jsonObject.isNull("errorcode")) {
                            camera.startPreview();
                            IdCardNInfoEntity idCardNInfoEntity = GsonUtil.fromJson(jsonObject.toString(),new TypeToken<IdCardNInfoEntity>(){}.getType());
                            if(idCardNInfoEntity.errorcode == 0){
                                idCardNInfoEntity.backimage = file.getPath();

                                CacheUtils.getInstance().put(IdCardCameraActivity.IMAGE_N_PATH,file.getPath());

                                Intent intent = new Intent();
                                intent.putExtra(IntentBuilder.KEY_DATA, idCardNInfoEntity);
                                setResult(RESULT_OK, intent);
                                ToastUtils.showLong(getApplicationContext(),"识别成功");
                                finish();
                            }else {
                                error();
                            }
                        } else {
                            error();
                        }
                    });
                }


                // 在拍照的时候相机是被占用的,拍照之后需要重新预览
                //跳到新的页面

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 监听surfaceview的创建
     *
     * @author Administrator
     *         Surfaceview只有当activity显示到前台，该空间才会被创建
     */
    private final class MySurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {

                // 当surfaceview创建就去打开相机

                try {
                    camera = Camera.open();
                    Camera.Parameters params = camera.getParameters();
                    List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
                    Camera.Size optionSize = getOptimalPreviewSize(sizeList, surfaceview.getWidth(), surfaceview.getHeight());
                    params.setPreviewSize(optionSize.width,optionSize.height);//把camera.size赋值到parameters
                    params.setJpegQuality(80);  // 设置照片的质量


                    params.setPictureSize(optionSize.width, optionSize.height);
                    camera.setParameters(params); // 将参数设置给相机
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //右旋90度，将预览调正
                //camera.setDisplayOrientation(90);
                // 设置预览显示
                camera.setPreviewDisplay(surfaceview.getHolder());
                // 开启预览
                camera.startPreview();

            } catch (Exception e) {
                DialogUtils.createDialogWithLeft(context, "缺少相机权限请去 设置->授权管理—>应用权限管理",sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();
                    finish();
                });
            }

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //对焦
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                camera.cancelAutoFocus();
            }
        });

        return super.onTouchEvent(event);
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void showLoad(){
        if (mLoadingSweetAlertDialog != null && mLoadingSweetAlertDialog.isShowing())
            mLoadingSweetAlertDialog.dismiss();
        else {
            mLoadingSweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mLoadingSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mLoadingSweetAlertDialog.setCancelable(true);
            mLoadingSweetAlertDialog.setTitleText("正在识别。。。");
            mLoadingSweetAlertDialog.show();
        }

    }

    private void error(){
        dialogPrompt = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        dialogPrompt.setCancelable(false);
        dialogPrompt.setTitleText("失败")
                .setContentText("请在蓝色框里拍照").
                setConfirmText(getString(R.string.btn_confirm)).show();
    }

}


