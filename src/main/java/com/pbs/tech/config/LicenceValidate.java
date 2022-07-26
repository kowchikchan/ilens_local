package com.pbs.tech.config;

/**
 * @author Balamurugan V
 * @project ilens
 * @created 22/07/2022
 **/


import com.pbs.tech.common.LicenceUtils;
import com.pbs.tech.common.exception.LicenceException;
import com.pbs.tech.vo.LicenceVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

public class LicenceValidate {
    public static Logger log = LoggerFactory.getLogger(LicenceValidate.class);

    public static LicenceVo isValidLicence(String strToDecrypt) {
        LicenceUtils utils = new LicenceUtils();
        LicenceVo vo=new LicenceVo();
        try {
            SimpleDateFormat dt = new SimpleDateFormat("ddMMyyyy");
            int period = dt.format(new Date()).length();
            String open = utils.getSplChar("" + period / Integer.valueOf("8"), 4, "0");

            String mainOpt = utils.getSplChar("1", 2, "0");

            String drToDecrypt = strToDecrypt.substring(Integer.valueOf(open), Integer.valueOf(open) + Integer.valueOf(mainOpt));

            String[] items = drToDecrypt.split("");
            StringBuilder mapping = new StringBuilder();
            for (String item : items) {
                if (StringUtils.isNumeric(item)) {
                    mapping.append(item);
                }
            }

            String secret = strToDecrypt.substring(0, Integer.valueOf(mapping.toString()));

            List<String> licArray = utils.getInduceStr(secret, dt.format(new Date()).length());
            int i = 0;
            List<String> mainStr = new ArrayList<String>(dt.format(new Date()).getBytes().length);
            while (i < dt.format(new Date()).length()) {
                String item = licArray.get(i);
                mainStr.add(item.substring(0, dt.format(new Date()).length() - 4));
                licArray.set(i, item.substring(dt.format(new Date()).length() / 2, item.length()));
                i++;
            }


            byte[] iv = StringUtils.join(mainStr, "").substring(0, period * 2).getBytes();

            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(StringUtils.join(mainStr, "").toCharArray(), utils.getMd5(StringUtils.join(mainStr, "")).getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            String res = new String(cipher.doFinal(Base64.getDecoder().decode(StringUtils.join(licArray, ""))));
            //System.out.println(res);
            String[] options = StringUtils.split(res, "/");
            List<String> values = Arrays.asList(options);
            Collections.reverse(values);

            String CT = null, ST = null, U = null, N = null, T = null , VT=null ,C=null;
            for (String value : values) {
                String[] dataSet = value.split("\\[");
                switch (dataSet[0]) {
                    case "CT":
                        CT = StringUtils.substringBetween(dataSet[1], "", "]");
                        /*long diff= utils.diffDate(CT,null);
                        System.out.println("difference : " + diff);
                        if(diff<0){
                            throw new LicenceException("Invalid licence : Date validation failed !..");
                        }*/
                        vo.setCreatedDate(CT);
                        break;
                    case "ST":
                        ST = StringUtils.substringBetween(dataSet[1], "", "]");
                        vo.setStartDate(ST);
                        break;
                    case "U":
                        U = StringUtils.substringBetween(dataSet[1], "", "]");
                        vo.setUsers(U);
                        break;
                    case "N":
                        N = StringUtils.substringBetween(dataSet[1], "", "]");
                        vo.setServerCount(N);
                        break;
                    case "VT":
                        VT = StringUtils.substringBetween(dataSet[1], "", "]");
                        long diff= utils.diffDate(VT,null);
                        if(diff<0){
                            throw new LicenceException("Invalid licence : Date validation failed !..");
                        }
                        vo.setValidTo(VT);
                        break;
                    case "T":
                        T = StringUtils.substringBetween(dataSet[1], "", "]");
                        vo.setContractType(T);
                        break;
                    case "C":
                        C = StringUtils.substringBetween(dataSet[1], "", "]");
                        vo.setClientName(C);
                        break;
                    default:
                        log.info("");

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error while decrypting: {}", e.toString());
        }
        return vo;
    }
}
