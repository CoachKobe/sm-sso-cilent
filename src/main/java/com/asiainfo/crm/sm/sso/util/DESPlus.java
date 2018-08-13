package com.asiainfo.crm.sm.sso.util;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author chenf
 */
public class DESPlus {
	private static String strDefaultKey = "national";
	private Cipher encryptCipher = null;
	private Cipher decryptCipher = null;
	
	/**
	 * 默认构造方法，使用默认密钥
	 * 
	 * @throws Exception
	 */
	public DESPlus() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * 指定密钥构造方法
	 * @param strKey 指定的密钥
	 */
	public DESPlus(String strKey) {
		try {
			Key key = getKey(strKey.getBytes());
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 * @param arrBTmp 构成该字符串的字节数组
	 * @return 生成的密钥
	 */
	private Key getKey(byte[] arrBTmp) {
		// 创建一个空的8位字节数组（默认值为0）
		byte[] arrB = new byte[8];
		// 将原始字节数组转换为8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
		return key;
	}

	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0812
	 * 
	 * @param arrB 
	 * @return String 转换后的字符串
	 */
	public static String byteArrToHexStr(byte[] arrB) {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组
	 * 
	 * @param strIn 
	 * @return 转换后的byte数组
	 */
	public static byte[] hexStrToByteArr(String strIn) {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * Function: 加密字节数组
	 * 
	 * @param arrB 
	 * @return byte[]
	 */
	public byte[] encrypt(byte[] arrB) {
		byte[] s = null;
		try {
			s = encryptCipher.doFinal(arrB);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 将启动码strIn加密，返回加密后的启动码
	 * 
	 * @param strIn 
	 * @return String
	 */
	public String encrypt(String strIn) {
		return byteArrToHexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * 解密字节数组
	 * 
	 * @param arrB 
	 * @return byte[]
	 */
	public byte[] decrypt(byte[] arrB) {
		byte[] s = null;
		try {
			s = decryptCipher.doFinal(arrB);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 解密 strIn
	 * 
	 * @param strIn 
	 * @return String 
	 */
	public String decrypt(String strIn) {
		return decrypt(strIn,null);
	}

	public String decrypt(String strIn,String charSet) {
		//return new String(decrypt(hexStrToByteArr(strIn)));
		if(charSet!=null){			
			try {
				return new String(decrypt(hexStrToByteArr(strIn)),charSet);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return new String(decrypt(hexStrToByteArr(strIn)));
	}

	public static void main(String[] args) {
		/*
		try {
			String test = "DES测试"+(char)2;
			DESPlus des = new DESPlus("1"); // 自定义密钥
			System1.out.println("加密前的字符：" + test);
			System1.out.println("加密后的字符：" + des.encrypt("1"));
			System1.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));
			
			String str = DESPlusHolder.encrypt("1");
			System1.out.println("str="+str);
			
			System1.out.println(des.decrypt("1111"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}
