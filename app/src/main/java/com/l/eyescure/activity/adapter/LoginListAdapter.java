package com.l.eyescure.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.server.SqlManage.localsql.DbDoctorEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author:admin
 * Version: V1.0
 * Description: 输入描述
 * Date: 2017/4/28
 */

public class LoginListAdapter extends BaseAdapter {
    private List<DbDoctorEntity> list;
    private Context context;

    public LoginListAdapter(Context context, List<DbDoctorEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_login_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DbDoctorEntity entity = list.get(position);
        viewHolder.item_account.setText(entity.getAccount());
        viewHolder.itemName.setText(entity.getNick());
        String isAdmin = "否";
        if (entity.getAccount().equals("admins")) {
            isAdmin = "是";
        }
        viewHolder.itemIsadmin.setText(isAdmin);
        viewHolder.itemCreateTime.setText(entity.getCreate_date());

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.item_account)
        TextView item_account;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_create_time)
        TextView itemCreateTime;
        @BindView(R.id.item_isadmin)
        TextView itemIsadmin;
        @BindView(R.id.item_bg)
        LinearLayout itemBg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
