package com.l.eyescure.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbCureDetailEntity;
import com.l.eyescure.util.DateUtil;

import java.util.List;

/**
 * Created by Ken on 2015/1/21.
 * 病人详情列表
 */
public class CureDetailListAdapter extends BaseAdapter {

    private List<DbCureDetailEntity> list;
    private LayoutInflater mInflater;

    public CureDetailListAdapter(Context context, List<DbCureDetailEntity> list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list != null) {
            if (list.size() > 0) {
                return list.size();
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null)
            return list.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        DbCureDetailEntity userEntity = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_cure_detail_item, null);
            viewHolder.numberTv = (TextView) convertView.findViewById(R.id.item_number);
            viewHolder.cureNumberTv = (TextView) convertView.findViewById(R.id.item_cure_number);
            viewHolder.cureNameTv = (TextView) convertView.findViewById(R.id.item_cure_name);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.leftTimeTv = (TextView) convertView.findViewById(R.id.item_left_time);
            viewHolder.rightTimeTv = (TextView) convertView.findViewById(R.id.item_right_time);
            viewHolder.cureDateTv = (TextView) convertView.findViewById(R.id.item_cure_date);
            viewHolder.isCureTv = (TextView) convertView.findViewById(R.id.item_is_cure);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //  viewHolder.numberTv.setText(userEntity.getCureForNumber()+"");
//        viewHolder.nameTv.setText(userEntity.getName());
        viewHolder.leftTimeTv.setText(DateUtil.parseSencond(userEntity.getLeftTime()));
        viewHolder.rightTimeTv.setText(DateUtil.parseSencond(userEntity.getRightTime()));
        viewHolder.cureNumberTv.setText(String.valueOf(position + 1));
        viewHolder.cureDateTv.setText(userEntity.getCureDate());
        viewHolder.cureNameTv.setText(userEntity.getDoctorName());
//        viewHolder.cureDateTv.setText(userEntity.getCureDate()+"");
        //     viewHolder.isCureTv.setText(userEntity.isStartCure()?"是":"否");

        return convertView;
    }

    public static class ViewHolder {
        public TextView cureNumberTv;
        public TextView numberTv;
        public TextView nameTv;
        public TextView leftTimeTv;
        public TextView rightTimeTv;
        public TextView isCureTv;
        public TextView cureDateTv;
        public TextView cureNameTv;
    }
}
