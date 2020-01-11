package com.l.eyescure.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.l.eyescure.R;
import com.l.eyescure.activity.PrintInfoActivity;
import com.l.eyescure.server.printManager.PrintersInfo;

import java.util.List;

/**
 * Created by wudi on 2016/11/23.
 */
public class PrinterListAdapter extends BaseAdapter {
    private Context context;
    private List<PrintersInfo> printersData;
    private LayoutInflater layoutInflater;

    public PrinterListAdapter(Context context, List<PrintersInfo> printersData) {
        this.context = context;
        this.printersData = printersData;
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        ImageView printer_sign;
        ImageView printer_check_sign;
        TextView name;
        TextView print_show;
    }

    @Override
    public int getCount() {
        return printersData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.printer_list_item, null);
            holder.printer_sign = (ImageView) convertView.findViewById(R.id.printer_sign);
            holder.printer_check_sign = (ImageView) convertView.findViewById(R.id.printer_check_sign);
            holder.name = (TextView) convertView.findViewById(R.id.printer_name);
            holder.print_show = (TextView) convertView.findViewById(R.id.print_show);

            convertView.setPadding(1, 1, 1, 1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PrintersInfo printInfo = printersData.get(position);

        String fileName = printInfo.getName();
        holder.name.setText(fileName);

        boolean bChecked = printInfo.isChecked();
        if (bChecked) {
            holder.printer_check_sign.setVisibility(View.VISIBLE);
        } else {
            holder.printer_check_sign.setVisibility(View.INVISIBLE);
        }

        holder.print_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PrintInfoActivity)context).clickPritfShowBtn();
            }
        });

        return convertView;
    }
}
