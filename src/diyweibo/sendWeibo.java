package diyweibo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import weibo4j.Timeline;
import weibo4j.examples.oauth2.Log;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

public class sendWeibo {

	/**
	 * 
	 * @param args:一个参数，args[0]:要发送的微博文本
	 */
	public void sendText(String[] args) {
		String access_token = WeiboConfig.getValue("access_token");
		System.out.print(access_token);
		String statuses = args[0];
		Timeline tm = new Timeline(access_token);
		try {
			Status status = tm.updateStatus(statuses);
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}	
	}
	/**
	 * 
	 * @param args:args[0]:发送的文本 args[2]:微博图片
	 */
	public void sendImage(String args[]) {
		try {
			try {
				System.out.println(args[1]);
				byte[] content = readFileImage(args[1]);
				System.out.println("content length:" + content.length);
				ImageItem pic = new ImageItem("pic", content);
				String s = java.net.URLEncoder.encode(args[0], "utf-8");
				String access_token = WeiboConfig.getValue("access_token");
				Timeline tm = new Timeline(access_token);
				Status status = tm.uploadStatus(s, pic);

				System.out.println("Successfully upload the status to ["
						+ status.getText() + "].");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception ioe) {
			System.out.println("Failed to read the system input.");
		}
	}

	public byte[] readFileImage(String filename) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(filename));
		int len = bufferedInputStream.available();
		byte[] bytes = new byte[len];
		int r = bufferedInputStream.read(bytes);
		if (len != r) {
			bytes = null;
			throw new IOException("读取文件不正确");
		}
		bufferedInputStream.close();
		return bytes;
	}

}
