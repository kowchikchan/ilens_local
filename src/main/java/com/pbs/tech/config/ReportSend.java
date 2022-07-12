package com.pbs.tech.config;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pbs.tech.model.ReportPeriod;
import com.pbs.tech.repo.ReportPeriodRepo;
import com.pbs.tech.services.IlenService;
import com.pbs.tech.services.ReportServices;
import com.pbs.tech.vo.ReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class ReportSend {
    private static final Logger log = LoggerFactory.getLogger(ReportSend.class);

    @Autowired
    ReportServices reportServices;

    @Autowired
    IlenService ilenService;

    @Autowired
    ReportPeriodRepo reportPeriodRepo;

    @Value("${ilens.python.path}")
    String pythonPath;

    @Scheduled(cron="0 1 0 * * *")
    public void sendReport() throws Exception {
        log.info("Report Triggered.");
        ReportPeriod reportPeriod = reportServices.getList();
        long rptPeriod = 7;
        if (reportPeriod.getReportPeriod() == 2) {
            rptPeriod = 14;
        } else if (reportPeriod.getReportPeriod() == 4) {
            rptPeriod = 28;
        }

        //Date currentDt = new Date();
        long diff = new Date().getTime() - reportPeriod.getPreviousDate().getTime();
        long diffDays = TimeUnit.MILLISECONDS.toDays(diff) % 365;

        if (diffDays == rptPeriod) {
            String scriptPath = System.getProperty("SCRIPT_PATH");
            Document document = new Document(PageSize.A4, 14, 14, 70, 40);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(scriptPath + "/report/report.pdf"));
            PdfDestination pdfDest = new PdfDestination(PdfDestination.XYZ, 0, document.getPageSize().getHeight(), 1.0f);

            // set border
            Rectangle rect = new Rectangle(32, 25, 562, 788);
            rect.enableBorderSide(1);
            rect.enableBorderSide(2);
            rect.enableBorderSide(4);
            rect.enableBorderSide(8);
            rect.setBorderColor(BaseColor.BLUE);
            rect.setBorderWidth(1);

            // open document.
            document.open();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE);

            // title
            PdfPTable title = new PdfPTable(3);
            float[] titleClWidths = new float[]{20f, 14f, 13f};
            title.getDefaultCell().setBorder(0);
            title.setWidths(titleClWidths);

            title.addCell("Total on time Entry");
            title.addCell("Total Grace Entry");
            title.addCell("Beyond grace Entry");

            ReportPeriod reportPeriodConfigurations = reportServices.getList();
            ReportVO reportVO = ilenService.totalEntries(reportPeriodConfigurations.getReportPeriod());

            // content
            PdfPTable content = new PdfPTable(5);
            float[] columnWidths = new float[]{20f, 5f, 20f, 5f, 20f};
            content.setWidths(columnWidths);
            content.getDefaultCell().setBorder(0);

            PdfPCell pdfPCell = new PdfPCell(new Phrase(Long.toString(reportVO.getOnTime()), font));
            pdfPCell.setBorder(0);
            BaseColor onTimeColor = WebColors.getRGBColor("#2e592e");
            pdfPCell.setBackgroundColor(onTimeColor);
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setPaddingTop(30);
            pdfPCell.setPaddingBottom(30);
            pdfPCell.setPaddingLeft(5);
            pdfPCell.setPaddingRight(5);

            PdfPCell pdfPCell1 = new PdfPCell(new Phrase(" ", font));
            pdfPCell1.setBorder(0);
            pdfPCell1.setBackgroundColor(BaseColor.WHITE);
            pdfPCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell1.setPaddingTop(30);
            pdfPCell1.setPaddingBottom(30);
            pdfPCell1.setPaddingLeft(1);
            pdfPCell1.setPaddingRight(1);

            PdfPCell pdfPCell2 = new PdfPCell(new Phrase(Long.toString(reportVO.getGraceTime()), font));
            pdfPCell2.setBorder(0);
            BaseColor graceTimeColor = WebColors.getRGBColor("#edb387");
            pdfPCell2.setBackgroundColor(graceTimeColor);
            pdfPCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell2.setPaddingTop(30);
            pdfPCell2.setPaddingBottom(30);
            pdfPCell2.setPaddingLeft(5);
            pdfPCell2.setPaddingRight(5);

            PdfPCell pdfPCell3 = new PdfPCell(new Phrase(" ", font));
            pdfPCell3.setBorder(0);
            pdfPCell3.setBackgroundColor(BaseColor.WHITE);
            pdfPCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell3.setPaddingTop(30);
            pdfPCell3.setPaddingBottom(30);
            pdfPCell3.setPaddingLeft(1);
            pdfPCell3.setPaddingRight(1);

            PdfPCell pdfPCell4 = new PdfPCell(new Phrase(Long.toString(reportVO.getLateTime()), font));
            pdfPCell4.setBorder(0);
            BaseColor lateEntryColor = WebColors.getRGBColor("#f00010");
            pdfPCell4.setBackgroundColor(lateEntryColor);
            pdfPCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell4.setPaddingTop(30);
            pdfPCell4.setPaddingBottom(30);
            pdfPCell4.setPaddingLeft(5);
            pdfPCell4.setPaddingRight(5);

            content.addCell(pdfPCell);
            content.addCell(pdfPCell1);
            content.addCell(pdfPCell2);
            content.addCell(pdfPCell3);
            content.addCell(pdfPCell4);


            PdfPTable attList = new PdfPTable(5);
            float[] wdthCls = new float[]{22f, 13f, 22f, 13f, 20f};
            attList.setWidthPercentage(90);
            attList.setWidths(wdthCls);
            attList.setHorizontalAlignment(Element.ALIGN_CENTER);


            attList.addCell("Name");
            attList.addCell("Entry location");
            attList.addCell("Date Time");
            attList.addCell("Exit location");
            attList.addCell("Date Time");

            //PdfPTable attnList = new PdfPTable(5);
            SimpleDateFormat formatTime = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");
            if (reportVO.getEntryExitList().size() != 0) {
                for (int i = 0; i < reportVO.getEntryExitList().size(); i++) {
                    String name = "----";
                    String inLocation = "----";
                    String inTime = "-----";
                    String outLocation = "----";
                    String outTime = "----";

                    if (reportVO.getEntryExitList().get(i).getEntry_view() != null) {
                        name = reportVO.getEntryExitList().get(i).getName().toString();
                        inLocation = reportVO.getEntryExitList().get(i).getEntry_view().getLocation();
                        inTime = formatTime.format(reportVO.getEntryExitList().get(i).getEntry_view().getTime());
                    }


                    if (reportVO.getEntryExitList().get(i).getExit_view().size() != 0) {
                        outTime = formatTime.format(reportVO.getEntryExitList().get(i).getExit_view().get(0).getTime());
                        outLocation = reportVO.getEntryExitList().get(i).getExit_view().get(0).getLocation();
                    }

                    attList.addCell(name);
                    attList.addCell(inLocation);
                    attList.addCell(inTime);
                    attList.addCell(outLocation);
                    attList.addCell(outTime);
                }
            }

            String s = null;
            String executeCmd = pythonPath + " " + scriptPath + "/report/graph.py";
            Process p = Runtime.getRuntime().exec(executeCmd);
            log.info("process id {}", p.pid());
            // read output.
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                throw new InterruptedException("Exception {}" + e.getMessage());
            }
            while (in.ready()) {
                log.info("Output : {}", in.readLine());
            }
            // read, if error occurred.
            BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stderr.readLine()) != null) {
                log.error("Error : {}", s);
            }
            stderr.close();


            Image img = Image.getInstance(scriptPath + "/report/reportGraph.png");
            //img.setAbsolutePosition(20f, 300f);
            int indentation = 0;
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - indentation) / img.getWidth()) * 100;
            img.scalePercent(scaler);


            document.add(title);
            document.add(new Paragraph("\n"));
            document.add(content);
            document.add(new Paragraph("\n"));
            document.add(attList);
            document.add(new Paragraph("\n"));
            document.add(img);
            document.add(rect);
            document.close();
            try {
                ReportPeriod reportPeriod1 = reportPeriodRepo.findById(reportPeriod.getId()).get();
                reportPeriod1.setId(reportPeriod1.getId());
                reportPeriod1.setReportPeriod(reportPeriod1.getReportPeriod());
                reportPeriod1.setMail(reportPeriod1.getMail());
                reportPeriod1.setPreviousDate(new Date());
                reportPeriod1.setUpdatedBy("Admin");
                reportPeriod1.setUpdatedDt(new Date());
                reportPeriodRepo.save(reportPeriod1);
            }catch (Exception e){
                throw new Exception("Configurations Not saved " + e.getMessage());
            }
            log.info("Report Generated.");
        }
    }

}
