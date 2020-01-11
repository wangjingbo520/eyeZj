package com.l.eyescure.server.portManage;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.l.eyescure.application.MyApplication;
import com.l.eyescure.server.EyesModule;
import com.l.eyescure.server.callBack.IRecvMsgByServiceCallback;
import com.l.eyescure.util.Constant;
import com.l.eyescure.util.ReceiveBuffer;
import com.l.eyescure.util.SendDataArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPort;

public class MessageManage extends EyesModule {
    public static final String TAG = "eyesService";
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private static int MIN_MSG_LENGTH = 13;          //最小数据长度 一头一尾8 + 1头 + 2长度+ 2 校验
    private MsgHandler mHandler;
    private HeartbeatHandler mHeartbeatHandler;
    private InitPortHandler mInitPortHandler;
    private List<IRecvMsgByServiceCallback> recvMsgByServiceListeners = new ArrayList<IRecvMsgByServiceCallback>();
    private int sayHelloToMcuCnt = 5;
    private final long HEARTBEAT_CHECK_INTERVAL = 20000;
    private final long INIT_PORT_INTERVAL = 6000;

    public boolean isbInit() {
        return bInit;
    }

    private boolean bInit = false;
    private int curHeartNoAckCnt = 0;
    private LocalBroadcastManager broadcastManager;
    private ReceiveBuffer mReceiveBuffer = new ReceiveBuffer();
    public static final int AHEAD_LENGTH = 4;
    private int hotEyesMode = 0;
    private long hotEyesWorkTime = 0;

    public long getHotEyesWorkTime() {
        return hotEyesWorkTime;
    }

    @Override
    public void init(int step) {
        switch (step) {
            case 0: {
                init();
                break;
            }

        }
    }

    @Override
    public void done(int step) {
        //MyApplication.getInstance().showToastStr("关闭串口");
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        if (mOutputStream != null)
            mOutputStream = null;
        if (mInputStream != null)
            mInputStream = null;
    }

    public void init() {
        if (mHandler == null)
            mHandler = new MsgHandler();
        if (mInitPortHandler == null)
            mInitPortHandler = new InitPortHandler();
        if (mHeartbeatHandler == null)
            mHeartbeatHandler = new HeartbeatHandler();

        if (!bInit)
            portInit();
        if (broadcastManager == null)
            broadcastManager = LocalBroadcastManager.getInstance(mContext);

    }

    public void resetPort(){
       // mHandler.sendEmptyMessage(200);
    }

    public void registerMsgRecvByServiceCallback(IRecvMsgByServiceCallback listener) {
        if (listener == null) {
            return;
        }
        if (!recvMsgByServiceListeners.contains(listener)) {
            recvMsgByServiceListeners.add(listener);
        }
    }

    public void unregisterMsgRecvByServiceCallback(IRecvMsgByServiceCallback listener) {
        if (listener == null) {
            return;
        }
        if (recvMsgByServiceListeners.contains(listener)) {
            recvMsgByServiceListeners.remove(listener);
        }
    }

    private void notifyMsgProcvServiceMsgListener(int msgKey, byte[] data) {
        if (recvMsgByServiceListeners.size() == 0)
            return;
        for (IRecvMsgByServiceCallback listener : recvMsgByServiceListeners) {
            listener.onReceive(msgKey, data);
        }
    }

    private void notifyMsgProcvServiceMsgErrListener(String msgKey, String info, int err_ID) {
        if (recvMsgByServiceListeners.size() == 0)
            return;
        for (IRecvMsgByServiceCallback listener : recvMsgByServiceListeners) {
            listener.onReceiveErr(msgKey, info, err_ID);
        }
    }

    private class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x21: {   //握手
                    bInit = true;
                    mHeartbeatHandler.sendEmptyMessage(1);

                    broadcastManager.sendBroadcast(new Intent(Constant.ACTION_PORT_INIT_OK));
                    break;
                }
                case 0x23: {    //心跳
                    curHeartNoAckCnt = 0;
                    break;
                }
                case 0x14: {    //热敷眼罩工作提示
                    final byte[] content = (byte[]) msg.obj;
                    if (content.length < 1)
                        break;
                    notifyMsgProcvServiceMsgListener(msg.what, (byte[]) msg.obj);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if ((content[0] & 0xFF) == 0x01) {
                                hotEyesWorkTime = 0;
                                if (hotEyesMode == 0) {
                                    MyApplication.getInstance().showToastStr("热敷眼罩已连接");
                                } else if (hotEyesMode == 2) {
                                    MyApplication.getInstance().showToastStr("热敷治疗已完成，请进行下一步治疗！");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (int i = 0; i < 2; i++) {
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ring();
                                                    }
                                                });
                                                try {
                                                    Thread.sleep(1800);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).start();
                                }
                                hotEyesMode = 1;
                            } else if ((content[0] & 0xFF) == 0x02) {
                                MyApplication.getInstance().showToastStr("热敷眼罩正在工作");
                                if (hotEyesMode != 2)
                                    hotEyesWorkTime = System.currentTimeMillis();
                                hotEyesMode = 2;
                            } else {
                                hotEyesWorkTime = 0;
                                MyApplication.getInstance().showToastStr("未连接热敷眼罩");
                                hotEyesMode = 0;
                            }
                        }
                    }, 200);
                    break;
                }
                case 200:{
                    done(1);
                    mHandler.sendEmptyMessageDelayed(201,1000);
                    break;
                }
                case 201:{
                    portInit1();
                    break;
                }
                default: {
                    notifyMsgProcvServiceMsgListener(msg.what, (byte[]) msg.obj);
                    break;
                }
            }
        }
    }

    private void ring() {//蜂鸣器
        send(SendDataArray.ring());
    }

    //串口初始化
    private void portInit() {
        if(!portInit1())
            return;

        mReadThread = new ReadThread();
        mReadThread.start();

        mSendThread = new SendThread();
        mSendThread.start();

        //发送握手消息
        mInitPortHandler.sendEmptyMessage(1);
    }

    private boolean portInit1(){
        //MyApplication.getInstance().showToastStr("打开串口");
        try {
            if (mSerialPort == null)
                mSerialPort = getSerialPort();
            if (mOutputStream == null)
                mOutputStream = mSerialPort.getOutputStream();
            if (mInputStream == null)
                mInputStream = mSerialPort.getInputStream();

        } catch (SecurityException e) {
            //再次尝试去获取权限

            return false;
        } catch (IOException e) {
            //提示初始化失败

            return false;
        }
        return true;
    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            String path = Constant.out_port_path;
            int baudrate = Constant.out_port_bitrate;
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    /**
     * 发送字符数据
     *
     * @param
     * @return 发送成功返回true
     */
    private List<byte[]> sendMsgs = new ArrayList<>();

    public boolean send(byte[] data) {
        if ((data == null) || data.length <= 0) {
            return false;
        }
        sendMsgs.add(data);
        return true;
    }

    private class SendThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (sendMsgs.size() == 0)
                    continue;
                byte[] data = sendMsgs.get(0);
                if (data == null || data.length == 0) {
                    sendMsgs.remove(0);
                    continue;
                }

                String send_date = byte2hex(data);
               // Log.e(TAG, "*** send send_date = " + send_date);

                if (mOutputStream != null) {
                    try {
                        mOutputStream.write(data);
                        mOutputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sendMsgs.remove(0);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] cache = new byte[1024];
                    if (mInputStream == null) {
                        return;
                    }
                    Thread.sleep(50);
                    size = mInputStream.read(cache);
                    if (size > 0) {
                        byte[] recv = new byte[size];
                        System.arraycopy(cache, 0, recv, 0, size);

                        String recv_date = byte2hex(recv);
                      //  Log.e(TAG, "*** onDataReceived recv_date = " + recv_date);

                        if (!TextUtils.isEmpty(recv_date))
                            onDataReceived(recv);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    //消息接收处理
    private void onDataReceived(byte[] recv) {
        if ((recv == null) || (recv.length == 0)) {
            Log.e(TAG, "onDataReceived: recv == null!!!");
            return;
        }

        mReceiveBuffer.add(recv);

        unpackLinkMessage();
    }

    private void unpackLinkMessage() {
        mReceiveBuffer.clearInvalid();
        if (mReceiveBuffer == null || mReceiveBuffer.length() < MIN_MSG_LENGTH)
            return;

        byte[] headBytes = mReceiveBuffer.getBytes(0, AHEAD_LENGTH);
        if (!doCheckHead(headBytes)) {
            return;
        }

        int dataLengthPosion = AHEAD_LENGTH + 1;
        byte[] aLengthBytes = mReceiveBuffer.getBytes(dataLengthPosion, 2);
        int length = ((aLengthBytes[0] & 0xFF) * 256 + aLengthBytes[1] & 0xFF);

        int endPosion = AHEAD_LENGTH + 5 + length;
        byte[] endBytes = mReceiveBuffer.getBytes(endPosion, 4);
        if (!doCheckEnd(endBytes)) {
            mReceiveBuffer.removeBytes(endPosion + 4);
            return;
        }

        int cmdLengthPosion = AHEAD_LENGTH;
        byte[] cmdBytes = mReceiveBuffer.getBytes(cmdLengthPosion, 1);

        int dataPosion = AHEAD_LENGTH + 3;
        byte[] dataBytes = mReceiveBuffer.getBytes(dataPosion, length);

        Message msg = new Message();
        msg.what = (cmdBytes[0] & 0xFF);
        msg.obj = dataBytes;
        mHandler.sendMessage(msg);

        int totalLength = 13 + length;
        mReceiveBuffer.removeBytes(totalLength);
    }

    private boolean doCheckHead(byte[] reciveData) {
        if (reciveData[0] != reciveData[1] || reciveData[0] != reciveData[2] ||
                reciveData[0] != reciveData[3] || reciveData[0] != 0x55) {
            Log.e(TAG, "数据帧头错误");
            return false;
        }
        return true;
    }

    private boolean doCheckEnd(byte[] reciveData) {
        if (reciveData[0] != reciveData[1] || reciveData[0] != reciveData[2] ||
                reciveData[0] != reciveData[3] || (reciveData[0] & 0xff) != 0xAA) {
            Log.e(TAG, "数据帧尾错误");
            return false;
        }
        return true;
    }

    private class HeartbeatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: {
                    if ((curHeartNoAckCnt > 3)) {
                        //提示用户，系统出现问题
                        //broadcastManager.sendBroadcast(new Intent(Constant.ACTION_HEART_BEEB_ERR));
                        curHeartNoAckCnt = 0;
                    } else {
                        curHeartNoAckCnt++;
                    }

                    send(SendDataArray.heartBeeb());
                    mHeartbeatHandler.removeMessages(1);
                    mHeartbeatHandler.sendEmptyMessageDelayed(1, HEARTBEAT_CHECK_INTERVAL);
                    break;
                }
            }
        }
    }

    private class InitPortHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!bInit) {
                if (sayHelloToMcuCnt <= 0) {
                    //提示用户，系统出现问题
                    broadcastManager.sendBroadcast(new Intent(Constant.ACTION_PORT_INIT_ERR));
                } else {
                    send(SendDataArray.portInit());
                    mInitPortHandler.sendEmptyMessageDelayed(1, INIT_PORT_INTERVAL);

                    sayHelloToMcuCnt--;
                }
            }
        }
    }

    private static String byte2hex(byte[] buffer) {
        String h = "";
        if (buffer == null || buffer.length == 0) {
            return h;
        }
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }
        return h;
    }

}
