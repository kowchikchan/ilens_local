package com.pbs.tech.config;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.*;
import com.pbs.tech.common.HeaderFooterPageEvent;
import com.pbs.tech.model.ReportPeriod;
import com.pbs.tech.services.ChannelRunTime;
import com.pbs.tech.services.IlenService;
import com.pbs.tech.services.MailSend;
import com.pbs.tech.services.ReportServices;
import com.pbs.tech.vo.ReportGen1VO;
import com.pbs.tech.vo.ReportGenVO;
import com.pbs.tech.vo.ReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledReportSend {
    private static final Logger log = LoggerFactory.getLogger(ScheduledReportSend.class);

    private static final SimpleDateFormat formatTime = new SimpleDateFormat("dd MMMM yyyy hh.mm aa");

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

    String onTime;
    String graceTime;

    @Scheduled(cron = "0 1 0 * * *")
    public void sendReport() throws Exception {
        log.info("Report Triggered.");
        ReportPeriod reportPeriod = reportServices.getList();
        Date curDtTime = new Date();
        long diff = curDtTime.getTime() - reportPeriod.getPreviousDate().getTime();
        long differenceBetweenDts = diff / 1000 / 60 / 60 / 24;
        if (differenceBetweenDts == reportPeriod.getReportPeriod()){
            this.getPdf();
        }
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void scheduleRun() throws Exception {
        List<ChannelRunTime> runTimes = ilenService.getRuntimes();
        List<String> arr = new ArrayList<>();
        for(int i=0; i<runTimes.size();i++){
            arr.add(runTimes.get(i).getName());
        }
        for(int i=0; i<arr.size();i++) {
            ilenService.stopRunTime(arr.get(i));
            ilenService.startRuntime(arr.get(i));
        }
    }


    public PdfPTable tableCreation(String[] header, float[] columnWidth) throws DocumentException {
        PdfPTable table = null;
        Font font = null;
        table = new PdfPTable(header.length);
        table.setSpacingBefore(5.0f);
        table.setSpacingAfter(40.0f);
        table.setWidthPercentage(100);
        table.setWidths(columnWidth);
        table.setSpacingBefore(8.0f);
        font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE);
        this.getHeaders(table, font, header);
        return table;
    }

    private PdfPTable getHeaders(PdfPTable table, Font font, String[] header) {
        for (String heading : header) {
            Paragraph p = new Paragraph(heading, font);
            p.setLeading(1.5f);
            p.setKeepTogether(true);
            PdfPCell cell = new PdfPCell(p);
            cell.setPaddingLeft(5.0f);
            cell.setFixedHeight(35f);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidth(2);
            cell.setBorderColor(new BaseColor(255, 255, 255));
            cell.setBackgroundColor(new BaseColor(80, 80, 80));
            table.addCell(cell);
        }
        return table;
    }

    public PdfPTable addCell(String[] overView, PdfPTable table, Font font) {
        for (int i = 0; i < overView.length; i ++) {
            Paragraph p = new Paragraph(overView[i], font);
            p.setLeading(1.5f, 0.5f);
            p.setKeepTogether(true);
            PdfPCell cell = new PdfPCell(p);
            cell.setPaddingLeft(5.0f);
            cell.setBorderWidth(2);
            cell.setFixedHeight(35f);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderColor(new BaseColor(255, 255, 255));
            cell.setBackgroundColor(new BaseColor(232, 232, 232));
            if (i == 1) {
                cell.setBackgroundColor(this.compareTime(onTime, graceTime, overView[i]));
            }
            table.addCell(cell);
        }
        return table;
    }

    private BaseColor compareTime(String onTime, String graceTime, String entryTime){
        try {
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            Date parseEntryTime = dateFormat.parse(entryTime);
            Date parsedGraceTime = dateFormat.parse(graceTime);
            Date parsedOnTime = dateFormat.parse(onTime);
            if(parseEntryTime.after(parsedGraceTime)){
                return new BaseColor(245, 198, 203);
            }else if(parseEntryTime.after(parsedOnTime)){
                return new BaseColor(255, 238, 186);
            }else{
                return new BaseColor(195, 230, 203);
            }
        }catch (Exception e){
//       e.printStackTrace();
        }
        return new BaseColor(232, 232, 232);
    }



    @Async
    public void getPdf() throws Exception {
        ReportPeriod reportPeriod = reportServices.getList();
        long days = reportPeriod.getReportPeriod();
        Calendar cal = Calendar.getInstance();
        days -= 1;
        cal.add(Calendar.DATE, -(int)days);

        String dateFormat = "dd MMMM yyyy";
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);

        ReportVO reportVO = ilenService.totalEntries(reportPeriod.getReportPeriod());
        String scriptPath = System.getProperty("SCRIPT_PATH");
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 45, BaseColor.WHITE);
        Document document = new Document(PageSize.A3.rotate(), 60, 35, 140, 60);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(scriptPath + "/report/iLens Report - "+df.format(new Date())+".pdf"));


        Rectangle rect = new Rectangle(45, 40, 1160, 790);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);

        writer.setBoxSize("art", rect);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        event.startDate = cal.getTime();
        writer.setPageEvent(event);

        BaseColor onTimeColor = WebColors.getRGBColor("#117d2a");
        BaseColor graceTimeColor = WebColors.getRGBColor("#d0a71a");
        BaseColor beyondGraceEntry = WebColors.getRGBColor("#c63535");


        // open document.
        document.open();
        PdfContentByte contentByte = writer.getDirectContent();
        contentByte.rectangle(rect);
        document.add(rect);
        Paragraph pgp = new Paragraph("Summary", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY));
        pgp.setAlignment(Element.ALIGN_CENTER);
        document.add(pgp);

        // title
        PdfPTable title1 = new PdfPTable(1);
        title1.setHorizontalAlignment(Element.ALIGN_LEFT);
        title1.setWidthPercentage(160 / 5.23f);
        float[] titleClWidths = new float[]{2f};
        title1.getDefaultCell().setBorder(0);
        title1.setWidths(titleClWidths);
        title1.addCell("Total on time entry");

        PdfPTable content1 = new PdfPTable(1);
        content1.setHorizontalAlignment(Element.ALIGN_LEFT);
        content1.setWidthPercentage(50 / 5.23f);

        PdfPCell content11 = new PdfPCell(new Phrase(String.valueOf(reportVO.getTotalOnTime()), font));
        content11.setPaddingTop(20);
        content11.setPaddingBottom(20);
        content11.setBorder(0);
        content11.setBackgroundColor(onTimeColor);
        content11.setHorizontalAlignment(Element.ALIGN_CENTER);

        content1.addCell(content11);

        PdfPTable title2 = new PdfPTable(1);
        title2.setHorizontalAlignment(Element.ALIGN_LEFT);
        title2.setWidthPercentage(160 / 5.23f);
        title2.getDefaultCell().setBorder(0);
        title2.setWidths(titleClWidths);
        title2.addCell("Total grace entry");


        PdfPTable content2 = new PdfPTable(1);
        content2.setHorizontalAlignment(Element.ALIGN_LEFT);
        content2.setWidthPercentage(50 / 5.23f);

        PdfPCell content21 = new PdfPCell(new Phrase(String.valueOf(reportVO.getTotalGraceTime()), font));
        content21.setPaddingTop(20);
        content21.setPaddingBottom(20);
        content21.setBorder(0);
        content21.setBackgroundColor(graceTimeColor);
        content21.setHorizontalAlignment(Element.ALIGN_CENTER);
        content2.addCell(content21);

        PdfPTable title3 = new PdfPTable(1);
        title3.setHorizontalAlignment(Element.ALIGN_LEFT);
        title3.setWidthPercentage(160 / 5.23f);
        title3.getDefaultCell().setBorder(0);
        title3.setWidths(titleClWidths);
        title3.addCell("Beyond grace entry");


        PdfPTable content3 = new PdfPTable(1);
        content3.setHorizontalAlignment(Element.ALIGN_LEFT);
        content3.setWidthPercentage(50 / 5.23f);

        PdfPCell content31 = new PdfPCell(new Phrase(String.valueOf(reportVO.getTotalBeyondGraceTime()), font));
        content31.setPaddingTop(20);
        content31.setPaddingBottom(20);
        content31.setBorder(0);
        content31.setBackgroundColor(beyondGraceEntry);
        content31.setHorizontalAlignment(Element.ALIGN_CENTER);
        content3.addCell(content31);


        PdfPTable totalEntryTitle = new PdfPTable(1);
        float[] columnWidths = new float[]{20f};
        totalEntryTitle.setWidths(columnWidths);
        totalEntryTitle.getDefaultCell().setBorder(0);


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
        img.scaleToFit(1210, 1210);
        img.setAbsolutePosition(50, 200);


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


        document.newPage();
        Paragraph paragraph = null;
        document.add(new Paragraph("Detailed Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY)));
        font.setColor(BaseColor.DARK_GRAY);

        onTime = reportVO.getOnTime();
        graceTime = reportVO.getGraceTime();
        List<ReportGen1VO> attendanceList = reportVO.getAttendance();
        for (int j = 0; j < attendanceList.size(); j++) {
            contentByte.rectangle(rect);
            document.add(rect);
            paragraph = new Paragraph();
            paragraph.add(new Chunk("Date: ", new Font(Font.FontFamily.HELVETICA, 17, Font.NORMAL)));
            paragraph.add(new Chunk(attendanceList.get(j).getDate(), new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD)));
            paragraph.setSpacingAfter(30f);
            paragraph.setSpacingBefore(30f);
            document.add(paragraph);
            List<ReportGenVO> employeesList = attendanceList.get(j).getEmployees();
            PdfPTable overAllTable = this.tableCreation(new String[]{"Name", "Entry time", "Entry location", "Exit time", "Exit location"}, new float[]{30, 15, 20, 15, 20});
            overAllTable.setHeaderRows(1);
            font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.DARK_GRAY);
            for (int i = 0; i < employeesList.size(); i++) {
                ReportGenVO details = employeesList.get(i);
                this.addCell(new String[]{details.getName(), details.getEntryTime(), details.getEntryLocation(), details.getExitTime(), details.getExitLocation()}, overAllTable, font);
            }
            if(employeesList.size() == 0){
                font = FontFactory.getFont(FontFactory.HELVETICA, 20, BaseColor.DARK_GRAY);
                this.addCell(new String[]{"","","No Data Found","",""},overAllTable,font);
            }
            document.add(overAllTable);
            if (j != attendanceList.size()-1)
                document.newPage();
        }
        document.close();

        reportServices.putConfigs();
        log.info("Report generated.");

        String subject = " iLens automated attendance report.";
        String msgContent = "<strong>Hi, " + "<br></strong>Please find the attached report for the period from <strong>" +df.format(cal.getTime()) +" 00:00 AM" + "</strong> to <strong>" + formatTime.format(new Date()) + "</strong>."
                + "<br>Report generated date time: <strong>"+formatTime.format(new Date())+ "</strong>."
                + "<br>Thanks,"
                +"<br><strong>Note: </strong>This is system generated mail and report, for any clarification please reach out to admin@logicfocus.net.";
        MailSend mailSend = new MailSend();
        mailSend.mailSend(host, port, username, password, reportPeriod.getMail(), subject, msgContent, scriptPath + "/report/iLens Report - "+df.format(new Date())+".pdf");
    }
}
