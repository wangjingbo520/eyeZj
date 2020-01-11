package com.l.eyescure.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbPatientEntity;

import java.util.List;

/**
 * Created by Ken on 2015/1/21.
 * 病人列表
 */
public class CureListAdapter extends BaseAdapter{

    private List<DbPatientEntity> list;
    private LayoutInflater mInflater;
    private int selectItem = -1;

    public CureListAdapter(Context context, List<DbPatientEntity> list) {
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
        DbPatientEntity userEntity = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_cure_item, null);
            viewHolder.numberTv = (TextView) convertView.findViewById(R.id.item_number);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.sexTv = (TextView) convertView.findViewById(R.id.item_sex);
            viewHolder.birthdayTv = (TextView) convertView.findViewById(R.id.item_birthday);
            viewHolder.itemBg = (LinearLayout) convertView.findViewById(R.id.item_bg);
            viewHolder.isCureTv = (TextView) convertView.findViewById(R.id.item_is_cure);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.numberTv.setText(userEntity.getNumber());
        viewHolder.nameTv.setText(userEntity.getName());
        viewHolder.sexTv.setText(userEntity.getSex());
        viewHolder.birthdayTv.setText(userEntity.getBirthday());
//        viewHolder.cureDateTv.setText(userEntity.getCureDate()+"");
        viewHolder.isCureTv.setText(userEntity.getCureNumber() +"");
//        if (selectItem == position) {
//            viewHolder.itemBg.setBackgroundColor(Color.rgb(230,130,21));
//        } else {
//            viewHolder.itemBg.setBackgroundColor(Color.TRANSPARENT);
//        }

        return convertView;
    }

    public static class ViewHolder {
        public LinearLayout itemBg;
        public TextView numberTv;
        public TextView nameTv;
        public TextView sexTv;
        public TextView birthdayTv;
        public TextView isCureTv;
    }
}
