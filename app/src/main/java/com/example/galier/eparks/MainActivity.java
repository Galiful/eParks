package com.example.galier.eparks;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    private CardView cardView_a, cardView_b;
    private ImageView carStart, carEnd;
    private LinearLayout loading, loadingOk, loadingError;
    private long mExitTime;
    private Socket socketclient = null;
    String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    public void initView() {
        cardView_a = findViewById(R.id.cardView_a);
        cardView_b = findViewById(R.id.cardView_b);
        carStart = findViewById(R.id.car_start);
        carEnd = findViewById(R.id.car_end);
        loading = findViewById(R.id.loading);
        loadingOk = findViewById(R.id.loadingOk);
        loadingError = findViewById(R.id.loadingError);
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


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                loading.setVisibility(View.GONE);
                loadingOk.setVisibility(View.GONE);
                loadingError.setVisibility(View.VISIBLE);//loadingError
                setFlickerAnimation(carStart, false);
                setFlickerAnimation(carEnd, false);
                carStart.setImageResource(R.drawable.ic_car2);
                carEnd.setImageResource(R.drawable.ic_car3);
                fab.show();
            } else if (msg.what == 101) {
                loading.setVisibility(View.VISIBLE);//loading
                loadingOk.setVisibility(View.GONE);
                loadingError.setVisibility(View.GONE);
                setFlickerAnimation(carStart, true);
                setFlickerAnimation(carEnd, true);
                showSnackBar(fab, "准备调度，请等待");
                carStart.setImageResource(R.drawable.ic_car2);
                carEnd.setImageResource(R.drawable.ic_car3);
                fab.hide();
            } else if (msg.what == 102) {
                loading.setVisibility(View.GONE);//loadingOk
                loadingOk.setVisibility(View.VISIBLE);
                loadingError.setVisibility(View.GONE);
                setFlickerAnimation(carStart, false);
                setFlickerAnimation(carEnd, false);
                carStart.setImageResource(R.drawable.ic_car3);
                carEnd.setImageResource(R.drawable.ic_car2);
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
            socketclient = new Socket("10.151.232.250", 8989);
            if (socketclient.isConnected()) {
                handler.sendEmptyMessage(101);
            }
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socketclient.getOutputStream()));
            String socketData = "YH CM 0 :" + "M1" + "|3|" + "111111"
                    + " 0\\r\\n";
            writer.write(socketData);
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
                if(socketclient != null){
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
            return true;
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
