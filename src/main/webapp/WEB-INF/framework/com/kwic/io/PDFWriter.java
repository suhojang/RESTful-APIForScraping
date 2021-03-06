package com.kwic.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFWriter {
	private String TYPE_TEXT	= "1";
	private String TYPE_IMAGE	= "2";
	
	private List<Map<String,Object>> pointList	= null;
	private String pdfPath;
	private String fontPath;
	private int totPageCount;
	
	public PDFWriter(String pdfPath,String fontPath){
		pointList			= new ArrayList<Map<String,Object>>();
		this.pdfPath		= pdfPath;
		this.fontPath		= fontPath;
		this.totPageCount	= 1;
	}
	
	public PDFWriter(String pdfPath,String fontPath,int totPageCount){
		pointList			= new ArrayList<Map<String,Object>>();
		this.pdfPath		= pdfPath;
		this.fontPath		= fontPath;
		this.totPageCount	= totPageCount;
	}
	
	public void addText(String text, float x, float y){
		addText(1,text, x, y,10);
	}
	public void addText(String text, float x, float y,int size){
		addText(1,text, x, y,size);
	}
	public void addText(int pageNo,String text, float x, float y){
		addText(pageNo,text, x, y,13);
	}
	public void addText(int pageNo,String text, float x, float y,int size){
		Map<String,Object> map	= new HashMap<String,Object>();
		map.put("TYPE", TYPE_TEXT);
		map.put("PAGE", String.valueOf(pageNo));
		map.put("DATA", text);
		map.put("X", String.valueOf(x));
		map.put("Y", String.valueOf(y));
		map.put("SIZE", String.valueOf(size));
		pointList.add(map);
	}
	public void addImage(byte[] bytes,float pX,float pY,int sW,int sH){
		addImage(1,bytes,pX,pY,sW,sH);
	}
	public void addImage(int pageNo,byte[] bytes,float pX,float pY,int sW,int sH){
		Map<String,Object> map	= new HashMap<String,Object>();
		map.put("TYPE", TYPE_IMAGE);
		map.put("PAGE", String.valueOf(pageNo));
		map.put("DATA", bytes);
		map.put("X", String.valueOf(pX));
		map.put("Y", String.valueOf(pY));
		map.put("W", String.valueOf(sW));
		map.put("H", String.valueOf(sH));
		pointList.add(map);
	}
	
	public void write(String writePath) throws Exception{
	    Document document	= null;
	    PdfWriter writer 	= null;
	    PdfReader reader	= null;
	    PdfContentByte cb	= null;
	    PdfImportedPage pPage = null;
	    FileInputStream is	= null;
	    
	    try{
	    	boolean policy		= true;
	    	if(!new File(writePath).getParentFile().exists())
	    		policy	= new File(writePath).getParentFile().mkdirs();
    	    if(!policy)
    	    	throw new IOException("Directory ??????????????? ????????????.");
	    	
	    	document	= new Document(PageSize.A4);
			is		= new FileInputStream(new File(pdfPath));
	        writer	= PdfWriter.getInstance(document, new FileOutputStream(writePath));
		    document.open();
		    cb		= writer.getDirectContent();
		    reader	= new PdfReader(is);
	    	
	    	for(int i=1;i<=totPageCount;i++){
				pPage	= writer.getImportedPage(reader, i);			
			    document.newPage();
			    cb.addTemplate(pPage, 0, 0);
	    		
	    		for(int j=0;j<pointList.size();j++){
	    			if(!pointList.get(j).get("PAGE").equals(String.valueOf(i)))
	    				continue;
	    			
	    			if(pointList.get(j).get("TYPE").equals(TYPE_TEXT))
	    				absText(cb,pointList.get(j));
	    			else if(pointList.get(j).get("TYPE").equals(TYPE_IMAGE))
	    				setImage(cb,pointList.get(j));
	    		}
	    	}
	    	
	    }catch(Exception e){
	    	throw e;
	    }finally{
			try{if(document!=null)document.close();}catch(Exception e){document=null;}
			try{if(reader!=null)reader.close();}catch(Exception e){reader=null;}
			try{if(writer!=null)writer.close();}catch(Exception e){writer=null;}
			try{if(is!=null)is.close();}catch(Exception e){is=null;}
	    }
	}
	
	
    public void absText(PdfContentByte cb, Map<String,Object> inf) throws Exception{
    	absText(cb, (String)inf.get("DATA"), Float.parseFloat((String) inf.get("X")), Float.parseFloat((String) inf.get("Y")),Integer.parseInt((String) inf.get("SIZE")));
    }
    public void setImage(PdfContentByte cb, Map<String,Object> inf) throws Exception{
    	setImage(cb, (byte[])inf.get("DATA"), Float.parseFloat((String) inf.get("X")), Float.parseFloat((String) inf.get("Y")),Integer.parseInt((String) inf.get("W")),Integer.parseInt((String) inf.get("H")));
    }
	
	//PDF ??????????????? ?????? 
    public void absText(PdfContentByte cb, String text, float x, float y) throws Exception{
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        cb.saveState();
        cb.beginText();
        cb.moveText(x, y);
        cb.setFontAndSize(bf, 13);
        cb.showText(text);
        cb.endText();
        cb.restoreState();
    }
	//PDF ??????????????? ?????? ????????? ?????? 
    public void absText(PdfContentByte cb, String text, float x, float y,int size) throws Exception {
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        cb.saveState();
        cb.beginText();
        cb.moveText(x, y);
        cb.setFontAndSize(bf, size);
        cb.showText(text);
        cb.endText();
        cb.restoreState();
    }
	//PDF ??????????????? ?????? ????????? ????????? ?????? 
    public void setImage(PdfContentByte cb,byte[] bytes,float pX,float pY,int sW,int sH) throws Exception{
		ByteArrayOutputStream stream	= null;
		Image image	= null;
		try{
	        image			= Image.getInstance(bytes);
	        image.setAbsolutePosition(pX,pY);
	        if(sW!=0 && sH!=0)
	        	image.scaleToFit(sW, sH);
	        else
    			image.scaleToFit(100, 50);
	        cb.addImage(image);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(stream!=null)stream.close();}catch(Exception ex){stream=null;}
		}
	}

    
    
    public static void main(String[] args) throws Exception{
    	PDFWriter pdfWriter	= new PDFWriter("E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/smartSecretary/src/main/webapp/form/006-1.pdf","E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/smartSecretary/src/main/webapp/font/NanumGothic-Bold.ttf");
    	
    	pdfWriter.addText("?????????",60,152,10);
    	pdfWriter.addText("?????????",270,166,10);
    	pdfWriter.addText("???????????????",480,166,10);
    	
    	//?????????
    	pdfWriter.addText("????????? ????????? ???????????? 340 988??? 2034???",270,185,10);
    	//??????????????????
    	pdfWriter.addText("??????????????????",270,202,10);
    	//?????????
    	pdfWriter.addText("2018",466,214,8);
    	pdfWriter.addText("07",504,214,8);
    	pdfWriter.addText("24",527,214,8);
    	//?????????????????????
    	pdfWriter.addText("94???-55-88222-1",215,242,10);
    	//???????????????
    	pdfWriter.addText("???????????????",215,271,10);
    	
    	pdfWriter.addText("V",57,302,10);
    	pdfWriter.addText("V",112,302,10);
    	pdfWriter.addText("V",427,302,10);
    	pdfWriter.addText("V",463,302,10);
    	
    	pdfWriter.addText("??????",270,387,10);
    	pdfWriter.addText("?????????????????????",480,386,10);
    	//??????
    	pdfWriter.addText("????????? ????????? ???????????? 340 988??? 2034???",270,404,10);
    	//?????????
    	pdfWriter.addText("2018",466,417,8);
    	pdfWriter.addText("07",504,417,8);
    	pdfWriter.addText("24",527,417,8);
    	//??????
    	pdfWriter.addText("??????",250,490,10);
    	//?????????
    	pdfWriter.addText("2018",394,490,8);
    	pdfWriter.addText("07",429,490,8);
    	pdfWriter.addText("24",455,490,8);
    	
    	pdfWriter.addText("V",225,451,10);
    	pdfWriter.addText("V",280,451,10);
    	
    	//?????????
    	pdfWriter.addText("?????????",150,665,8);
    	//?????????
    	pdfWriter.addText("jjh0568@kwic.com",335,660,8);
    	//??????
    	pdfWriter.addText("02-3654-2584",335,672,8);
    	//???????????????
    	pdfWriter.addText("?????? ??? ?????????????????????",442,660,8);
    	//?????????
    	pdfWriter.addText("????????? ????????? ???????????? 340 988??? 2034???",150,702,8);
    	//????????????
    	pdfWriter.addText("02-3654-2584",442,698,8);
    	//?????????????????????
    	pdfWriter.addText("94???-55-88222-1",150,732,8);
    	//?????????????????????
    	pdfWriter.addText("??????????????????",335,732,8);
    	//?????????????????????
    	pdfWriter.addText("5 ???",442,728,8);
    	
    	
    	
    	pdfWriter.write("E:/006-1.pdf");
/*
		PDFImage pdf	= new PDFImage(new File("E:/006-1.pdf"));
		int cnt	= pdf.getPageCount();
		File file	= pdf.writeImage(new File("E:/006-1_tmp.jpeg"), cnt, cnt, "1234");
		ImageConverter.convert(file,new File("E:/006-1.jpeg"),300*1024);
		*/
    }
    public static void main2(String[] args) throws Exception{
    	PDFWriter pdfWriter	= new PDFWriter("E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/smartSecretary/src/main/webapp/form/006-2.pdf","E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/smartSecretary/src/main/webapp/font/NanumGothic-Bold.ttf");
    	
    	
    	pdfWriter.addText("?????????",260,335,10);
    	pdfWriter.addText("???????????????",330,335,10);
    	
    	pdfWriter.addText("2015",243,363,10);
    	pdfWriter.addText("10",280,363,10);
    	pdfWriter.addText("13",306,363,10);
    	
    	//?????????????????????
    	pdfWriter.addText("214-81-59394",185,496,12);
    	//????????????
    	pdfWriter.addText("?????????",185,529,12);
    	//????????????
    	pdfWriter.addText("740808-2346521",420,529,12);
    	//?????????
//    	String addr1	= "????????? ????????? ???????????? 340";
//    	String addr2	= "928??? 1104???";
    	String addr1	= "????????? ????????? ????????????";
    	String addr2	= "928??? 104???";
    	if((addr1+addr2).length()>22){
        	pdfWriter.addText(addr2,185,555,10);
        	pdfWriter.addText(addr1,185,568,10);
    	}else{
        	pdfWriter.addText((addr1+" "+addr2),187,560,12);
    	}
    	//????????????
    	pdfWriter.addText("02-3366-7742",458,561,10);
    	//?????????????????????
    	pdfWriter.addText("94???-55-88222-1",185,599,12);
    	//????????????
    	pdfWriter.addText("??????????????????",420,599,12);
    	
    	//?????????
    	pdfWriter.addText("?????????",185,638,12);
    	//????????????
    	pdfWriter.addText("740808-2346521",420,638,12);

//    	addr1	= "????????? ????????? ???????????? 340";
//    	addr2	= "928??? 1104???";
    	addr1	= "????????? ????????? ????????????";
    	addr2	= "928??? 104???";
    	if((addr1+addr2).length()>22){
        	pdfWriter.addText(addr2,185,665,10);
        	pdfWriter.addText(addr1,185,678,10);
    	}else{
        	pdfWriter.addText((addr1+" "+addr2),187,672,12);
    	}
    	//????????????
    	pdfWriter.addText("02-3366-7742",458,673,10);
    	
    	//?????????????????????
    	pdfWriter.addText("94???-55-88222-1",185,704,12);
    	//????????????
    	pdfWriter.addText("??????????????????",420,704,12);
    	
    	pdfWriter.write("E:/006-2.pdf");
/*
		PDFImage pdf	= new PDFImage(new File("E:/006-2.pdf"));
		int cnt	= pdf.getPageCount();
		File file	= pdf.writeImage(new File("E:/006-2_tmp.jpeg"), cnt, cnt, "1234");
		ImageConverter.convert(file,new File("E:/006-2.jpeg"),300*1024);
		*/
    }
    public static void main1(String[] args) throws Exception{
    	PDFWriter pdfWriter	= new PDFWriter("E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/smartSecretary/src/main/webapp/form/005.pdf","E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/smartSecretary/src/main/webapp/font/NanumGothic-Bold.ttf");
    	
    	pdfWriter.addText("?????????",410,125,8);
    	pdfWriter.addText("?????????",500,124,8);
    	pdfWriter.addText("?????????",410,145,8);
    	pdfWriter.addText("?????????",500,144,8);
    	
    	pdfWriter.addText("2015",425,156,8);
    	pdfWriter.addText("10",474,156,8);
    	pdfWriter.addText("13",505,156,8);
    	
    	pdfWriter.addText("V",464,216);
    	pdfWriter.addText("V",464,308);
    	
    	//?????????????????????
    	pdfWriter.addText("2",145,383,20);
    	pdfWriter.addText("1",171,383,20);
    	pdfWriter.addText("4",196,383,20);
    	pdfWriter.addText("8",239,383,20);
    	pdfWriter.addText("1",265,383,20);
    	pdfWriter.addText("5",303,383,20);
    	pdfWriter.addText("9",329,383,20);
    	pdfWriter.addText("3",355,383,20);
    	pdfWriter.addText("9",382,383,20);
    	pdfWriter.addText("4",407,383,20);
    	
    	//?????????????????????
    	pdfWriter.addText("7",144,420,20);
    	pdfWriter.addText("5",170,420,20);
    	pdfWriter.addText("0",196,420,20);
    	pdfWriter.addText("7",222,420,20);
    	pdfWriter.addText("2",248,420,20);
    	pdfWriter.addText("1",274,420,20);
    	pdfWriter.addText("1",320,420,20);
    	
    	//???????????????
    	pdfWriter.addText("0",144,456,20);
    	pdfWriter.addText("1",171,456,20);
    	pdfWriter.addText("0",196,456,20);
    	pdfWriter.addText("5",238,456,20);
    	pdfWriter.addText("6",264,456,20);
    	pdfWriter.addText("7",290,456,20);
    	pdfWriter.addText("8",316,456,20);
    	pdfWriter.addText("3",357,456,20);
    	pdfWriter.addText("1",384,456,20);
    	pdfWriter.addText("6",410,456,20);
    	pdfWriter.addText("8",436,456,20);
    	
    	
    	//????????????
    	int x	= 145;
    	for(int i=0;i<16;i++){
        	pdfWriter.addText("0",x,490,20);
        	x	+= 20;
    	}
    	
    	pdfWriter.addText("???????????????????????????",145,530);
    	pdfWriter.addText("????????????",400,522);

    	pdfWriter.addText("V",143,554);//????????????
    	
    	pdfWriter.addText("V",142,579);//????????????
    	pdfWriter.addText("V",142,591);//????????????
    	pdfWriter.addText("800,000",202,590);//????????????
    	
    	pdfWriter.addText("V",399,585);//?????????
    	pdfWriter.addText("V",440,585);//?????????
    	pdfWriter.addText("V",486,585);//?????????
    	
    	pdfWriter.addText("?????????",145,616);
    	pdfWriter.addText("??????",259,616,8);
    	
    	pdfWriter.addText("010",402,615);
    	pdfWriter.addText("5678",442,615);
    	pdfWriter.addText("3168",492,615);

    	pdfWriter.addText("????????? ????????? ????????? 550-1 ??????IT??????2??? 613???",145,670);
    	
    	pdfWriter.addText("????????????",145,695);
    	pdfWriter.addText("000-00-00000",400,695);
    	
    	pdfWriter.addText("???????????????",145,720);
    	pdfWriter.addText("????????????",400,720);
    	
    	pdfWriter.write("E:/005.pdf");
/*
		PDFImage pdf	= new PDFImage(new File("E:/005.pdf"));
		int cnt	= pdf.getPageCount();
		File file	= pdf.writeImage(new File("E:/005_tmp.jpeg"), cnt, cnt, "1234");
		ImageConverter.convert(file,new File("E:/005.jpeg"),300*1024);
		*/
    }
}
