package com.pbs.tech.common;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
public class HeaderFooterPageEvent extends PdfPageEventHelper {

    public Date startDate;
    SimpleDateFormat formatTime = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");

    public void onStartPage(PdfWriter writer,Document document) {
            Rectangle rect = writer.getBoxSize("art");
            rect.setTop(770);
            rect.setLeft(100);
            String curDate = formatTime.format(new Date());
            String previousDt = formatTime.format(startDate);
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("iLens Report"), 105, 750, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("From Date : "+ previousDt), 160, 720, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("To Date : " + curDate), 150, 690, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Generated Date: " + curDate), 170, 660, 0);
        }
        public void onEndPage(PdfWriter writer,Document document) {
            Rectangle rect = writer.getBoxSize("art");
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(" "), rect.getLeft(), rect.getBottom(), 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(" "), rect.getRight(), rect.getBottom(), 0);
        }
}
