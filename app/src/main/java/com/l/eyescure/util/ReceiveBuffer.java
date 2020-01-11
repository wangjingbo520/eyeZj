package com.l.eyescure.util;

import java.util.ArrayList;

/**
 * Created by jerryco on 2016/7/29.
 */
public class ReceiveBuffer {
    private ArrayList<byte[]> list;
    private int listSize;

    public int offset;

    public ReceiveBuffer() {
        list = new ArrayList<byte[]>();
        listSize = 0;
        offset = 0;
    }


    public int length() {
        return listSize - offset;
    }

    public void add(byte[] content) {
        list.add(content);
        listSize += content.length;
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
        listSize = 0;
        offset = 0;
    }

    private void clearParams() {
        offset = 0;
        listSize = 0;
    }

    public void removeBytes(int numbers) {
        if ((numbers == 0) || (length() < numbers)) {
            return;
        }
        if (list.isEmpty()) {
            clearParams();
            return;
        }

        int removeAllBytes = offset + numbers;
        do {
            if (list.isEmpty()) {
                clearParams();
                break;
            }

            byte[] content = list.get(0);
            int length = content.length;

            if (length > removeAllBytes) {
                offset = removeAllBytes;
                break;
            } else {
                list.remove(0);
                listSize -= length;
                removeAllBytes -= length;
            }
        } while (true);


    }

    public void clearInvalid() {
        byte[] AllBytes = getBytes(0, length());
        int length = AllBytes.length;
        if (length == 0) {
            return;
        }

//        boolean bfindData = false;
        for (int i = 0; i < length - 3; i++) {
            if (AllBytes[i] == 0x55 && AllBytes[i + 1] == 0x55 && AllBytes[i + 2] == 0x55
                    && AllBytes[i + 3] == 0x55) {
                removeBytes(i);
                break;
//                bfindData = true;
            }
        }

//        if(!bfindData){
//            clear();
//        }
    }

    public byte[] getBytes(int index, int numbers) {
        if ((numbers == 0) || (numbers > length())) {
            return null;
        }

        byte[] data = new byte[numbers];
        int start = offset + index;

        boolean isFind = false;
        int getNumbers = numbers;
        int copiedNumbers = 0;
        for (int i = 0; i < list.size(); i++) {
            byte[] content = list.get(i);
            int length = content.length;

            if (!isFind) {
                if (start < length) {
                    isFind = true;
                } else {
                    start -= length;
                    continue;
                }
            }

            if (getNumbers < (length - start)) {
                System.arraycopy(content, start, data, copiedNumbers, getNumbers);
                break;
            } else {
                System.arraycopy(content, start, data, copiedNumbers, length - start);
                getNumbers -= (length - start);
                copiedNumbers += (length - start);
            }
            start = 0;

        }

        return data;
    }

    public void offset(int value) {
        if (list.isEmpty()) {
            clearParams();
            return;
        }

        int start = offset;
        int numbers = 0;
        boolean isFindStart = false;
        boolean isFind = false;

        for (int i = 0; i < list.size(); i++) {
            byte[] content = list.get(i);
            int length = content.length;

            if (!isFindStart) {
                if (start < length) {
                    isFindStart = true;
                } else {
                    start -= length;
                    continue;
                }
            }

            for (int j = start; j < length; j++) {
                if (content[j] == value) {
                    isFind = true;
                    break;
                }
                numbers += 1;
            }

            start = 0;
            if (isFind) {
                break;
            }
        }

        removeBytes(numbers);
    }
}
