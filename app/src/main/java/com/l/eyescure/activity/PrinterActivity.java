package com.l.eyescure.activity;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.PrintJobsListAdapter;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.printManager.PrintManager;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import org.cups4j.PrintJobAttributes;
import org.cups4j.PrinterAttributes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.l.eyescure.application.MyApplication.TAG;

public class PrinterActivity extends BaseActivity {
    @BindView(R.id.print_jobs_list_view)
    ListView print_jobs_list_view;

    @BindView(R.id.printer_view_top)
    TopBarView viewTop;

    @BindView(R.id.printer_name)
    TextView printer_name;

    @BindView(R.id.printer_zhiliang)
    TextView printer_zhiliang;

    @BindView(R.id.printer_size)
    TextView printer_size;

    @BindView(R.id.printer_status)
    TextView printer_status;


    private PrintManager printManager;
    private PrinterAttributes printerInfo;
    private Handler mHandler;
    private List<PrintJobAttributes> allJobs = new ArrayList<>();
    private PrintJobsListAdapter mPrintJobsListAdapter;

    @Override
    protected int bindLayout() {
        return R.layout.activity_printer;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initView() {
        mHandler = new Handler();
        printManager = (PrintManager) ServerHelper.getInstance().getModule(ServerHelper.MODULE_ID_PRINT);
        assert (printManager != null);

        if (printManager != null)
            printManager.registerPrinterChangedCallback(printerCallback);

        mPrintJobsListAdapter = new PrintJobsListAdapter(this, allJobs);
        print_jobs_list_view.setAdapter(mPrintJobsListAdapter);

        print_jobs_list_view.setOnItemClickListener(printJobClick);

        setTopBarView(viewTop);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    AdapterView.OnItemClickListener printJobClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
            builder.setTitle("打印");
            builder.setMessage("您确定取消当前的打印任务吗？");
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PrintJobAttributes pjob = allJobs.get(position);
                    if (pjob != null)
                        cancelPrintJob(pjob);
                }
            });
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    };

    private void refreshPrintJobList(int pid) {
        boolean bFind = false;
        for (int i = 0; i < allJobs.size(); i++) {
            if (allJobs.get(i).getJobID() == pid) {
                allJobs.remove(i);
                bFind = true;
                break;
            }
        }

        if (bFind)
            mPrintJobsListAdapter.notifyDataSetChanged();
    }

    private void cancelPrintJob(PrintJobAttributes pjob) {
        final int pid = pjob.getJobID();
        getPrintManager().cancelPrintJob(pid, new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean bSuccess = (boolean) value;
                        if (bSuccess) {
                            Toast.makeText(PrinterActivity.this, "您已成功删除该打印任务!", Toast.LENGTH_SHORT).show();
                            refreshPrintJobList(pid);
                        } else {
                            Toast.makeText(PrinterActivity.this, "您删除打印任务失败!任务正在执行或者数据线未连接！", Toast.LENGTH_SHORT).show();
                            initData();
                        }
                    }
                });
            }

            @Override
            public void onErrEvent(final int errCode) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (errCode) {
                            case 4002: {
                                Log.e(TAG, "get cancel printer job err,errcode = " + errCode);
                                break;
                            }
                        }
                    }
                });
            }
        });
    }

    private INormalEventListener printerCallback = new INormalEventListener() {
        @Override
        public void onValueEvent(final int key, Object value) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (key) {
                        case 1: {
                            initData();
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public void onErrEvent(int errCode) {

        }
    };

    private void initData() {
	if(getPrintManager()!=null){
        getPrintManager().getPrinterAllJobs(new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        allJobs.clear();
                        if (value == null) {
                            Log.e(TAG, "Print jobs info get sucess,but get value is null");
                        } else {
                            List<PrintJobAttributes> arr = (List<PrintJobAttributes>) value;
                            for (int i = 0; i < arr.size(); i++) {
                                PrintJobAttributes pjob = arr.get(i);
                                if (pjob != null) {
                                    allJobs.add(pjob);
                                }
                            }
                            mPrintJobsListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onErrEvent(final int errCode) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (errCode) {
                            case 4002: {
                                Log.e(TAG, "get printer jobs ere,errcode = " + errCode);
                                break;
                            }
                        }
                    }
                });
            }
        });

        getPrintManager().queryDefaultPrinterInfo(new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        printerInfo = (PrinterAttributes) value;
                        printer_name.setText("名称：" + printerInfo.getPrinterName());
                        printer_zhiliang.setText("质量：标准");
                        printer_size.setText("尺寸：A4");
                        String status = printerInfo.getPrinterStateMessage();
                        if (TextUtils.isEmpty(status)) {
                            status = "空闲";
                        }
                        printer_status.setText("状态：" + status);
                    }
                });
            }

            @Override
            public void onErrEvent(int errCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.s("未能检测到打印机，请检测打印机或者连接数据线接口！");
                    }
                });
            }
        });
		}
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "长按打印任务项，可以取消未开始的打印任务。\n" +
                "正在进行中的打印任务无法取消。";
        showHelpDialog(showStr);
    }

    @Override
    protected boolean topExit() {
        finish();
        return false;
    }

    @Override
    protected void topSet() {
        setBtnClick();
        finish();
    }

    @Override
    protected void topCare() {
        ToastUtil.s("在进行系统设置的时候，不能进行治疗！");
    }

    @Override
    protected void topFile() {
        fileBtnClick();
    }

    @Override
    protected void topPrintf() {
        String userNum = util.readString(Constant.CUR_DETAIL_SAVE_NUM);
        if (!TextUtils.isEmpty(userNum)) {
            clickPritfBtn();
        } else {
            ToastUtil.s("你需要先选定一个病人，才能查看打印病例！");
        }
    }

    @Override
    protected void topUser() {
        if (getCureInfoManage().isSuperLogin()) {
            readyGo(AccountManagerActivity.class);
        }
    }

}
