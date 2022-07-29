package com.pbs.tech.common;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Attendance Report",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22.2f, WebColors.getRGBColor("#062E51"))), 600, 750, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("From Date : "+ previousDt,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12.2f, WebColors.getRGBColor("#062E51"))), 150, 740, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("To Date : " + curDate,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12.2f, WebColors.getRGBColor("#062E51"))), 1055, 740, 0);
        }
        public void onEndPage(PdfWriter writer,Document document) {
            String curDate = formatTime.format(new Date());
            Rectangle rect = writer.getBoxSize("art");
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Generated Date: " + curDate,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10.0f, WebColors.getRGBColor("#062E51"))), 133, 23, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(" "), rect.getRight(), rect.getBottom(), 0);
        }
}
