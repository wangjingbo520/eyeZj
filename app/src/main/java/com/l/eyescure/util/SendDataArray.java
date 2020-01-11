package com.l.eyescure.util;

/**
 * 封装数据
 */
public class SendDataArray {
	public SendDataArray(){
		super();
	}

	/**
	 * 自检
	 * 指令码 01 内容 04
	 * @return
     */
	public static byte[] selfCheck(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x01, 0x00, 0x01, 0x04, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x01, 0x00, 0x01, 0x04};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}
	/**
	 * 气压阀工作
	 * 指令码 03
	 * mode 工作模式  type 工作模式细分
	 * mode 01压力阀值 此时type为具体的压力值
	 * mode 02默认模式 此时type为1，自动循环3种模式 01固定的压力 02压力线性增加 03震荡压力
	 * left 左眼01  right 右眼01
     */
	public static byte[] qyfWork(int mode,int type,int left,int right){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x03, 0x00, 0x04, 0x00 ,0x00 ,0x00 ,0x00 , 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		sendBuf[7] = (byte)mode;
		sendBuf[8] = (byte)(type);
		if (mode == 2){
			sendBuf[8] = 0x01;
		}
		sendBuf[9] = (byte)left;
		sendBuf[10] = (byte)right;
		byte check[] = {0x03, 0x00, 0x04,sendBuf[7],sendBuf[8],sendBuf[9],sendBuf[10]};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[11] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[12] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}




	//发送握手消息
	public static byte[] portInit(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x20, 0x00, 0x01, 0x01, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x20, 0x00, 0x01,0x01};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	//发送心跳消息
	public static byte[] heartBeeb(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x22, 0x00, 0x01, 0x01, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x22, 0x00, 0x01,0x01};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
	 * 停止气压阀
	 * 指令码05
	 * left 01左眼  right 01右眼
	 * mode	0x00 暂停    0x01继续   0x02停止并泄气
	 */
	public static byte[] stopQyf(int mode,int left,int right){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x05, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00,0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		sendBuf[7] = (byte)mode;
		sendBuf[8] = (byte)left;
		sendBuf[9] = (byte)right;
		byte check[] = {0x05, 0x00, 0x03,sendBuf[7],sendBuf[8],sendBuf[9]};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[10] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[11] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
	 * 加热
	 * 指令码07
	 * 内容 目标温度 left左眼01  right 右眼01
	 * 温度分为两位,小数点前跟小数点后 如42.5℃	0x2A 0x05
	 * warm1 小数点前温度  warm2小数点后温度
	 */
	public static byte[] warm(byte warm1,byte warm2,int left,int right){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x07, 0x00, 0x04, 0x00, 0x00,0x00, 0x00, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		sendBuf[7] = warm1;
		sendBuf[8] = warm2;
		sendBuf[9] = (byte)left;
		sendBuf[10] = (byte)right;
		byte check[] = { 0x07, 0x00, 0x04,sendBuf[7],sendBuf[8],sendBuf[9],sendBuf[10]};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[11] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[12] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
	 * 停止加热
	 * 指令码09
	 * left 左眼01 right右眼01
	 */
	public static byte[] stopWarm(int left,int right){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x09, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		sendBuf[7] = (byte)left;
		sendBuf[8] = (byte)right;
		byte check[] = {0x09, 0x00, 0x02,sendBuf[7],sendBuf[8]};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[9] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[10] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
	 * 查询气压
	 * 指令码0b
	 * 内容 00
	 */
	public static byte[] queryQy(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x0b, 0x00, 0x01, 0x00, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x0b, 0x00, 0x01, 0x00};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
	 * 查询温度
	 * 指令码0d
	 * 内容 00
	 */
	public static byte[] queryTemp(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x0d, 0x00, 0x01, 0x00, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x0d, 0x00, 0x01, 0x00};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
     * 治疗完成并自毁
     * 指令码0f
     * 内容 00
     */
    public static byte[] finish(){
        byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x0f, 0x00, 0x01, 0x00, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
        byte check[] = {0x0f, 0x00, 0x01, 0x00};
        int iCheck = SerialPortUtil.CRC16(check,check.length);
        sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
        sendBuf[9] = (byte) (iCheck & 0xFF);
        return sendBuf;
    }

    /**
     * 蜂鸣器
     * 指令码11
     * 内容 01
     */
    public static byte[] ring(){
        byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x11, 0x00, 0x01, 0x01, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
        byte check[] = {0x11, 0x00, 0x01, 0x01};
        int iCheck = SerialPortUtil.CRC16(check,check.length);
        sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
        sendBuf[9] = (byte) (iCheck & 0xFF);
        return sendBuf;
    }

	/**
	 * 热敷眼罩
	 * 指令码13
	 * 内容 01
	 */
	public static byte[] checkGoggles(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x13, 0x00, 0x01, 0x01, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x13, 0x00, 0x01, 0x01};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	//发送关灯消息
	public static byte[] CloseLight(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x15, 0x00, 0x01, 0x01, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x15, 0x00, 0x01,0x01};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
	 * 查询气压
	 * 指令码0b
	 * 内容 00
	 */
	public static byte[] queryLeftQy(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x17, 0x00, 0x01, 0x01, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x17, 0x00, 0x01, 0x01};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

	/**
	 * 查询气压
	 * 指令码0b
	 * 内容 00
	 */
	public static byte[] queryRightQy(){
		byte sendBuf[] = {0x55, 0x55, 0x55, 0x55, 0x19, 0x00, 0x01, 0x01, 0x00, 0x00, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa};
		byte check[] = {0x19, 0x00, 0x01, 0x01};
		int iCheck = SerialPortUtil.CRC16(check,check.length);
		sendBuf[8] = (byte) ((iCheck >> 8) & 0xFF);
		sendBuf[9] = (byte) (iCheck & 0xFF);
		return sendBuf;
	}

}
