package com.l.eyescure.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by admin on 2017/4/11.
 * 网络打印机
 */

public class NetPrinter {
    //定义编码方式
    private static String encoding = "ASCII";
    private String ip;
    private Socket sock = null;
    // 通过socket流进行读写
    private OutputStream socketOut = null;
    private OutputStreamWriter writer = null;

    /**
     * 初始化Pos实例
     *
     * @param ip       打印机IP
     * @param port     打印机端口号
     * @param encoding 编码
     * @throws IOException
     */
    public NetPrinter(String ip, int port) {//, String encoding
        boolean isSocketConnect = false;
        try {
            this.ip = ip;
            if (sock != null) {
                closeIOAndSocket();
            } else {
                sock = new Socket(ip, port);
            }
            sock.setSoTimeout(1000 * 3);
            socketOut = new DataOutputStream(sock.getOutputStream());
//            this.encoding = encoding;
            writer = new OutputStreamWriter(socketOut, encoding);
            isSocketConnect = true;
        } catch (Exception e) {
            Log.e("king", e.toString());
        }
    }

    /**
     * 获取打印机状态
     * @return
     */
    public String getPrintStatus(String ip, int port) {
        try {
            Socket sock = new Socket(ip, port); // ip and port of printer
            sock.setSoTimeout(3000);
            OutputStream outStream = sock.getOutputStream();
            outStream.write(new byte[]{0x1B, 0x76});
            outStream.flush();
            InputStream stream = sock.getInputStream();
            byte[] bytes = new byte[4];
            stream.read(bytes);
//            Message msg = new Message();
            stream.close();
            outStream.close();
            sock.close();
            return bytes[0] + "," + bytes[1] + "," + bytes[2] + "," + bytes[3] + ",";
        } catch (UnknownHostException e) {
//            Message msg = new Message();
            e.printStackTrace();
        } catch (IOException e) {
//            Message msg = new Message();
            e.printStackTrace();
        }
        return "error";

    }

    /**
     * 关闭IO流和Socket
     *
     * @throws IOException
     */
    public void closeIOAndSocket() {
        try {
            writer.close();
            socketOut.close();
            sock.close();
        } catch (Exception e) {

        }
    }

    /**
     * 判断socket连接状态
     */
    public boolean isSocketConnect() throws IOException {
        boolean connected = sock.isConnected();
        return connected;
    }

    /**
     * 打印二维码
     *
     * @param qrData 二维码的内容
     * @throws IOException
     */
    public void qrCode(String qrData) throws IOException {
        int moduleSize = 8;
        int length = qrData.getBytes(encoding).length;

        //打印二维码矩阵
        writer.write(0x1D);// init
        writer.write("(k");// adjust height of barcode
        writer.write(length + 3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(80); // fn
        writer.write(48); //
        writer.write(qrData);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(69);
        writer.write(48);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(67);
        writer.write(moduleSize);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(81); // fn
        writer.write(48); // m

        writer.flush();

    }

    /**
     * 进纸并全部切割
     *
     * @throws IOException
     */
    public void feedAndCut() throws IOException {
        writer.write(0x1D);
        writer.write(86);
        writer.write(65);
        //        writer.write(0);
        //切纸前走纸多少
        writer.write(100);
        writer.flush();

        //另外一种切纸的方式
        //        byte[] bytes = {29, 86, 0};
        //        socketOut.write(bytes);
    }

    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        for (int i = 0; i < lineNum; i++) {
            writer.write("\n");
        }
        writer.flush();
    }

    /**
     * 打印换行(只换一行)
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        writer.write("\n");
        writer.flush();
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("\t");
        }
        writer.flush();
    }

    /**
     * 打印空白（一个汉字的位置）
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printWordSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("  ");
        }
        writer.flush();
    }

    /**
     * 打印位置调整
     *
     * @param position 打印位置  0：居左(默认) 1：居中 2：居右
     * @throws IOException
     */
    public void printLocation(int position) throws IOException {
        writer.write((char) 27);
        writer.write((char) 97);
        writer.write((char) position);
        writer.flush();
    }

    /**
     * 绝对打印位置
     *
     * @throws IOException
     */
    public void printLocation(int light, int weight) throws IOException {
        writer.write(0x1B);
        writer.write(0x24);
        writer.write(light);
        writer.write(weight);
        writer.flush();
    }

    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 打印照片
     *
     * @param path
     * @throws IOException
     */
    public void printImg(String path) throws Exception {
        InputStream inputStream = new FileInputStream(path);
        byte[] content = readStream(inputStream);
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * @param 将图片内容解析成字节数组
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    private static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    /**
     * 新起一行，打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printTextNewLine(String text) throws IOException {
        //换行
        writer.write("\n");
        writer.flush();

        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    public void initPos() throws IOException {
        writer.write(0x1B);
        writer.write(0x40);
        writer.flush();
    }

    /**
     * 加粗
     *
     * @param flag false为不加粗
     * @return
     * @throws IOException
     */
    public void bold(boolean flag) throws IOException {
        if (flag) {
            //常规粗细
            writer.write(0x1B);
            writer.write(69);
            writer.write(0xF);
            writer.flush();
        } else {
            //加粗
            writer.write(0x1B);
            writer.write(69);
            writer.write(0);
            writer.flush();
        }
    }

    public void fontSize(int size) throws IOException {
        writer.write(CMD_FontSize(size));
        writer.flush();

    }

    /**
     * 睡几秒
     */
    private void sleep(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    /**
     * 打开钱箱
     */
    public void openCashBox() throws IOException {
        writer.write(0x1B);
        writer.write(0x70);
        writer.write(0);
        writer.write(100);
        writer.write(80);
        writer.flush();
    }


    /// <summary>
    /// 字体的大小
    /// </summary>
    /// <param name="nfontsize">0:正常大小 1:两倍高 2:两倍宽 3:两倍大小 4:三倍高 5:三倍宽 6:三倍大小 7:四倍高 8:四倍宽 9:四倍大小 10:五倍高 11:五倍宽 12:五倍大小</param>
    /// <returns></returns>
    public String CMD_FontSize(int nfontsize) {
        String _cmdstr = "";

        //设置字体大小
        switch (nfontsize) {
            case -1:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 0).toString();//29 33
                break;

            case 0:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 0).toString();//29 33
                break;

            case 1:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 1).toString();
                break;

            case 2:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 16).toString();
                break;

            case 3:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 17).toString();
                break;

            case 4:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 2).toString();
                break;

            case 5:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 32).toString();
                break;

            case 6:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 34).toString();
                break;

            case 7:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 3).toString();
                break;

            case 8:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 48).toString();
                break;

            case 9:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 51).toString();
                break;

            case 10:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 4).toString();
                break;

            case 11:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 64).toString();
                break;

            case 12:
                _cmdstr = new StringBuffer().append((char) 29).append((char) 33).append((char) 68).toString();
                break;

        }
        return _cmdstr;
    }
}
