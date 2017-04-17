package application;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

public class TypeTrans {
	
	private static final int wdFormatPDF = 17;
	private static final int xlTypePDF = 0;
	private static final int ppSaveAsPDF = 32;
	private static final int msoTrue = -1;
	private static final int msofalse = 0;

	public boolean convert2PDF(String inputFile, String pdfFile) {
		String suffix =  getFileSufix(inputFile);
		File file = new File(inputFile);
		if(!file.exists()){
			System.out.println("�ļ������ڣ�");
			return false;
		}
		if(suffix.equals("pdf")){
			System.out.println("PDF not need to convert!");
			return false;
		}
		if(suffix.equals("doc")||suffix.equals("docx")||suffix.equals("txt")){
			return word2PDF(inputFile,pdfFile);
		}else if(suffix.equals("ppt")||suffix.equals("pptx")){
			return ppt2PDF(inputFile,pdfFile);
		}else if(suffix.equals("xls")||suffix.equals("xlsx")){
			return excel2PDF(inputFile,pdfFile);
		}else{
			System.out.println("�ļ���ʽ��֧��ת��!");
			return false;
		}
	}

	public static String getFileSufix(String fileName){
		int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
	}

	public boolean word2PDF(String inputFile,String pdfFile){
		ComThread.InitSTA();
		try{
		//��wordӦ�ó���
		ActiveXComponent app = new ActiveXComponent("WPS.Application");
		//����word���ɼ�
		app.setProperty("Visible", false);
		//���word�����д򿪵��ĵ�,����Documents����
		Dispatch docs = app.getProperty("Documents").toDispatch();
		//����Documents������Open�������ĵ��������ش򿪵��ĵ�����Document
		Dispatch doc = Dispatch.call(docs,
									"Open",
									inputFile,
									false,
									true
									).toDispatch();
		//����Document�����SaveAs���������ĵ�����Ϊpdf��ʽ
		
		Dispatch.call(doc,
					"SaveAs",
					pdfFile,
					wdFormatPDF		//word����Ϊpdf��ʽ�ֵ꣬Ϊ17
					);
		/*			
		Dispatch.call(doc,
				"ExportAsFixedFormat",
				pdfFile,
				wdFormatPDF		//word����Ϊpdf��ʽ�ֵ꣬Ϊ17
				);*/
		//�ر��ĵ�
		Dispatch.call(doc, "Close",false);
		//�ر�wordӦ�ó���
		app.invoke("Quit", 0);
		ComThread.Release();
		return true;
	}catch(Exception e){
		ComThread.Release();
		return false;
	}
	}

	public boolean excel2PDF(String inputFile,String pdfFile){
		ComThread.InitSTA();
		try{
			ActiveXComponent app = new ActiveXComponent("WPS.Application");
		app.setProperty("Visible", false);
		Dispatch excels = app.getProperty("Workbooks").toDispatch();
		Dispatch excel = Dispatch.call(excels,
									"Open",
									inputFile,
									false,
									true
									).toDispatch();
		Dispatch.call(excel,
					"SaveAs",
					xlTypePDF,		
					pdfFile
					);
		Dispatch.call(excel, "Close",false);
		app.invoke("Quit");
		ComThread.Release();
		return true;
	}catch(Exception e){
		ComThread.Release();
		return false;
	}
		
	}

	public boolean ppt2PDF(String inputFile,String pdfFile){
		ComThread.InitSTA();
		try{
		ActiveXComponent app = new ActiveXComponent("WPS.Application");
		//app.setProperty("Visible", msofalse);
		Dispatch ppts = app.getProperty("Presentations").toDispatch();
		
		Dispatch ppt = Dispatch.call(ppts,
									"Open",
									inputFile,
									true,//ReadOnly
									true,//Untitledָ���ļ��Ƿ��б���
									false//WithWindowָ���ļ��Ƿ�ɼ�
									).toDispatch();
		
		Dispatch.call(ppt,
					"SaveAs",
					pdfFile,
					ppSaveAsPDF	
					);
				
		Dispatch.call(ppt, "Close");
		
		app.invoke("Quit");
		ComThread.Release();
		return true;
		}catch(Exception e){
			ComThread.Release();
			return false;
		}
	}
	
}

