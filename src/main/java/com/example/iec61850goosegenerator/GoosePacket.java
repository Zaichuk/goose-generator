package com.example.iec61850goosegenerator;

import com.example.iec61850goosegenerator.ByteConverter;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Data;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;



@Data
public class GoosePacket {
    private String macDst;
    private String macSrc;
    private short type;
    private byte[] appid = {00, 0x05};

    private byte[] gooseLength = {00, (byte) 0x98};
    private static final byte[] reserved1 = {0x00, 0x00};
    private static final byte[] reserved2 = {0x00, 0x00};

    private final byte goosePduTag = 0x61;
    private final byte containerSize = (byte) 0x81;

    private byte goosePduLen = (byte) 0x90;
    private String gocbRef;

    private final byte gocbRefTag = (byte) 0x80;
    private int timeAllowedtoLive;

    private final byte timeAllowedtoLiveTag = (byte) 0x81;
    private String datSet;

    private final byte datSetTag = (byte) 0x82;
    private String goID;
    private final byte goIDtag = (byte) 0x83;
    private int t;

    private final byte tTag = (byte) 0x84;
    private final byte tLen = 0x08;
    private int stNum;

    private final byte stNumTag = (byte) 0x85;
    private int sqNum;
    private final byte sqNumTag = (byte) 0x86;
    private boolean simulation;
    private final byte simulationTag = (byte) 0x87;
    private final byte simulationLen = 0x01;
    private int confRef;
    private final byte confRefTag = (byte) 0x88;
    private final byte confRefLen = 0x01;
    private boolean ndsCom;

    private final byte ndsComTag = (byte) 0x89;
    private final byte ndsComLen = 0x01;
    private int numDatSetEntries;
    private final byte numDatSetEntriesTag = (byte) 0x8a;
    private final byte numDatSetEntriesLen = 0x01;

    private final byte dataSetTag = (byte) 0xab;
    private byte dataSetLen = 0x18;
    private boolean data;

    private boolean data1;

    private boolean data2;

    private boolean data3;

    private boolean data4;

    private boolean data5;

    private boolean data6;

    private boolean data7;
    private final byte dataTag = (byte) 0x83;
    private final byte dataLen = 0x01;



    public byte[] createGoosePayload() {
        int gooseLengthOffset = 0;
        int goosePduLenOffset = 0;
        int dataSetLenOffset = 0;
        boolean[] allData = {data,data1,data2,data3,data4,data5,data6,data7};

        byte[] tempArray = new byte[1000];
        int offset = 0;
        System.arraycopy(appid, 0, tempArray, offset, appid.length);
        offset += appid.length;
        gooseLengthOffset = offset;
        System.arraycopy(gooseLength, 0, tempArray, offset, gooseLength.length);
        offset += gooseLength.length;

        System.arraycopy(reserved1, 0, tempArray, offset, reserved1.length);
        offset += reserved1.length;

        System.arraycopy(reserved2, 0, tempArray, offset, reserved2.length);
        offset += reserved2.length;

        tempArray[offset] = goosePduTag;
        offset++;
        tempArray[offset] = containerSize;
        offset++;
        goosePduLenOffset = offset;
        tempArray[offset] = goosePduLen;
        offset++;

        tempArray[offset] = gocbRefTag;
        offset++;
        byte[] gocbRefBytes = ByteConverter.convertToBytes(gocbRef);
        tempArray[offset] = (byte) gocbRefBytes.length;
        offset++;
        System.arraycopy(gocbRefBytes, 0, tempArray, offset, gocbRefBytes.length);
        offset += gocbRefBytes.length;

        tempArray[offset] = timeAllowedtoLiveTag;
        offset++;
        byte[] timeAllowedtoLiveBytes = ByteConverter.convertToBytes(timeAllowedtoLive);
        tempArray[offset] = (byte) timeAllowedtoLiveBytes.length;
        offset++;
        System.arraycopy(timeAllowedtoLiveBytes, 0, tempArray, offset, timeAllowedtoLiveBytes.length);
        offset += timeAllowedtoLiveBytes.length;

        tempArray[offset] = datSetTag;
        offset++;
        byte[] datSetBytes = ByteConverter.convertToBytes(datSet);
        tempArray[offset] = (byte) datSetBytes.length;
        offset++;
        System.arraycopy(datSetBytes, 0, tempArray, offset, datSetBytes.length);
        offset += datSetBytes.length;


        tempArray[offset] = goIDtag;
        offset++;
        byte[] goIdBytes = ByteConverter.convertToBytes(goID);
        tempArray[offset] = (byte) goIdBytes.length;
        offset++;
        System.arraycopy(goIdBytes, 0, tempArray, offset, goIdBytes.length);
        offset += goIdBytes.length;


        tempArray[offset] = tTag;
        offset++;
        byte[] tInBytes = ByteBuffer.allocate(8).putInt(t).array();
        tempArray[offset] = (byte) tInBytes.length;
        offset++;
        System.arraycopy(tInBytes, 0, tempArray, offset, tInBytes.length);

        offset += tInBytes.length;


        tempArray[offset] = stNumTag;
        offset++;
        byte[] stNumBytes = ByteConverter.convertToBytes(stNum);
        tempArray[offset] = (byte) stNumBytes.length;
        offset++;
        System.arraycopy(stNumBytes, 0, tempArray, offset, stNumBytes.length);
        offset += stNumBytes.length;

        tempArray[offset] = sqNumTag;
        offset++;
        byte[] sqNumBytes = ByteConverter.convertToBytes(sqNum);
        tempArray[offset] = (byte) sqNumBytes.length;
        offset++;
        System.arraycopy(sqNumBytes, 0, tempArray, offset, sqNumBytes.length);
        offset += sqNumBytes.length;


        tempArray[offset] = simulationTag;
        offset++;
        tempArray[offset] = simulationLen;
        offset++;
        tempArray[offset] = ByteConverter.convertToByte(simulation);
        offset++;


        tempArray[offset] = confRefTag;
        offset++;
        byte[] confRefBytes = ByteConverter.convertToBytes(confRef);
        tempArray[offset] = (byte) confRefBytes.length;
        offset++;
        System.arraycopy(confRefBytes, 0, tempArray, offset, confRefBytes.length);
        offset += confRefBytes.length;

        tempArray[offset] = ndsComTag;
        offset++;
        tempArray[offset] = ndsComLen;
        offset++;
        tempArray[offset] = ByteConverter.convertToByte(ndsCom);
        offset++;


        tempArray[offset] = numDatSetEntriesTag;
        offset++;
        tempArray[offset] = (byte) ByteConverter.convertToBytes(numDatSetEntries).length;
        offset++;
        System.arraycopy(ByteConverter.convertToBytes(numDatSetEntries), 0, tempArray, offset, ByteConverter.convertToBytes(numDatSetEntries).length);
        offset += ByteConverter.convertToBytes(numDatSetEntries).length;

        tempArray[offset] = dataSetTag;
        offset++;
        tempArray[offset] = dataSetLen;
        offset++;

        for (int i = 0; i < allData.length; i++) {
            tempArray[offset] = dataTag;
            offset++;
            tempArray[offset] = dataLen;
            offset++;
            tempArray[offset] = ByteConverter.convertToByte(allData[i]);
            offset++;

        }
        gooseLength[1] = (byte) (offset-3);
        goosePduLen = (byte) (offset - goosePduLenOffset-1);
        System.arraycopy(gooseLength, 0, tempArray, gooseLengthOffset, gooseLength.length);
        tempArray[goosePduLenOffset] = goosePduLen;

        byte [] result = new byte[offset];
        System.arraycopy(tempArray,0,result,0,offset);

        return result;
    }


    public static int getCurrentTimeForT() {
        return  (int) (System.currentTimeMillis() / 1000) + 3 * 60 * 60;


    }


}
