//package com.ftsafe.misc;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLDecoder;
//
//public class XiaMi {
//	public static void main(String[] args) {
//		// http://www.xiami.com/song/1795316173
//		String url = XiaMi.getById("1795316173");
//		System.out.println(url);
//	}
//
//	public static String getById(String id) {
//		// http://www.xiami.com/song/playlist/id/1795316173
//		String url = "http://www.xiami.com/song/playlist/id/" + id;
//		String str = httpGet(url);
//		System.err.println(str);
//		JSONObject json = XML.toJSONObject(str);
//		// String data=json.toString(4);
//		String data = json.getJSONObject("playlist").getJSONObject("trackList").getJSONObject("track")
//				.getString("location");
//		data = uu(data);
//		return data;
//	}
//
//	private static String uu(String str) {
//		// System.out.println(str);
//		int l = Integer.parseInt(str.substring(0, 1));
//		str = str.substring(1);
//		int tn = str.length();
//		int ln = (int) Math.floor((double) tn / (double) l);
//		int r = tn % l;
//		char[] tex = str.toCharArray();
//		String text = "";
//		for (int i = 0; i <= ln; i++) {
//			for (int j = 0; j < l; j++) {
//				int n = j * ln + i;
//				if (j < r)
//					n += j;
//				else
//					n += r;
//				if (n < tex.length) {
//					text += tex[n];
//				} else {
//					break;
//				}
//			}
//		}
//		text = text.substring(0, text.lastIndexOf("null") + 4);
//		try {
//			text = URLDecoder.decode(text, "utf-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		text = text.replace("^", "0");
//		// text = text.replace("%", "|");
//		// System.out.println(text);
//		return text;
//	}
//
//	private static String httpGet(String path) {
//		try {
//			URL url = new URL(path.trim());
//			// 鎵撳紑杩炴帴
//			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//			if (200 == urlConnection.getResponseCode()) {
//				// 寰楀埌杈撳叆娴�
//				InputStream is = urlConnection.getInputStream();
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				byte[] buffer = new byte[1024];
//				int len = 0;
//				while (-1 != (len = is.read(buffer))) {
//					baos.write(buffer, 0, len);
//					baos.flush();
//				}
//				return baos.toString("utf-8");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//}
