package pdfToImg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * 
 * 对实现对IMG与PDF相互转换。 运行测试需要导入以下2个jar包 itext-2.0.2.jar PDFRenderer.jar
 *
 */
@SuppressWarnings("unused")
public class PDFchangToImage {
	/**
	 * 针对1张pdf生成图片
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件
	 * @param x
	 *            读取源文件中的第几页
	 */
	public void pdfToJpgSingle(String source, String target, int x) throws Exception {
		// 创建从中读取和向其中写入（可选）的随机访问文件流，R表示对其只是访问模式
		RandomAccessFile rea = new RandomAccessFile(new File(source), "r");

		// 将流读取到内存中，然后还映射一个PDF对象
		FileChannel channel = rea.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdfFile = new PDFFile(buf);
		PDFPage page = pdfFile.getPage(x);

		// get the width and height for the doc at the default zoom
		java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, (int) page.getBBox().getWidth(),
				(int) page.getBBox().getHeight());
		//设置生成图片的分辨率，数值越大越清晰
		int ratio=2;
		// generate the image
		java.awt.Image img = page.getImage(rect.width*ratio, rect.height*ratio, // width &
				rect, // clip rect
				null, // null for the ImageObserver
				true, // fill background with white
				true // block until drawing is done
		);

		BufferedImage tag = new BufferedImage(rect.width*ratio, rect.height*ratio, BufferedImage.TYPE_INT_RGB);

		tag.getGraphics().drawImage(img, 0, 0, rect.width*ratio, rect.height*ratio, null);
		FileOutputStream out = new FileOutputStream(target); // 输出到文件流
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//		JPEGEncodeParam param=encoder.getDefaultJPEGEncodeParam(tag);
//		param.setQuality(0.8f, false);
//		encoder.setJPEGEncodeParam(param);
		encoder.encode(tag); // JPEG编码
		out.close();
	}
	
	public void pdf2multiImage(String pdfFile, String outpath) {
		try {
			InputStream is = new FileInputStream(pdfFile);
			PDDocument pdf = PDDocument.load(is, true);
			@SuppressWarnings("unchecked")
			List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();
			List<BufferedImage> piclist = new ArrayList<BufferedImage>();
			int actSize = pages.size(); // pdf中实际的页数
			//if (actSize < maxPage) maxPage = actSize;
			for (int i = 0; i < actSize; i++) {
				piclist.add((pages.get(i)).convertToImage());
			}
			yPic(piclist, outpath);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将宽度相同的图片，竖向追加在一起 ##注意：宽度必须相同
	 * 
	 * @param piclist
	 *            文件流数组
	 * @param outPath
	 *            输出路径
	 */
	public void yPic(List<BufferedImage> piclist, String outPath) {// 纵向处理图片
		if (piclist == null || piclist.size() <= 0) {
			System.out.println("图片数组为空!");
			return;
		}
		try {
			int height = 0, // 总高度
			width = 0, // 总宽度
			_height = 0, // 临时的高度 , 或保存偏移高度
			__height = 0, // 临时的高度，主要保存每个高度
			picNum = piclist.size();// 图片的数量
			File fileImg = null; // 保存读取出的图片
			int[] heightArray = new int[picNum]; // 保存每个文件的高度
			BufferedImage buffer = null; // 保存图片流
			List<int[]> imgRGB = new ArrayList<int[]>(); // 保存所有的图片的RGB
			int[] _imgRGB; // 保存一张图片中的RGB数据
			for (int i = 0; i < picNum; i++) {
				buffer = piclist.get(i);
				heightArray[i] = _height = buffer.getHeight();// 图片高度
				if (i == 0) {
					width = buffer.getWidth();// 图片宽度
				}
				height += _height; // 获取总高度
				_imgRGB = new int[width * _height];// 从图片中读取RGB
				_imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
				imgRGB.add(_imgRGB);
			}
			_height = 0; // 设置偏移高度为0
			// 生成新图片
			BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < picNum; i++) {
				__height = heightArray[i];
				if (i != 0) _height += __height; // 计算偏移高度
				imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中
			}
			File outFile = new File(outPath);
			ImageIO.write(imageResult, "jpg", outFile);// 写图片
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}