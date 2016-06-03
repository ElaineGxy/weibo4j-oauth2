package diyweibo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import entity.News;
import pdfToImg.ItextManager;
import pdfToImg.PDFchangToImage;

public class Test {

	public static void main(String[] args) throws Exception {

		/**
		 * 文本转图片测试类
		 * 
		 * @author YY2924 2014/11/18
		 * @version 1.0
		 */
		ItextManager itext=new ItextManager();
		News news=new News();
		List<News>newsList=news.getData();
		List<String>imgList=new ArrayList<String>();
		imgList.add("/Users/Elaine/Desktop/IMG_1778.jpg");
		imgList.add("/Users/Elaine/Desktop/IMG_1778.jpg");
		//生成的pdf的路径名称
		String pdfName="/Users/Elaine/Desktop/test.pdf";
		OutputStream out=new FileOutputStream(new File(pdfName));
		String type="pdf";
		itext.createRtfContext(newsList, imgList, out, type);//生成pdf文件
		
		String imageFileName = "/Users/Elaine/Desktop/test.jpg";
		PDFchangToImage convert = new PDFchangToImage();
		//convert.pdfToJpgMultiple(pdfName,imageFileName);
		convert.pdf2multiImage(pdfName,imageFileName);
		//convert.pdfToJpgSingle(pdfName, imageFileName, 1);
		
		String[] arg = new String[2];
		arg[0] = "6月3号21:15微博图片文本测试";
		arg[1] = imageFileName;
		sendWeibo sender = new sendWeibo();
		sender.sendImage(arg);
	}
}