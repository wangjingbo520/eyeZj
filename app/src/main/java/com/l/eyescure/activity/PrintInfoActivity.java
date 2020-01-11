package com.l.eyescure.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.PrinterListAdapter;
import com.l.eyescure.server.ServerHelper;
import com.l.eyescure.server.callBack.INormalEventListener;
import com.l.eyescure.server.printManager.PrintManager;
import com.l.eyescure.server.printManager.PrintUnit;
import com.l.eyescure.server.printManager.PrintersInfo;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.ToastUtil;
import com.l.eyescure.view.TopBarView;

import org.cups4j.PrinterAttributes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.l.eyescure.application.MyApplication.TAG;

public class PrintInfoActivity extends BaseActivity {
    @BindView(R.id.print_info_gv_list)
    GridView print_info_gv_list;
    @BindView(R.id.print_info_view_top)
    TopBarView viewTop;
    @BindView(R.id.print_page_edge_distance_left_edit)
    EditText print_page_edge_distance_left_edit;
    @BindView(R.id.print_page_edge_distance_right_edit)
    EditText print_page_edge_distance_right_edit;
    @BindView(R.id.print_page_edge_distance_top_edit)
    EditText print_page_edge_distance_top_edit;
    @BindView(R.id.print_page_edge_distance_bottom_edit)
    EditText print_page_edge_distance_bottom_edit;
    @BindView(R.id.print_show_page_bg)
    ImageView print_show_page_bg;

    private PrinterListAdapter mPrinterListAdapter;
    private List<PrintersInfo> allPrints = new ArrayList<>();
    private int curPostion = 0;
    private PrintManager printManager;
    private Handler mHandler;
    private int left, right, top, bottom;
    private boolean mSync = false;

    @Override
    protected int bindLayout() {
        return R.layout.activity_print_info;
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

        mPrinterListAdapter = new PrinterListAdapter(this, allPrints);
        print_info_gv_list.setAdapter(mPrinterListAdapter);

        print_info_gv_list.setOnItemClickListener(printerClick);

        curPostion = getCurPosition();
        print_info_gv_list.setSelection(curPostion);

        setTopBarView(viewTop);

        print_page_edge_distance_left_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSync) {
                    return;
                }
                mSync = true;
                String text = print_page_edge_distance_left_edit.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    left = Integer.valueOf(text);
                } else {
                    left = 0;
                }

                right = left;
                top = (210 - (297 - left * 2) * 21 / 32) / 2;
                if (top < 0)
                    top = 0;
                bottom = top;

                print_page_edge_distance_right_edit.setText("" + right);
                print_page_edge_distance_top_edit.setText("" + top);
                print_page_edge_distance_bottom_edit.setText("" + bottom);

                changeImageShow();
                mSync = false;
            }
        });

        print_page_edge_distance_right_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSync) {
                    return;
                }
                mSync = true;
                String text = print_page_edge_distance_right_edit.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    right = Integer.valueOf(text);
                } else {
                    right = 0;
                }

                left = right;
                top = (210 - (297 - left * 2) * 21 / 32) / 2;
                if (top < 0)
                    top = 0;
                bottom = top;

                print_page_edge_distance_left_edit.setText("" + left);
                print_page_edge_distance_top_edit.setText("" + top);
                print_page_edge_distance_bottom_edit.setText("" + bottom);
                changeImageShow();
                mSync = false;
            }
        });

        print_page_edge_distance_top_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSync) {
                    return;
                }
                mSync = true;
                String text = print_page_edge_distance_top_edit.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    top = Integer.valueOf(text);
                } else {
                    top = 0;
                }

                bottom = top;

                left = (297 - (210 - top * 2) * 32 / 21) / 2;
                if (left < 0)
                    left = 0;
                right = left;

                print_page_edge_distance_left_edit.setText("" + left);
                print_page_edge_distance_right_edit.setText("" + right);
                print_page_edge_distance_bottom_edit.setText("" + bottom);

                changeImageShow();
                mSync = false;
            }
        });

        print_page_edge_distance_bottom_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSync) {
                    return;
                }
                mSync = true;
                String text = print_page_edge_distance_bottom_edit.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    bottom = Integer.valueOf(text);
                } else {
                    bottom = 0;
                }

                top = bottom;

                left = (297 - (210 - top * 2) * 32 / 21) / 2;
                if (left < 0)
                    left = 0;
                right = left;

                print_page_edge_distance_left_edit.setText("" + left);
                print_page_edge_distance_right_edit.setText("" + right);
                print_page_edge_distance_top_edit.setText("" + top);
                changeImageShow();
                mSync = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void changeImageShow() {
        int mLeft = left;
        int mRight = right;
        int mTop = top;
        int mBottom = bottom;

        if (left < 3) {
            mLeft = 3;
            mRight = 3;
        }

        int mWidth = 297 - mLeft - mRight;
        if (mWidth > 297) {
            mWidth = 297;
        } else if (mWidth < 0) {
            mWidth = 0;
        }
        int mHeight = 210 - mTop - mBottom;
        if (mHeight > 210) {
            mHeight = 210;
        } else if (mHeight < 0) {
            mHeight = 0;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) print_show_page_bg.getLayoutParams();
        params.width = mWidth;
        params.height = mHeight;
        params.setMargins(mLeft, mTop, 0, 0);
        print_show_page_bg.setLayoutParams(params);

        PrintUnit printUnit = new PrintUnit(mLeft, mTop, mRight, mBottom);
        getStorageManage().setPrintEdgeValue(printUnit);
    }

    private void initData() {
	   if(getPrintManager()!=null){
        getPrintManager().queryDefaultPrinterInfo(new INormalEventListener() {
            @Override
            public void onValueEvent(int key, final Object value) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        allPrints.clear();
                        if (value == null) {
                            Log.e(TAG, "Printers info ger sucess,but get value is null");
                        } else {
                            PrinterAttributes arr = (PrinterAttributes) value;
                            Log.d(TAG, "  Name:" + arr.getPrinterName() +
                                    ", State:" + arr.getPrinterState() +
                                    ", Reasons:" + arr.getPrinterStateReasons() +
                                    ", Message:" + arr.getPrinterStateMessage() + "\n");
                            PrintersInfo printer = new PrintersInfo(arr.getPrinterName(), arr.getPrinterState(),
                                    arr.getPrinterStateReasons(), arr.getPrinterStateMessage(), true);
                            allPrints.add(printer);
                            mPrinterListAdapter.notifyDataSetChanged();
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
                                Log.e(TAG, "Printers info ger err,id = " + errCode);
                                break;
                            }
                        }
                    }
                });
            }
        });
		}

      if(getStorageManage()!=null){
        PrintUnit printUnit = getStorageManage().getPrintEdgeValue();
        if (printUnit == null) {
            left = Constant.PRINT_EDGE_LEFT;
            right = Constant.PRINT_EDGE_RIGHT;
            top = Constant.PRINT_EDGE_TOP;
            bottom = Constant.PRINT_EDGE_BOTTOM;
        } else {
            left = printUnit.getLeft();
            print_page_edge_distance_left_edit.setText(String.valueOf(left));
            right = printUnit.getRight();
            print_page_edge_distance_right_edit.setText(String.valueOf(right));
            top = printUnit.getTop();
            print_page_edge_distance_top_edit.setText(String.valueOf(top));
            bottom = printUnit.getBottom();
            print_page_edge_distance_bottom_edit.setText(String.valueOf(bottom));
        }
        changeImageShow();
		}
    }

    private INormalEventListener printerCallback = new INormalEventListener() {
        @Override
        public void onValueEvent(final int key, Object value) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (key) {
                        case 0: {


                            break;
                        }
                        case 1: {
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

    private int getCurPosition() {
        return 0;
    }

    AdapterView.OnItemClickListener printerClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PrintersInfo info = allPrints.get(position);
            if (info != null) {
                if (info.isChecked()) {
                    return;
                } else {
                    info.setChecked(true);
                    String name = info.getName();
                    for (int i = 0; i < allPrints.size(); i++) {
                        PrintersInfo info1 = allPrints.get(i);
                        if (info1 != null) {
                            String name1 = info.getName();
                            if (TextUtils.isEmpty(name)) {
                                if (!TextUtils.isEmpty(name1))
                                    info1.setChecked(false);
                            } else {
                                if (TextUtils.isEmpty(name1))
                                    info1.setChecked(false);
                                else {
                                    if (!name.equals(name1)) {
                                        info1.setChecked(false);
                                    }
                                }
                            }
                        }
                    }
                    mPrinterListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void clickPritfShowBtn() {
        Intent intent = new Intent(PrintInfoActivity.this, PrinterActivity.class);
        startActivity(intent);
    }

    @Override
    protected int topHelp() {
        showHelpDialog();
        return 0;
    }

    protected void showHelpDialog() {
        String showStr = "页面设置支持A4纸张尺寸，标准质量和横向打印。\n" +
                "页边距可以设置，数字单位是mm。可以在页边距预览中看到效果。请以实际打印效果为准。\n" +
                "设置页边距的时候，为了使图像不失真，当调整了一个距离的时候，会自动计算出其他最恰当的距离。请以实际打印效果为准。\n" +
                "如果有支持的打印机连接，可以在打印机下显示。\n" +
                "点击打印机图标下的“查看打印机详情”，可以进入打印机详情界面查看打印机属性和任务。";
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
