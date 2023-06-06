package com.example.iec61850goosegenerator;

import java.nio.charset.StandardCharsets;

public class ByteConverter {

    public static byte[] convertToBytes(int val) {

        int byte3 = (int) Math.pow(2, 24);

        if (Character.MIN_VALUE <= val && val <= Byte.MAX_VALUE) {
            byte[] res = new byte[1];
            res[0] = (byte) (val & 0xff);
            return res;
        } else if (Byte.MAX_VALUE < val && val <= Character.MAX_VALUE) {
            byte[] res = new byte[2];
            res[1] = (byte) (val & 0xff);
            res[0] = (byte) ((val >> 8) & 0xff);
            return res;
        } else if (Character.MAX_VALUE < val && val <= byte3) {
            byte[] res = new byte[3];
            res[2] = (byte) (val & 0xff);
            res[1] = (byte) ((val >> 8) & 0xff);
            res[0] = (byte) ((val >> 16) & 0xff);
            return res;

        } else if (byte3 < val && val < Integer.MAX_VALUE) {
            byte[] res = new byte[4];
            res[3] = (byte) (val & 0xff);
            res[2] = (byte) ((val >> 8) & 0xff);
            res[1] = (byte) ((val >> 16) & 0xff);
            res[0] = (byte) ((val >> 24) & 0xff);
            return res;

        } else {
            throw new IllegalArgumentException("Argument must be undeniable");
        }
    }


    public static byte[] convertToBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] convertToBytes(long val) {
        byte[] res = new byte[8];
        res[7] = (byte) (val & 0xff);
        res[6] = (byte) ((val >> 8) & 0xff);
        res[5] = (byte) ((val >> 16) & 0xff);
        res[4] = (byte) ((val >> 24) & 0xff);
        res[3] = (byte) ((val >> 32) & 0xff);
        res[2] = (byte) ((val >> 40) & 0xff);
        res[1] = (byte) ((val >> 48) & 0xff);
        res[0] = (byte) ((val >> 56) & 0xff);
        return res;
    }

    public static byte convertToByte(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }
}