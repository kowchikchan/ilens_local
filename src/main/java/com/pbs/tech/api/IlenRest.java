package com.pbs.tech.api;

import com.pbs.tech.services.IlenService;
import com.pbs.tech.vo.EntryExitFilter;
import com.pbs.tech.vo.ChannelData;
import com.pbs.tech.vo.UnknownFilterVO;
import com.pbs.tech.vo.UnknownInputVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/ilens")
public class IlenRest {

    @Autowired
    IlenService ilenService;

    @GetMapping("/entryexit")
    public ResponseEntity<?> getEntryExit(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getEntryExits(), HttpStatus.OK);
    }


    @GetMapping("/exitview")
   public ResponseEntity<?> getExit(@RequestHeader("CLIENT_KEY") String clientKey) {
        return new ResponseEntity(ilenService.getExitView(),HttpStatus.OK);
    }

    @PostMapping("/attendance/{pageNumber}")
    public ResponseEntity<?> getAttendance(@RequestHeader("CLIENT_KEY") String clientKey,
                                           @RequestBody EntryExitFilter entryExitFilter,
                                           @PathVariable int pageNumber) throws Exception {
        return new ResponseEntity(ilenService.getAttendance(entryExitFilter,pageNumber), HttpStatus.OK);
    }

    @GetMapping("/attendance/count")
    public ResponseEntity<?> getAttendanceCount(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getTodayCount(), HttpStatus.OK);
    }


    @PostMapping("/dataset")
    public ResponseEntity<?> getEntryExit(@RequestHeader("CLIENT_KEY") String clientKey,
                                          @RequestBody ChannelData channelData) throws Exception {
        ilenService.saveDataSet(channelData);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @GetMapping("/entryviolation")
    public ResponseEntity<?> getEntryViolation(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getEntryViolations(),HttpStatus.OK);
    }

    @GetMapping("/lasthourentry")
    public ResponseEntity<?> getLastHourEntry(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getLastOneHourEntry(),HttpStatus.OK);
    }

    @GetMapping("/ontimeentry")
    public ResponseEntity<?> getOnTime(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getOnTimeEntry(),HttpStatus.OK);
    }

    @GetMapping("/gracetimeentry")
    public ResponseEntity<?> getGraceTime(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getGraceTimeEntry(),HttpStatus.OK);
    }

    @GetMapping("/average")
    public ResponseEntity<?> getMonthAverage(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getAverageOnTillDate(),HttpStatus.OK);
    }

    @GetMapping("/everytenminutes")
    public ResponseEntity<?> getEveryTenMinutesValues(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getEveryTenMinutes(),HttpStatus.OK);
    }

    @GetMapping("/sixmonthaverage")
    public ResponseEntity<?> getEverySixMinutesValues(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getAverageOnSixMonth(),HttpStatus.OK);
    }

    @GetMapping("/entryviolationbylocation")
    public ResponseEntity<?> getEntryViolationByLocation(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getEntryViolationsByLoc(),HttpStatus.OK);
    }
    @GetMapping("/entryviolationtotalcount")
    public ResponseEntity<?> getEntryViolationCount(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity(ilenService.getEntryViolationsCount(),HttpStatus.OK);
    }

    @GetMapping("/attendance/snapshot/{snapshot}/{type}")
    public ResponseEntity<?> getSnapshot(@RequestHeader("CLIENT_KEY") String clientKey,
                                         @PathVariable String snapshot, @PathVariable String type) throws IOException {
        return new ResponseEntity(ilenService.attendanceSnapshot(snapshot, type), HttpStatus.OK);
    }

    @GetMapping("/runtime")
    public ResponseEntity<?> getRunTimes(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(ilenService.getRuntimes(), HttpStatus.OK);
    }

    @GetMapping("/attendance/lateEntry")
    public ResponseEntity<?> lateEntry(@RequestHeader("CLIENT_KEY") String clientKey) throws Exception {
        return new ResponseEntity<>(ilenService.getLateEntry(), HttpStatus.OK);
    }

    @PostMapping("/attendance/filter")
    public ResponseEntity<?> attendanceFilter(@RequestHeader("CLIENT_KEY") String clientKey,
                                              @RequestBody EntryExitFilter entryExitFilter) throws Exception {
        return new ResponseEntity<>(ilenService.attendanceFilter(entryExitFilter), HttpStatus.OK);
    }

    @PostMapping("/unknown/save")
    public ResponseEntity<?> unknownSaveDataSet(@RequestHeader("CLIENT_KEY") String clientKey,
                                            @RequestBody UnknownInputVO unknownFilterVO) throws Exception {
            ilenService.unknownSaveDataset(unknownFilterVO);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @PostMapping("/unknown/getList/{pageNumber}")
    public ResponseEntity<?> unknownList(@RequestHeader("CLIENT_KEY") String clientKey,
                                                @RequestBody UnknownFilterVO unknownFilterVO,
                                         @PathVariable long pageNumber) throws Exception {

        return new ResponseEntity<>(ilenService.unknownList(unknownFilterVO, pageNumber), HttpStatus.OK);
    }
    @GetMapping("/unknown/count/{date}")
    public ResponseEntity<?> unknownCount(@RequestHeader("CLIENT_KEY") String clientKey,
                                         @PathVariable String date) throws Exception {
        return new ResponseEntity<>(ilenService.unknownCount(date), HttpStatus.OK);
    }
}

