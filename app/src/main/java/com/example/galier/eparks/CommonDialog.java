package com.example.galier.eparks;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;

import java.util.Calendar;


/**
 * description:自定义dialog
 */

public class CommonDialog extends Dialog {
    private Context context;
//    private static Application application =new MainActivity().getApplication();

    public CommonDialog(Context context) {
        super(context, R.style.Theme_AppCompat_Dialog);
        this.context = context;
    }

    private LinearLayout linearLayout = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(true);
        //初始化界面控件
        initView();
        //初始化界面数据
        refreshView();
    }


    /**
     * 初始化界面控件的显示数据
     */
    private void refreshView() {
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        linearLayout = findViewById(R.id.line1);
        MainActivity.mPlayer = (EZUIPlayer) findViewById(R.id.player_ui);

//        mPlayer.setSurfaceSize(500, 400);
        MainActivity.mPlayer.setLoadingView(initProgressBar());

//        EZUIKit.initWithAppKey(new MainActivity().getApplication(), "bacf0478454a4f7eaedf9e0bb946055b");
//        EZUIKit.setAccessToken("at.ddy4gvp544swgskhac2l8a9abaernce0-7eek1xhnul-0w5ydxu-70kpdv75a");

        //设置播放回调callback
        MainActivity.mPlayer.setCallBack(callBack);

        //设置播放参数
        MainActivity.mPlayer.setUrl("ezopen://open.ys7.com/C10647386/1.live");
    }

    private View initProgressBar() {
        ProgressBar mLoadView = new ProgressBar(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mLoadView.setLayoutParams(lp);
        return mLoadView;
    }

    private EZUIPlayer.EZUIPlayerCallBack callBack = new EZUIPlayer.EZUIPlayerCallBack() {
        public void onPlayFinish() {
            Log.e("MainActivity", "EZUIPlayerCallBack: onPlayFinish");
        }

        public void onVideoSizeChange(int var1, int var2) {
            Log.d("MainActivity", "onVideoSizeChange  width = " + var1 + "   height = " + var2);
        }

        @Override
        public void onPlayFail(EZUIError ezuiError) {
            Log.e("MainActivity", "EZUIPlayerCallBack: onPlayFail" + ezuiError.getErrorString());
        }

        @Override
        public void onPlaySuccess() {
            Log.e("MainActivity", "EZUIPlayerCallBack: onPlaySuccess");
        }

        @Override
        public void onPlayTime(Calendar calendar) {
            Log.d("MainActivity", "onPlayTime calendar = " + calendar.getTime().toString());
        }

        @Override
        public void onPrepared() {
            MainActivity.mPlayer.startPlay();
            Log.e("MainActivity", "EZUIPlayerCallBack: onPrepared");
        }
    };


}
