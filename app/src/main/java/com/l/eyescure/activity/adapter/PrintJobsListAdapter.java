package com.l.eyescure.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.l.eyescure.R;

import org.cups4j.PrintJobAttributes;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by wudi on 2016/11/23.
 */
public class PrintJobsListAdapter extends BaseAdapter {
    private Context context;
    private List<PrintJobAttributes> printJobsDataMsg;
    private LayoutInflater layoutInflater;

    public PrintJobsListAdapter(Context context, List<PrintJobAttributes> printJobsDataMsg) {
        this.context = context;
        this.printJobsDataMsg = printJobsDataMsg;
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        TextView print_jobs_name;
        TextView print_jobs_status;
        TextView print_jobs_creat_time;
        TextView print_jobs_user;
    }

    @Override
    public int getCount() {
        return printJobsDataMsg.size();
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
            convertView = layoutInflater.inflate(R.layout.print_jobs_list_item, null);
            holder.print_jobs_name = (TextView) convertView.findViewById(R.id.print_jobs_name);
            holder.print_jobs_status = (TextView) convertView.findViewById(R.id.print_jobs_status);
            holder.print_jobs_creat_time = (TextView) convertView.findViewById(R.id.print_jobs_creat_time);
            holder.print_jobs_user = (TextView) convertView.findViewById(R.id.print_jobs_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PrintJobAttributes jobInfo = printJobsDataMsg.get(position);
        String name = jobInfo.getJobName();
        holder.print_jobs_name.setText(name);

        String status = jobInfo.getJobState().toString();
        holder.print_jobs_status.setText(status);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String time = sdf.format(jobInfo.getJobCreateTime());
        holder.print_jobs_creat_time.setText(time);

        String user = jobInfo.getUserName();
        holder.print_jobs_user.setText(user);
        return convertView;
    }
}
