package com.l.eyescure.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.server.cureManage.CureStepInfo;

import java.util.List;

/**
 * Created by Ken on 2015/1/21.
 * 病人列表
 */
public class CureStepListAdapter extends BaseAdapter {

    private List<CureStepInfo> list;
    private LayoutInflater mInflater;
    private int selectItem = -1;

    public CureStepListAdapter(Context context, List<CureStepInfo> list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public int getCount() {
        if (list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        CureStepInfo msg = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_cure_step_item, null);
            viewHolder.stepMsg = (TextView) convertView.findViewById(R.id.cure_step_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String LorR_str = msg.getLorR();
        if (LorR_str.equals("L")) {
            viewHolder.stepMsg.setTextColor(convertView.getResources().getColor(R.color.blue));
        } else if (LorR_str.equals("R")) {
            viewHolder.stepMsg.setTextColor(convertView.getResources().getColor(R.color.black));
        } else if (LorR_str.equals("H")) {
            viewHolder.stepMsg.setTextColor(convertView.getResources().getColor(R.color.green));
        }
        viewHolder.stepMsg.setText(getStepMsg(msg));
        return convertView;
    }

    private String getStepMsg(CureStepInfo msg) {
        if (msg == null || TextUtils.isEmpty(msg.getLorR()) ||
                TextUtils.isEmpty(msg.getMode()) || TextUtils.isEmpty(msg.getTime())) {
            return "..........";
        }

        String LorR_str = msg.getLorR();
        boolean bEyesMsg = false;
        if (LorR_str.equals("L")) {
            LorR_str = "左眼";
            bEyesMsg = true;
        } else if (LorR_str.equals("R")) {
            LorR_str = "右眼";
            bEyesMsg = true;
        } else if (LorR_str.equals("H")) {
            bEyesMsg = false;
            LorR_str = "热敷眼罩";
            String Mode_str = msg.getMode();
            if (Mode_str.equals("L")) {
                String TimeStr = msg.getTime();
                if (TimeStr.equals("0000")) {
                    return LorR_str + "已连接。";
                }
                TimeStr = getTimeStr(TimeStr);
                return LorR_str + "在" + TimeStr + "的时候" + "再次连接。";
            } else if (Mode_str.equals("T")) {
                String TimeStr = msg.getTime();
                if (TimeStr.equals("0000")) {
                    return LorR_str + "已停止工作。";
                }
                TimeStr = getTimeStr(TimeStr);
                return LorR_str +"在治疗了" + TimeStr + "的时候"  + "停止治疗。";
            } else if (Mode_str.equals("G")) {
                String TimeStr = msg.getTime();
                if (TimeStr.equals("0000")) {
                    return LorR_str + "已开始治疗。";
                }
                TimeStr = getTimeStr(TimeStr);
                return LorR_str + "已经治疗" + TimeStr;
            } else if (Mode_str.equals("D")) {
                String TimeStr = msg.getTime();
                if (TimeStr.equals("0000")) {
                    return LorR_str + "已被拔出。";
                }
                TimeStr = getTimeStr(TimeStr);
                return LorR_str +"在治疗了" + TimeStr + "的时候" + "被拔出。";
            }
        }

        if (bEyesMsg) {
            String Mode_str = msg.getMode();
            if (Mode_str.equals("K")) {
                Mode_str = "开始";
                return LorR_str + Mode_str + "治疗。";
            } else if (Mode_str.equals("Z")) {
                Mode_str = "暂停";
                String TimeStr = msg.getTime();
                TimeStr = getTimeStr(TimeStr);
                return LorR_str + "在" + TimeStr + "的时候" + Mode_str + "了治疗。";
            } else if (Mode_str.equals("J")) {
                Mode_str = "继续";
                String TimeStr = msg.getTime();
                TimeStr = getTimeStr(TimeStr);
                return LorR_str + "在" + TimeStr + "的时候开始" + Mode_str + "治疗。";
            } else if (Mode_str.equals("T")) {
                Mode_str = "停止";
                String TimeStr = msg.getTime();
                TimeStr = getTimeStr(TimeStr);
                return LorR_str + "治疗在" + TimeStr + "的时候" + Mode_str + "。";
            }
        }
        return "..........";
    }

    private String getTimeStr(String Time_str) {
        String mTime_str;
        String Time_Min = Time_str.substring(0, 2);
        String Time_Sec = Time_str.substring(2, 4);
        mTime_str = Time_Min + "分" + Time_Sec + "秒";
        return mTime_str;
    }

    public static class ViewHolder {
        public TextView stepMsg;
    }
}
