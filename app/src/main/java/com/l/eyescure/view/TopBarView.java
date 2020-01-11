package com.l.eyescure.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.l.eyescure.R;

/**
 * Created by Look on 2016/12/31.
 * 顶部栏
 */

public class TopBarView extends RelativeLayout{
    private Context mContext;
    private LinearLayout helpBtn,exitBtn,setBtn,fileBtn,cureBtn,printBtn,user_btn;
    private TextView helpTv,exitTv,setTv,fileTv,cureTv,printTv,userTv;
    private ImageView super_sign;

    public TopBarView(Context context,AttributeSet attrs) {
        super(context,attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.view_top, this);
        mContext = context;
        initMould();
    }

    private void initMould(){
        helpBtn = (LinearLayout) findViewById(R.id.help_btn);
        exitBtn = (LinearLayout) findViewById(R.id.exit_btn);
        setBtn = (LinearLayout) findViewById(R.id.set_btn);
        fileBtn = (LinearLayout) findViewById(R.id.file_btn);
        cureBtn = (LinearLayout) findViewById(R.id.cure_btn);
        printBtn = (LinearLayout) findViewById(R.id.print_btn);
        user_btn = (LinearLayout) findViewById(R.id.user_btn);

        helpTv = (TextView) findViewById(R.id.help_tv);
        exitTv = (TextView) findViewById(R.id.exit_tv);
        setTv = (TextView) findViewById(R.id.set_tv);
        fileTv = (TextView) findViewById(R.id.file_tv);
        cureTv = (TextView) findViewById(R.id.cure_tv);
        printTv = (TextView) findViewById(R.id.print_tv);
        userTv = (TextView) findViewById(R.id.top_user_tv);

        super_sign = (ImageView) findViewById(R.id.super_sign);
        setOnTouch();
    }

    public void setUserTv(String text,boolean bSuper){
        userTv.setText(text);
        if(bSuper){
            visiableBtn(6);
            userTv.setEnabled(true);
        }else{
            super_sign.setVisibility(GONE);
            userTv.setEnabled(false);
        }
    }

    public void setListeners(OnClickListener onClickListener) {
        helpBtn.setOnClickListener(onClickListener);
        exitBtn.setOnClickListener(onClickListener);
        setBtn.setOnClickListener(onClickListener);
        fileBtn.setOnClickListener(onClickListener);
        cureBtn.setOnClickListener(onClickListener);
        printBtn.setOnClickListener(onClickListener);
        user_btn.setOnClickListener(onClickListener);
    }

    private void setOnTouch(){
        helpBtn.setOnTouchListener(new btnOnTouch());
        exitBtn.setOnTouchListener(new btnOnTouch());
        setBtn.setOnTouchListener(new btnOnTouch());
        fileBtn.setOnTouchListener(new btnOnTouch());
        cureBtn.setOnTouchListener(new btnOnTouch());
        printBtn.setOnTouchListener(new btnOnTouch());
        user_btn.setOnTouchListener(new btnOnTouch());
    }

    private class btnOnTouch implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ){
                switch (v.getId()){
                    case R.id.help_btn:
                        helpTv.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case R.id.exit_btn:
                        exitTv.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case R.id.set_btn:
                        setTv.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case R.id.file_btn:
                        fileTv.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case R.id.cure_btn:
                        cureTv.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case R.id.print_btn:
                        printTv.setTextColor(getResources().getColor(R.color.gray));
                        break;
                }
            }else{
                helpTv.setTextColor(getResources().getColor(R.color.white));
                exitTv.setTextColor(getResources().getColor(R.color.white));
                setTv.setTextColor(getResources().getColor(R.color.white));
                fileTv.setTextColor(getResources().getColor(R.color.white));
                cureTv.setTextColor(getResources().getColor(R.color.white));
                printTv.setTextColor(getResources().getColor(R.color.white));
            }

            return false;
        }
    }

    /**
     * -1禁止全部点击 0帮助 1退出 2设置 3档案 4治疗 5查看打印
     * @param id
     */
    public void disableBtn(int id){
        if (id == -1){
            helpBtn.setEnabled(false);
            exitBtn.setEnabled(false);
            setBtn.setEnabled(false);
            fileBtn.setEnabled(false);
            cureBtn.setEnabled(false);
            printBtn.setEnabled(false);
            user_btn.setEnabled(false);
            return;
        }
        switch (id){
            case 0:
                helpBtn.setEnabled(false);
                break;
            case 1:
                exitBtn.setEnabled(false);
                break;
            case 2:
                setBtn.setEnabled(false);
                break;
            case 3:
                fileBtn.setEnabled(false);
                break;
            case 4:
                cureBtn.setEnabled(false);
                break;
            case 5:
                printBtn.setEnabled(false);
                break;
            case 6:
                user_btn.setEnabled(false);
                break;
        }
    }

    /**
     * -1 全部不显示 0帮助 1退出 2设置 3档案 4治疗 5查看打印
     * @param id
     */
    public void visiableBtn(int id){
        if (id == -1){
            helpBtn.setVisibility(INVISIBLE);
            exitBtn.setVisibility(INVISIBLE);
            setBtn.setVisibility(INVISIBLE);
            fileBtn.setVisibility(INVISIBLE);
            cureBtn.setVisibility(INVISIBLE);
            printBtn.setVisibility(INVISIBLE);
            super_sign.setVisibility(GONE);
            return;
        }
        switch (id){
            case 0:
                helpBtn.setVisibility(VISIBLE);
                break;
            case 1:
                exitBtn.setVisibility(VISIBLE);
                break;
            case 2:
                setBtn.setVisibility(VISIBLE);
                break;
            case 3:
                fileBtn.setVisibility(VISIBLE);
                break;
            case 4:
                cureBtn.setVisibility(VISIBLE);
                break;
            case 5:
                printBtn.setVisibility(VISIBLE);
                break;
            case 6:
                super_sign.setVisibility(VISIBLE);
                break;
        }
    }
}
