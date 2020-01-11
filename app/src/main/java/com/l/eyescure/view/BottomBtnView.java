package com.l.eyescure.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.l.eyescure.R;

/**
 * Created by Look on 2017/1/6.
 * 底部栏按钮
 */

public class BottomBtnView extends RelativeLayout {

    Button midBtn;
    Button leftBtn;
    Button rightBtn;
    Button midStopBtn;
    Button leftStopBtn;
    Button rightStopBtn;
    Button midBtnAbove;
    Button leftBtnAbove;
    Button rightBtnAbove;
    Button midStopBtnAbove;
    Button leftStopBtnAbove;
    Button rightStopBtnAbove;

    public BottomBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.view_bottom, this);
        initMould();
    }

    private void initMould(){
        midBtn = (Button) findViewById(R.id.mid_btn);
        leftBtn = (Button) findViewById(R.id.left_btn);
        rightBtn = (Button) findViewById(R.id.right_btn);
        midStopBtn = (Button) findViewById(R.id.mid_stop_btn);
        leftStopBtn = (Button) findViewById(R.id.left_stop_btn);
        rightStopBtn = (Button) findViewById(R.id.right_stop_btn);

        midBtnAbove = (Button) findViewById(R.id.mid_btn_above);
        leftBtnAbove = (Button) findViewById(R.id.left_btn_above);
        rightBtnAbove = (Button) findViewById(R.id.right_btn_above);
        midStopBtnAbove = (Button) findViewById(R.id.mid_stop_btn_above);
        leftStopBtnAbove = (Button) findViewById(R.id.left_stop_btn_above);
        rightStopBtnAbove = (Button) findViewById(R.id.right_stop_btn_above);
    }

    public void setListeners(OnClickListener onClickListener) {
        midBtn.setOnClickListener(onClickListener);
        leftBtn.setOnClickListener(onClickListener);
        rightBtn.setOnClickListener(onClickListener);
        midStopBtn.setOnClickListener(onClickListener);
        leftStopBtn.setOnClickListener(onClickListener);
        rightStopBtn.setOnClickListener(onClickListener);
    }

    public void setAllEnabled(boolean enabled){
        if(enabled){
            midBtnAbove.setVisibility(INVISIBLE);
            leftBtnAbove.setVisibility(INVISIBLE);
            rightBtnAbove.setVisibility(INVISIBLE);
            midStopBtnAbove.setVisibility(INVISIBLE);
            leftStopBtnAbove.setVisibility(INVISIBLE);
            rightStopBtnAbove.setVisibility(INVISIBLE);
        }else {
            midBtnAbove.setVisibility(VISIBLE);
            leftBtnAbove.setVisibility(VISIBLE);
            rightBtnAbove.setVisibility(VISIBLE);
            midStopBtnAbove.setVisibility(VISIBLE);
            leftStopBtnAbove.setVisibility(VISIBLE);
            rightStopBtnAbove.setVisibility(VISIBLE);
        }
    }

    public void setMidEnabled(boolean enabled){
        if(!enabled){
            midBtnAbove.setVisibility(VISIBLE);
        }else{
            midBtnAbove.setVisibility(INVISIBLE);
        }
    }

    public void setLeftEnabled(boolean enabled){
        if(!enabled){
            leftBtn.setBackgroundResource(R.drawable.left_start_btn);
            leftBtnAbove.setVisibility(VISIBLE);
        }else{
            leftBtnAbove.setVisibility(INVISIBLE);
        }
    }

    public void setRightEnabled(boolean enabled){
        if(!enabled){
            rightBtn.setBackgroundResource(R.drawable.right_start_btn);
            rightBtnAbove.setVisibility(VISIBLE);
        }else{
            rightBtnAbove.setVisibility(INVISIBLE);
        }
    }

    public void setMidStopEnabled(boolean enabled){
        if(!enabled){
            midStopBtnAbove.setVisibility(VISIBLE);
        }else{
            midStopBtnAbove.setVisibility(INVISIBLE);
        }
    }

    public void setLeftStopEnabled(boolean enabled){
        if(!enabled){
            leftStopBtnAbove.setVisibility(VISIBLE);
        }else{
            leftStopBtnAbove.setVisibility(INVISIBLE);
        }
    }

    public void setRightStopEnabled(boolean enabled){
        if(!enabled){
            rightStopBtnAbove.setVisibility(VISIBLE);
        }else{
            rightStopBtnAbove.setVisibility(INVISIBLE);
        }
    }

    public void setLeftBg(int mode){
        if (mode == 0){
            leftBtn.setBackgroundResource(R.drawable.left_start_btn);
        }else if(mode == 1){
            leftBtn.setBackgroundResource(R.drawable.left_pause_btn);
        }else if(mode == 2){
            leftBtn.setBackgroundResource(R.drawable.left_continue_btn);
        }
    }
    public void setMidBg(int mode){
        if (mode == 0){
            midBtn.setBackgroundResource(R.drawable.mid_start_btn);
        }else if(mode == 1){
            midBtn.setBackgroundResource(R.drawable.mid_pause_btn);
        }else if(mode == 2){
            midBtn.setBackgroundResource(R.drawable.mid_continue_btn);
        }
    }
    public void setRightBg(int mode){
        if (mode == 0){
            rightBtn.setBackgroundResource(R.drawable.right_start_btn);
        }else if(mode == 1){
            rightBtn.setBackgroundResource(R.drawable.right_pause_btn);
        }else if(mode == 2){
            rightBtn.setBackgroundResource(R.drawable.right_continue_btn);
        }
    }

    public void resetAllBg(){
        leftBtn.setBackgroundResource(R.drawable.left_start_btn);
        rightBtn.setBackgroundResource(R.drawable.right_start_btn);
        midBtn.setBackgroundResource(R.drawable.mid_start_btn);
    }
}
