package com.pbs.tech.common;

/**
 * @author Balamurugan V
 * @project ilens
 * @created 22/07/2022
 **/

import com.pbs.tech.common.exception.LicenceException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LicenceUtils {

    /**
     *
     * @param str
     * @param size
     * @param charStr
     * @return
     */
    public static String getSplChar(String str, int size, String charStr) {
        int tarGetsize = size - str.length();
        if (tarGetsize > 0) {

            StringBuilder builder = new StringBuilder(tarGetsize);
            for (int i = 0; i < tarGetsize; i++) {
                if (charStr != null) {
                    builder.append(charStr);
                } else {
                    builder.append('\f');
                }
            }

            return str + builder.toString();
        } else
            return str;
    }

    /**
     *
     * @param str
     * @param length
     * @return
     */
    public static List<String> getInduceStr(String str, int length) {
        List<String> strings = new ArrayList<String>();
        int index = 0;
        while (index < str.length()) {
            strings.add(str.substring(index, Math.min(index + length, str.length())));
            index += length;
        }
        return strings;
    }

    /**
     *
     * @param input
     * @return
     */
    public static String getMd5(String input) {
        try {
            System.out.println(" Input :"+ input);
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @param fromDt
     * @param toDt
     * @return
     * @throws LicenceException
     */
    public static long diffDate(String fromDt, String toDt) throws LicenceException {
        SimpleDateFormat dt = new SimpleDateFormat("ddMMyyyy");
        long dtOne = 0;
        try {
            Calendar today = Calendar.getInstance();
            if (toDt == null) {
                today.setTime(new Date());
            } else {
                today.setTime(dt.parse(toDt.trim()));
            }
            //dtOne = today.getTime().getTime() - dt.parse(fromDt.trim()).getTime();
            dtOne = dt.parse(fromDt.trim()).getTime() - today.getTime().getTime();
        } catch (ParseException e) {
            throw new LicenceException("Invalid Licence");
        }
        return (dtOne / (24 * 60 * 60 * 1000));
    }


}
