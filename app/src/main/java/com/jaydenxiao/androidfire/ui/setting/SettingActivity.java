package com.jaydenxiao.androidfire.ui.setting;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baseapp.AppManager;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by xtt on 2017/10/26.
 */

public class SettingActivity extends BaseActivity{
    SettingService settingService=null;
    @Bind(R.id.Et_ip)
    EditText Et_ip;
    @Bind(R.id.Et_port)
    EditText Et_port;
    @Bind(R.id.Btn_setting_confirm)
    Button Btn_setting_confirm;
    @Bind(R.id.Btn_setting_cancle)
    Button Btn_setting_cancle;
    @OnClick(R.id.Btn_setting_cancle)
    public void cancle(){
        finish();
    }
    @OnClick(R.id.Btn_setting_confirm)
    public void confirm(){
        if(TextUtils.isEmpty(Et_ip.getText().toString())){
            showShortToast("请输入ip地址");
            return;
        }
        if(TextUtils.isEmpty(Et_port.getText().toString())){
            showShortToast("请输入端口");
            return;
    }
        String ip=Et_ip.getText().toString();
        String port=Et_port.getText().toString();
        ZzxFaceApplication.commonApplication.settingService.addConfig(SettingService.IP,ip);
        ZzxFaceApplication.commonApplication.settingService.addConfig(SettingService.PORT,port);
        AppManager.getAppManager().finishAllActivity();
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        Et_ip.setText(ZzxFaceApplication.commonApplication.settingService.getConfig(SettingService.IP));
        Et_ip.setSelection(Et_ip.getText().toString().length());
        Et_port.setText(ZzxFaceApplication.commonApplication.settingService.getConfig(SettingService.PORT));
        Et_port.setSelection(Et_port.getText().toString().length());
    }
}
