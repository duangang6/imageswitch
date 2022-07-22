package com.example.tupianswitch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List images = new ArrayList();
    ImageSwitcher mswitch;
    ViewGroup viewgroup;
    int position;
    float downX;
    ImageView[] tips;
    boolean isRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewgroup = findViewById(R.id.xiaoyuandian);
        mswitch = findViewById(R.id.imageswitch);

        initData();
        initPointer();
        mswitch.setFactory(switchFactory);
        mswitch.setOnTouchListener(switchTouch);
        new Thread(new Runnable() {
            //自动切换的进程
            @Override
            public void run() {
                isRunning = true;

                while(isRunning){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int cur_item=position;
                            cur_item=(cur_item+1)%images.size();
                            mswitch.setImageResource((Integer) images.get(cur_item));
                            setTips(cur_item);
                            position=cur_item;
                        }
                    });
                }
            }
        }).start();
    }

    private void initData() {
        //创建图片链表
        images.add(R.drawable.test1);
        images.add(R.drawable.test2);
        images.add(R.drawable.test3);
    };
    private ViewSwitcher.ViewFactory switchFactory = new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource((Integer) images.get(position));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
    };
    private View.OnTouchListener switchTouch = new View.OnTouchListener() {
        //动作间厅
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float lastX = motionEvent.getX();
                    //抬起的时候的X坐标大于按下的时候就显示上一张图片
                    if (lastX > downX){
                        if (position > 0){
                            //设置动画
                            mswitch.setInAnimation( AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_in_left));
                            mswitch.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_out_right));
                            position --;
                            mswitch.setImageResource( (Integer) images.get( position % images.size() ) );
                            setTips(position);
                        }else {
                            Toast.makeText(getApplication(), "已经是第一张", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(position < images.size() - 1){
                            //右进左出安卓自带的没有，根据安卓自带的自己写
                            mswitch.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.slide_in_right ));
                            mswitch.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.slide_out_left ));
                            position ++ ;
                            mswitch.setImageResource( (Integer) images.get( position % images.size() ) );
                            setTips(position);
                        }else{
                            Toast.makeText(getApplication(), "到了最后一张", Toast.LENGTH_SHORT).show();
                        }
                    }

            }
            return true;
        }
    };
    private void initPointer() {
        tips = new ImageView[images.size()];

        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置控件的宽高
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(45, 45));
            //设置小圆点的间隔属性
            imageView.setPadding(20, 0, 20, 0);
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.zhishiqi0);
            } else {
                tips[i].setBackgroundResource(R.mipmap.zhishiqi1);
            }
            viewgroup.addView(tips[i]);
        }
    }

    void setTips(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i==selectItems){
                tips[i].setBackgroundResource(R.mipmap.zhishiqi0);
            }else{
                tips[i].setBackgroundResource(R.mipmap.zhishiqi1);
            }
        }
    }
}