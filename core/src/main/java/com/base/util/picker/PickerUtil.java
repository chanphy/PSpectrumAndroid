package com.base.util.picker;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.base.http.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by Zhu TingYu on 2018/4/4.
 */

public class PickerUtil {

    /**
     * 时间选择器 返回年月日
     *
     * @param activity
     * @param time     默认显示时间
     * @param listener
     */

    public static void showTimePicker(Activity activity, long time, DatePickerDialog.OnDateSetListener listener) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date(time));
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                listener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    public static void showTimePickYMD(AppCompatActivity activity, SelectTimeHaveHMSDialog.OnTimeSelectListener onTimeSelectListener){
        SelectTimeHaveHMSDialog selectTimeHaveHMSDialog = new SelectTimeHaveHMSDialog();
        selectTimeHaveHMSDialog.setOnTimeSelectListener(onTimeSelectListener);
        selectTimeHaveHMSDialog.show(activity.getSupportFragmentManager());
    }


    /**
     * 时间选择器 返回年月日
     *
     * @param activity
     * @param listener
     */

    public static void showTimeYMD(Activity activity, long startTimeStamp, DatePicker.OnYearMonthDayPickListener listener) {

        try {
            Date endTime = new Date(startTimeStamp);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endTime);

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(endTime);
            startCalendar.add(Calendar.YEAR, -10);

            final DatePicker picker = new DatePicker(activity);
            picker.setCanceledOnTouchOutside(true);
            picker.setUseWeight(true);
            picker.setTopPadding(ConvertUtils.toPx(activity, 10));
            picker.setRangeStart(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH));
            picker.setRangeEnd(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1 , endCalendar.get(Calendar.DAY_OF_MONTH));
            picker.setSelectedItem(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DAY_OF_MONTH));
            picker.setResetWhileWheel(false);
            picker.setOnDatePickListener(listener);
            picker.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 时间选择器 返回年月日时分秒
     *
     * @param activity
     * @param startTimeStamp 选择的上限
     * @param endTimeStamp   选择的的下限
     * @param isShowEndTime  是否默认显示结束时间
     */

    public static void showTimePickerHaveMS(Activity activity, long startTimeStamp, long endTimeStamp, boolean isShowEndTime, DateTimePicker.OnYearMonthDayTimePickListener listener) {
        DateTimePicker picker = new DateTimePicker(activity, DateTimePicker.HOUR_24);
        Date startTime = new Date(startTimeStamp);
        Date endTime = new Date(endTimeStamp);


        Calendar startCalendar = Calendar.getInstance();

        startCalendar.setTime(startTime);

        Calendar endCalendar = Calendar.getInstance();

        endCalendar.setTime(endTime);

        picker.setDateRangeStart(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH));
        picker.setDateRangeEnd(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DAY_OF_MONTH));
        picker.setTimeRangeStart(0, 0);
        picker.setTimeRangeEnd(23, 59);
        if (isShowEndTime) {
            picker.setSelectedItem(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
        }
        picker.setTopLineColor(activity.getResources().getColor(R.color.colorPrimary));
        picker.setLabelTextColor(activity.getResources().getColor(R.color.colorPrimary));
        picker.setDividerColor(activity.getResources().getColor(R.color.colorPrimary));
        picker.setOnDateTimePickListener(listener);
        picker.show();
    }

    /**
     * 单项选择器
     *
     * @param activity
     * @param data
     * @param defaultPosition 默认显示选项
     * @param listener
     */

    public static void showItemPicker(Activity activity, List<String> data, int defaultPosition, OptionPicker.OnOptionPickListener listener) {
        OptionPicker picker = new OptionPicker(activity, data);
        picker.setCanceledOnTouchOutside(false);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setSelectedIndex(defaultPosition);
        picker.setCycleDisable(true);
        picker.setTextSize(16);
        picker.setOnOptionPickListener(listener);
        picker.show();
    }

    /**
     * 地址选择器
     *
     * @param activity
     * @param callback
     */
    public static void onAddress3Picker(Activity activity, AddressPickTask.Callback callback) {
        AddressPickTask task = new AddressPickTask(activity);
        task.setCallback(callback);
        task.execute("四川", "阿坝");
    }
}
