package textToImage;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

public class ConvertPDF {

	static final int wdDoNotSaveChanges = 0;// 不保存待定的更改。
	static final int wdFormatPDF = 17;// PDF 格式

	public String createPDF(String fileName) {
		String toFilename = fileName+ ".pdf";
		System.out.println("启动Word...");
		long start = System.currentTimeMillis();
		System.loadLibrary("jacob-1.18-x64");
		ActiveXComponent app = null;
		try {
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", false);

			Dispatch docs = app.getProperty("Documents").toDispatch();
			System.out.println("打开文档..." + fileName);
			Dispatch doc = Dispatch.call(docs, //
					"Open", //
					fileName, // FileName
					false, // ConfirmConversions
					true // ReadOnly
			).toDispatch();

			System.out.println("转换文档到PDF..." + toFilename);
			File tofile = new File(toFilename);
			if (tofile.exists()) {
				tofile.delete();
			}
			Dispatch.call(doc, //
					"SaveAs", //
					toFilename, // FileName
					wdFormatPDF);

			Dispatch.call(doc, "Close", false);
			long end = System.currentTimeMillis();
			System.out.println("转换完成..用时：" + (end - start) + "ms.");
		} catch (Exception e) {
			System.out.println("========Error:文档转换失败：" + e.getMessage());
		} finally {
			if (app != null)
				app.invoke("Quit", wdDoNotSaveChanges);
		}
		return toFilename;
	}
}