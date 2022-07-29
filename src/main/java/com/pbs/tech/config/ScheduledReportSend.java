package com.pbs.tech.config;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.*;
import com.pbs.tech.common.HeaderFooterPageEvent;
import com.pbs.tech.model.ReportPeriod;
import com.pbs.tech.services.IlenService;
import com.pbs.tech.services.MailSend;
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
public class ScheduledReportSend {
    private static final Logger log = LoggerFactory.getLogger(ScheduledReportSend.class);

    private static final SimpleDateFormat formatTime = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");

    @Autowired
    ReportServices reportServices;

    @Autowired
    IlenService ilenService;

    @Value("${ilens.python.path}")
    String pythonPath;

    @Value("${mail.user-name}")
    String username;

    @Value("${mail.password}")
    String password;

    @Value("${mail.host}")
    String host;

    @Value("${mail.port}")
    String port;

    @Scheduled(cron = "0 1 0 * * *")
    public void sendReport() throws Exception {
        log.info("Report Triggered.");
        ReportPeriod reportPeriod = reportServices.getList();
        long rptPeriod = 7;
        if (reportPeriod.getReportPeriod() == 2) {
            rptPeriod = 14;
        } else if (reportPeriod.getReportPeriod() == 4) {
            rptPeriod = 28;
        }
        long diff = new Date().getTime() - reportPeriod.getPreviousDate().getTime();
        long diffDays = TimeUnit.MILLISECONDS.toDays(diff) % 365;

        if (diffDays == rptPeriod) {
            this.getPdf();
        }

    }

    public void getPdf() throws Exception {
        ReportPeriod reportPeriod = reportServices.getList();

        ReportVO reportVO = ilenService.totalEntries(reportPeriod.getReportPeriod());
        String scriptPath = System.getProperty("SCRIPT_PATH");

        Document document = new Document(PageSize.A3.rotate(), 60, 35, 140, 60);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(scriptPath + "/report/report.pdf"));

        BaseColor titleColor = WebColors.getRGBColor("#062E51");

        Rectangle rect = new Rectangle(45, 40, 1160, 790);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);

        writer.setBoxSize("art", rect);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        event.startDate = reportPeriod.getPreviousDate();
        writer.setPageEvent(event);

        // open document.
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE);

        // title
        PdfPTable title1 = new PdfPTable(1);
        title1.setHorizontalAlignment(Element.ALIGN_LEFT);
        title1.setWidthPercentage(160 / 5.23f);
        float[] titleClWidths = new float[]{2f};
        title1.getDefaultCell().setBorder(0);
        title1.setWidths(titleClWidths);
        title1.addCell("Total on time Entry");

        PdfPTable content1 = new PdfPTable(1);
        content1.setHorizontalAlignment(Element.ALIGN_LEFT);
        content1.setWidthPercentage(50 / 5.23f);

        PdfPCell content11 = new PdfPCell(new Phrase(String.valueOf(reportVO.getOnTime()), font));
        content11.setPaddingTop(20);
        content11.setPaddingBottom(20);
        content11.setBorder(0);
        content11.setBackgroundColor(titleColor);
        content11.setHorizontalAlignment(Element.ALIGN_CENTER);

        content1.addCell(content11);

        PdfPTable title2 = new PdfPTable(1);
        title2.setHorizontalAlignment(Element.ALIGN_LEFT);
        title2.setWidthPercentage(160 / 5.23f);
        title2.getDefaultCell().setBorder(0);
        title2.setWidths(titleClWidths);
        title2.addCell("Total Grace Entry");


        PdfPTable content2 = new PdfPTable(1);
        content2.setHorizontalAlignment(Element.ALIGN_LEFT);
        content2.setWidthPercentage(50 / 5.23f);

        PdfPCell content21 = new PdfPCell(new Phrase(String.valueOf(reportVO.getGraceTime()), font));
        content21.setPaddingTop(20);
        content21.setPaddingBottom(20);
        content21.setBorder(0);
        content21.setBackgroundColor(titleColor);
        content21.setHorizontalAlignment(Element.ALIGN_CENTER);
        content2.addCell(content21);

        PdfPTable title3 = new PdfPTable(1);
        title3.setHorizontalAlignment(Element.ALIGN_LEFT);
        title3.setWidthPercentage(160 / 5.23f);
        title3.getDefaultCell().setBorder(0);
        title3.setWidths(titleClWidths);
        title3.addCell("Beyond grace Entry");


        PdfPTable content3 = new PdfPTable(1);
        content3.setHorizontalAlignment(Element.ALIGN_LEFT);
        content3.setWidthPercentage(50 / 5.23f);

        PdfPCell content31 = new PdfPCell(new Phrase(String.valueOf(reportVO.getLateTime()), font));
        content31.setPaddingTop(20);
        content31.setPaddingBottom(20);
        content31.setBorder(0);
        content31.setBackgroundColor(titleColor);
        content31.setHorizontalAlignment(Element.ALIGN_CENTER);
        content3.addCell(content31);


        PdfPTable totalEntryTitle = new PdfPTable(1);
        float[] columnWidths = new float[]{20f};
        totalEntryTitle.setWidths(columnWidths);
        totalEntryTitle.getDefaultCell().setBorder(0);

        PdfPTable attList = new PdfPTable(5);
        float[] wdthCls = new float[]{27f, 13f, 22f, 15f, 25f};
        attList.setWidthPercentage(100);
        attList.setWidths(wdthCls);
        attList.setHorizontalAlignment(Element.ALIGN_CENTER);


        attList.addCell("Name");
        attList.addCell("Entry location");
        attList.addCell("Entry Date time");
        attList.addCell("Exit location");
        attList.addCell("Exit Date time");

        if (reportVO.getEntryExitList().size() != 0) {
            for (int i = 0; i < reportVO.getEntryExitList().size(); i++) {
                String name = "----";
                String inLocation = "----";
                String inTime = "-----";
                String outLocation = "----";
                String outTime = "----";

                if (reportVO.getEntryExitList().get(i).getEntry_view() != null) {
                    name = reportVO.getEntryExitList().get(i).getName();
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
        img.scaleToFit(1210, 660);
        img.setAbsolutePosition(50, 320);


        Image ilensLogo = Image.getInstance(scriptPath + "/report/logo.png");
        ilensLogo.scaleToFit(100, 100);
        ilensLogo.setAbsolutePosition(55, 790);

        document.add(ilensLogo);
        document.add(new Paragraph("\n"));
        document.add(title1);
        document.add(new Paragraph("\n"));
        document.add(content1);
        document.add(new Paragraph("\n"));
        document.add(title2);
        document.add(new Paragraph("\n"));
        document.add(content2);
        document.add(new Paragraph("\n"));
        document.add(title3);
        document.add(new Paragraph("\n"));
        document.add(content3);
        document.add(new Paragraph("\n"));
        document.add(img);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        document.add(attList);
        document.add(rect);

        PdfContentByte contentByte = writer.getDirectContent();
        contentByte.rectangle(rect);
        document.close();

        //ReportServices reportServices1 = new ReportServices();
        reportServices.putConfigs();
        log.info("Report Generated.");

        String subject = "iLens Attendance Report.";
        String msgContent = "<strong>Hi, </strong>" + "<strong>Email is generated by system.</strong>" +
                "<br>Please find attached complete attendance report for the selected dates between <strong>" +
                formatTime.format(reportPeriod.getPreviousDate()) + "</strong> to <strong>" + formatTime.format(new Date()) + "</strong>, as scheduled in the system."
                + "<br>If you have any queries, feel free to email <strong>admin@logicfocus.net</strong>."
                + "<br><br><br><strong>Regards<br>Admin</strong>";
        MailSend mailSend = new MailSend();
        mailSend.mailSend(host, port, username, password, reportPeriod.getMail(), subject, msgContent, scriptPath + "/report/report.pdf");
    }
}
