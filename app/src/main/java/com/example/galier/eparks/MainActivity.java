package com.example.galier.eparks;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.github.kevin.library.AnimPath;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    FloatingActionButton fab;
    private CardView cardView_a, cardView_b;
    private ImageView carStart, carEnd, pressView;
    private LinearLayout loading, loadingOk, loadingError,loadingTip;
    private long mExitTime;
    private Socket socketclient = null;
    private static boolean isNetworkValidated;
    public static EZUIPlayer mPlayer;
    public Application application = MainActivity.this.getApplication();
    String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestPermission();
        initView();
    }

    public void initView() {
        cardView_a = findViewById(R.id.cardView_a);
        cardView_b = findViewById(R.id.cardView_b);
        carStart = findViewById(R.id.car_start);
        carEnd = findViewById(R.id.car_end);
        pressView = findViewById(R.id.pressview);
        loading = findViewById(R.id.loading);
        loadingOk = findViewById(R.id.loadingOk);
        loadingError = findViewById(R.id.loadingError);
        loadingTip = findViewById(R.id.loadTip);
        fab = findViewById(R.id.fab);
        cardView_a.setOnClickListener(cardViewOnClickListener);
        cardView_b.setOnClickListener(cardViewOnClickListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        connectServerWithSocket();
                    }
                }.start();
            }
        });


        carStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });


        EZUIKit.initWithAppKey(getApplication(), "bacf0478454a4f7eaedf9e0bb946055b");
        EZUIKit.setAccessToken("at.ddy4gvp544swgskhac2l8a9abaernce0-7eek1xhnul-0w5ydxu-70kpdv75a");
//        EZUIPlayer();

    }
    public void requestPermission() {
        //动态获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
            }
        }
    }

    private void dialog(){

//        Dialog dialog = new Dialog(this,R.style.Theme_AppCompat_Dialog);
        CommonDialog dialog = new CommonDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉dialog的标题
//        dialog.setContentView(R.layout.dialog);
//        ((ViewGroup)linePlayer.getParent()).removeView(linePlayer);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
////        dialog.addContentView(linePlayer,layoutParams);
//
//        dialog.setContentView(linePlayer,layoutParams);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mPlayer.stopPlay();
//                linePlayer.setVisibility(View.GONE);
                Log.e("MainActivity","stopPlay");
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
//                mPlayer.startPlay();
//                linePlayer.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();
//        EZUIPlayer();

    }

//
    public void EZUIPlayer(){
        //获取EZUIPlayer实例
        mPlayer = (EZUIPlayer) findViewById(R.id.player_ui);

//        mPlayer.setSurfaceSize(500, 400);

        mPlayer.setLoadingView(initProgressBar());

        //初始化EZUIKit
        EZUIKit.initWithAppKey(getApplication(), "");

        //设置授权token
        EZUIKit.setAccessToken("at.ddy4gvp544swgskhac2l8a9abaernce0-7eek1xhnul-0w5ydxu-70kpdv75a");

        //设置播放回调callback
        mPlayer.setCallBack(callBack);

        //设置播放参数
        mPlayer.setUrl("ezopen://open.ys7.com/C10647386/1.live");

        //开始播放
//        mPlayer.startPlay();
    }
    public View initProgressBar() {
        ProgressBar  mLoadView = new ProgressBar(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mLoadView.setLayoutParams(lp);
        return mLoadView;
    }

    public EZUIPlayer.EZUIPlayerCallBack callBack = new EZUIPlayer.EZUIPlayerCallBack(){
        public void onPlayFinish(){
            Log.e("MainActivity","EZUIPlayerCallBack: onPlayFinish");
        }
        public void onVideoSizeChange(int var1, int var2){
            Log.d("MainActivity","onVideoSizeChange  width = "+var1+"   height = "+var2);
        }
        @Override
        public void onPlayFail(EZUIError ezuiError) {
            Log.e("MainActivity","EZUIPlayerCallBack: onPlayFail"+ezuiError.getErrorString());
        }
        @Override
        public void onPlaySuccess() {
            Log.e("MainActivity","EZUIPlayerCallBack: onPlaySuccess");
        }
        @Override
        public void onPlayTime(Calendar calendar) {
            Log.d("MainActivity","onPlayTime calendar = "+calendar.getTime().toString());
        }
        @Override
        public void onPrepared() {
//            mPlayer.startPlay();
            Log.e("MainActivity","EZUIPlayerCallBack: onPrepared");
        }
    };



    private void newPaint() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10.0f);
        paint.setAntiAlias(true);
        path.lineTo(200, 200);
        path.lineTo(200, 400);
        canvas.drawPath(path, paint);
        canvas.drawPoint(100, 100, paint);
    }


    public void onPress(View view) {
        AnimPath animPath = new AnimPath();
        animPath.moveTo(0, 0);//起始点位置（相对坐标）
        animPath.lineTo(1000, 0);
        animPath.lineTo(1000, -720);
        animPath.lineTo(512, -720);
        animPath.lineTo(512, -594);
        animPath.startAnimation(view, 10000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        new Thread() {
            @Override
            public void run() {
                isNetworkValidated();
            }
        }.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        mPlayer.releasePlayer();
    }

    private boolean isNetworkValidated() {
        Runtime runtime = Runtime.getRuntime();
        int ret = 1;
        try {
            Process p = runtime.exec("ping -c 3 www.baidu.com");
            ret = p.waitFor();
            Log.e(TAG, "Process:" + ret);
            if (ret == 0) {
                showSnackBar(fab, "网络已连接");
                loadingError.setVisibility(View.GONE);
                return true;
            } else {
                showSnackBar(fab, "网络没有连接，请重试");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    Handler handler = new Handler() {
        Runnable runnable;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                loading.setVisibility(View.GONE);
                loadingOk.setVisibility(View.GONE);
                loadingError.setVisibility(View.VISIBLE);//loadingError
                loadingTip.setVisibility(View.GONE);
                setFlickerAnimation(carStart, false);
                setFlickerAnimation(carEnd, false);
                carStart.setImageResource(R.drawable.ic_car2);
                carEnd.setImageResource(R.drawable.ic_car3);
                handler.removeCallbacks(runnable);
                pressView.setVisibility(View.GONE);
                fab.show();
            } else if (msg.what == 101) {
                loading.setVisibility(View.VISIBLE);//loading
                loadingOk.setVisibility(View.GONE);
                loadingError.setVisibility(View.GONE);
                loadingTip.setVisibility(View.GONE);
                pressView.setVisibility(View.VISIBLE);
                setFlickerAnimation(carStart, true);
                setFlickerAnimation(carEnd, true);
                showSnackBar(fab, "准备调度，请等待");
                carStart.setImageResource(R.drawable.ic_car2);
                carEnd.setImageResource(R.drawable.ic_car3);
                fab.hide();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(103);
                        handler.postDelayed(this, 10000);
                    }
                };
                new Thread(runnable).start();

            } else if (msg.what == 103) {
                onPress(pressView);

            } else if (msg.what == 102) {
                loading.setVisibility(View.GONE);//loadingOk
                loadingOk.setVisibility(View.VISIBLE);
                loadingError.setVisibility(View.GONE);
                loadingTip.setVisibility(View.GONE);
                setFlickerAnimation(carStart, false);
                setFlickerAnimation(carEnd, false);
                carStart.setImageResource(R.drawable.ic_car3);
                carEnd.setImageResource(R.drawable.ic_car2);
                handler.removeCallbacks(runnable);
                pressView.setVisibility(View.GONE);
                fab.show();
            }
        }
    };

    public View.OnClickListener cardViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cardView_a) {
                showSnackBar(v, "停车场A（空闲）");
            } else if (v.getId() == R.id.cardView_b) {
                showSnackBar(v, "停车场B（拥挤）");
            }
        }
    };

    private void setFlickerAnimation(ImageView iv_sample, boolean flag) {
        if (flag) {
            final Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(600);//闪烁时间间隔
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setRepeatCount(Animation.INFINITE);//设置重复次数
            animation.setRepeatMode(Animation.REVERSE);//设置完动画的model后启动动画
            iv_sample.startAnimation(animation);//iv_sample.setAnimation(animation)
        } else if (!flag) {
            iv_sample.clearAnimation();//清除
        }

    }

    private void connectServerWithSocket() {
        try {
            //61.191.217.247:8899
            socketclient = new Socket("61.191.217.247", 8899);
            if (socketclient.isConnected()) {
                handler.sendEmptyMessage(101);
            }
//
            String socketData = "";
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socketclient.getOutputStream()));
            writer.write(socketData.replace("\n", " ") + "\n");
            writer.flush();

            while (true) {
                InputStream inputStream = socketclient.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                String sb = null;
                while ((len = inputStream.read(bytes)) != -1) {
                    sb = new String(bytes, 0, len, "UTF-8");
                    if (sb != null) {
                        handler.sendEmptyMessage(102);
                    }
                    Log.e(TAG, "get message from server: " + sb);
                    break;
                }
                if (socketclient != null && sb == null) {
                    handler.sendEmptyMessage(100);//连接出错判断
                }
                if (socketclient != null) {
                    socketclient.close();
                    Log.e(TAG, "socketclient.close()");
                }
                break;
            }


        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(100);
            showSnackBar(fab, e.toString());
        }
    }

    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    //再按一次退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void showCustomizeDialog() {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.dialog_customize,null);
        customizeDialog.setTitle("设置目的地经纬度：");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText edit_text =
                                (EditText) dialogView.findViewById(R.id.edit_text);
                        Toast.makeText(MainActivity.this,
                                edit_text.getText().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        customizeDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showCustomizeDialog();
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void showSnackBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(" ", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        //内容
        TextView tvSnackbarText = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        tvSnackbarText.setTextColor(Color.parseColor("#D81B60"));
        //onClick
        snackbar.getView().setBackgroundColor(Color.parseColor("#ffffff"));
        snackbar.setActionTextColor(Color.parseColor("#FF152B38"));
        snackbar.show();
    }
}
