package com.l.eyescure.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.l.eyescure.R;

/**
 * Created by Look on 2017/1/5.
 * 治疗按钮
 */

public class CureDialog {
    Dialog mDialog;
    RelativeLayout dialogExit;
    LinearLayout startCureBtn;
    LinearLayout printBtn;
    LinearLayout fileBtn;

    public Dialog creatDialog(Context context,View.OnClickListener onClickListener) {
        if (mDialog == null) {
            mDialog = new Dialog(context,R.style.dialog);
            mDialog.setContentView(R.layout.dialog_cure);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.getWindow().setGravity(Gravity.CENTER);
            View view = mDialog.getWindow().getDecorView();
            setListeners(view,onClickListener);
        }
        return mDialog;
    }

    public void setListeners(View view,View.OnClickListener onClickListener){
        dialogExit = (RelativeLayout) view.findViewById(R.id.dialog_exit);
        startCureBtn = (LinearLayout) view.findViewById(R.id.start_cure_btn);
        printBtn = (LinearLayout) view.findViewById(R.id.print_btn);
        fileBtn = (LinearLayout) view.findViewById(R.id.file_btn);

        dialogExit.setOnClickListener(onClickListener);
        startCureBtn.setOnClickListener(onClickListener);
        printBtn.setOnClickListener(onClickListener);
        fileBtn.setOnClickListener(onClickListener);
    }

    public void cancel(){
        if(mDialog!=null){
            mDialog.cancel();
        }
    }
}
