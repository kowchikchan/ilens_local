package com.pbs.tech.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbs.tech.common.SearchCriteria;
import com.pbs.tech.common.exception.IlensException;
import com.pbs.tech.model.*;
import com.pbs.tech.model.big.EntryExitEntity;
import com.pbs.tech.model.big.EntryViolation;
import com.pbs.tech.model.big.UnknownEntry;
import com.pbs.tech.repo.*;
import com.pbs.tech.repo.big.*;
import com.pbs.tech.model.big.ExitView;
import com.pbs.tech.vo.*;
import com.pbs.tech.vo.runtime.EntryExitVo;
import com.pbs.tech.vo.EntryViolationByLocationVo;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class IlenService {

    Logger log = LoggerFactory.getLogger(IlenService.class);

    static List<ChannelRunTime> runtimes = new ArrayList<>();
    private static final String dateFormatForDb = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateWithDayFormat = new SimpleDateFormat("dd MMMM yyyy-EEEE");
    private static final SimpleDateFormat timeFormatOnly = new SimpleDateFormat("hh:mm a");
    private static final String os = System.getProperty("os.name");

    @Autowired
    EntryExitRepo entryExitRepo;

    @Autowired
    ExitViewRepo exitViewRepo;

    @Autowired
    ChannelRepo channelRepo;

    @Autowired
    EntryViolationRepo entryViolationRepo;

    @Autowired
    AccessConfigRepo accessConfigRepo;

    @Autowired
    ChannelsServices channelsServices;

    @Autowired
    UnknownEntryRepo unknownEntryRepo;

    @Autowired
    UserRepo userRepo;

    @Value("${ilens.api.protocal}")
    String protocal;

    @Value("${ilens.api.host}")
    String host;

    @Value("${ilens.api.port}")
    String port;

    @Value("${ilens.api.path}")
    String apiPath;

    @Value("${ilens.python.path}")
    String pythonPath;

    @Value("${locations.data-location}")
    String dataLocation;

    @Value("${locations.json-location}")
    String configsJsonPath;

    @Value("${locations.tessearct-location}")
    String tesseractLocation;

    @Value("${locations.unknown-location}")
    String unknownLocation;

    @Value("${ilens.user.train-data.path}")
    String trainingImagePath;

    @Autowired
    private CassandraOperations cassandraTemplate;

    @Autowired
    PeopleCountRepo peopleCountRepo;

    @Autowired
    FRConfigRepo frConfigRepo;

    @Autowired
    NprConfigRepo nprConfigRepo;

    @Autowired
    DataApiRepo dataApiRepo;

    @Autowired
    ConfigurationServices configurationServices;

    @Autowired
    FirebaseMessagingServices firebaseMessagingServices;

    @Autowired
    WeekDayServices weekDayServices;

    public void startRuntime(String id) throws Exception {
        HashMap<String, String> usersList = new HashMap<>();
        //TODO: check if already running.
        if (isRunning(id)) {
            throw new IlensException("Arleady Running");
        }
        String scriptPath = System.getProperty("SCRIPT_PATH");
        Channel channel = channelRepo.findById(Long.valueOf(id)).get();
        String entryOrExit = null;
        if (channel.isEntry()){
            entryOrExit = "entry";
        }if(channel.isExit()){
            entryOrExit = "exit";
        }
/*        vo.setIp(channel.getIp());
        vo.setName(channel.getName());
        vo.setApi(protocal + "://" + host + ":" + port + "/" + apiPath);
        // TODO : get config from entry .
        Execution execution = new Execution();
        execution.setType("fr");
        execution.setConfig(entryOrExit);
        List<Execution> executions = new ArrayList<>();
        executions.add(execution);
        vo.setExecutions(executions);
        String config = channel.getConfig();*/


        // get data api and client key
        DataApi dataApi = null;
        Configurations configurations = null;
        try {
            dataApi = dataApiRepo.findById(Long.valueOf(0)).get();
            configurations = configurationServices.getList();
        }catch (IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("Index Error " + e.getMessage());
        }
        try {
            List<User> userObj = (List<User>) userRepo.findAll();
            for (User user : userObj) {
                usersList.put(user.getUsername().toLowerCase(), user.getFirstName() + " " + user.getLastName());
            }
        }catch (NoSuchElementException e){
            throw new NoSuchElementException(e.getMessage());
        }

        JSONObject configJson = new JSONObject();
        JSONObject userListJson = new JSONObject();
        userListJson.put("usersList", usersList);
        configJson.put("videoSave", configurations.isVideoStatus());
        configJson.put("dataApi", dataApi.getDataApi());
        configJson.put("reportApi", dataApi.getReportApi());
        configJson.put("apiToken", dataApi.getApiToken());
        configJson.put("ip", channel.getIp().toString());
        configJson.put("id", id);

        ArrayList<Object> executionsConfigs = new ArrayList<>();
        if(channel.isFrEnabled()){
            JSONObject frConfigJson = new JSONObject();
            try {
                // fr configurations
                FRConfig frConfig = frConfigRepo.findById(Long.valueOf(id)).get();
                frConfigJson.put("type", "fr");
                frConfigJson.put("config",  entryOrExit);
                executionsConfigs.add(frConfigJson);
            }catch (NoSuchElementException e){
                throw new NoSuchElementException("No Such Element" + e.getMessage());
            }
        }if(channel.isNprEnabled()) {
            JSONObject nprConfigJson = new JSONObject();
            JSONObject nprDimensionConfigs = new JSONObject();
            try {
                // npr configurations
                NprConfig nprConfig = nprConfigRepo.findById(Long.valueOf(id)).get();
                nprConfigJson.put("type", "npr");
                nprDimensionConfigs.put("minRatio", nprConfig.getMinRatio());
                nprDimensionConfigs.put("maxRatio", nprConfig.getMaxRatio());
                nprDimensionConfigs.put("thresh1", nprConfig.getMinKernel());
                nprDimensionConfigs.put("thresh2", nprConfig.getMaxKernel());
                nprConfigJson.put("config", nprDimensionConfigs);
                executionsConfigs.add(nprConfigJson);
            }catch (NoSuchElementException e){
                throw new NoSuchElementException("No Such Element " + e.getMessage());
            }
        }
        configJson.put("executions" , executionsConfigs);
        configJson.put("name", channel.getName().replace(" ", ""));
        configJson.put("tesseract", tesseractLocation);
        try {
            //save configurations as json
            String filePath = configsJsonPath + "/" + id + ".json";
            FileWriter file = new FileWriter(filePath);
            file.write(String.valueOf(configJson));
            file.close();

            //save user list as json.
            FileWriter userList = new FileWriter(configsJsonPath + "/userList.json");
            userList.write(String.valueOf(userListJson));
            userList.close();

            //set permission.
            File filePermission = new File(filePath);
            filePermission.setExecutable(true);
            filePermission.setReadable(true);
            filePermission.setWritable(true);

            //check file permission.
            log.info("Read : {}", filePermission.canRead());
            log.info("Write : {}", filePermission.canWrite());
            log.info("Execute : {}", filePermission.canExecute());

        } catch (IOException e) {
            throw new IOException("IO Exception {}" + e.getMessage());
        }
        /*String s = null;
        script executing command.*/
        /*String executeCmd = pythonPath + " " +scriptPath + "/main.py" + " -i " + configsJsonPath + "/" + id + ".json"
                + " -b " + scriptPath + " -d "+ dataLocation;
        log.info("CMD {}", executeCmd);
        Process p = Runtime.getRuntime().exec(executeCmd);
        log.info("process id {}", p.pid());*/
        long pid = 0;
        //try {
        ProcessBuilder builder = new ProcessBuilder(pythonPath, scriptPath + "/main.py", "-i", configsJsonPath +
                "/" + id + ".json", "-b", scriptPath, "-d", dataLocation);
        log.info("CMD:"+String.join(" ",builder.command()));
        Process process = builder.start();
        pid = process.pid();
        /*}catch (Exception e){
            throw new Exception("Exception " + e.getMessage());
        }*/

        // add run time.
        ChannelRunTime runTime = new ChannelRunTime(id);
        runTime.setPid(pid);
        runtimes.add(runTime);

        /*// read output.
        BufferedReader in  = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            throw new InterruptedException("Exception {}" + e.getMessage());
        }
        while (in.ready()){
            log.info("Output : {}" , in.readLine());
        }
        // read, if error occurred.
        BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((s = stderr.readLine()) != null) {
            log.error("Error : {}", s);
        }
        stderr.close();*/

/*        try {
            log.info("Start with {}", configJson);
            ProcessBuilder builder = new ProcessBuilder(pythonPath, scriptPath + "/main.py","-s", jsonPath + "/" +id +".json", "-i",  "\""+ configJson.toString() + "\"", "-b", "\"" + scriptPath + "/faceDetection/" + "\""  , "-d", "\""+dataLocation+"\"");
            long pid = 0;
            Process process = builder.start();
            pid = process.pid();

            log.info("CMD:"+String.join(" ",builder.command()));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //process.waitFor();
            log.info("PID:" + pid);
            ChannelRunTime runTime = new ChannelRunTime(id);
            runTime.setPid(pid);
            runtimes.add(runTime);
            StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
            reader.lines().iterator().forEachRemaining(sj::add);
            result = sj.toString();
            log.info("OUTPUT : {}", result);
/*            PrintWriter gp = new PrintWriter(process.getOutputStream());
            gp.print(String.join(" ", builder.command()));
            gp.close();
        } catch (IOException e) {
            throw new IlensException("Error", e);
        }*/

    }

    private boolean isRunning(String id) {
        for (ChannelRunTime runtime : runtimes) {
            if (runtime.getName().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public List<ChannelRunTime> getRuntimes() {
        //Check if already ilen process are running.
        /*   try {
                String line;
                Process p = Runtime.getRuntime().exec("ps -ef | grep ilen.sh");
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    System.out.println(line); //<-- Parse data here.
                }
                input.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        */
        return runtimes;
    }

    public void stopRunTime(String id) throws IOException {
        int removeIndex = -1;
        for (ChannelRunTime runtime : runtimes) {
            if (runtime.getName().equalsIgnoreCase(id)) {
                long pid = runtime.getPid();
                log.info("Killing pid : {}", pid);
                String cmd = "";
                if(os.toLowerCase().contains("nux") || os.toLowerCase().contains("nix")){
                    cmd = "kill -9 " + pid;
                }
                if(os.toLowerCase().contains("mac")){
                    cmd = "kill -9 " + pid;
                }
                if(os.toLowerCase().contains("win")){
                    cmd = "taskkill /F /PID " + pid;
                }
                try{
                    Runtime.getRuntime().exec(cmd);
                }catch(IOException e){
                    throw new IOException("Exception in kill" +e.getMessage());
                }
                removeIndex = runtimes.indexOf(runtime);
            }
        }
        try {
            if (removeIndex != -1) {
                runtimes.remove(removeIndex);
            }
        }catch (IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("Exception {}" + e.getMessage());
        }
    }

    SimpleDateFormat dt = new SimpleDateFormat("ddMMyyyyHHmmss");


    public void saveDataSet(ChannelData channelData) throws Exception {
        boolean x = true;
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info(mapper.writeValueAsString(channelData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (channelData.getEntryExit() != null) {
            for (EntryExitVo entryExit : channelData.getEntryExit()) {
                EntryExitEntity entryExist = new EntryExitEntity();
                EntryViolation entryViolation = new EntryViolation();
                Date now = null;
                try {
                    now = dt.parse(channelData.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                entryExist.setTime(now);
                entryExist.setType(channelData.getType());
                entryExist.setId(entryExit.getId());
                entryExist.setName(entryExit.getName());
                entryExist.setLocation(Long.toString(channelData.getChannelId()));
                entryExist.setSnapshot(channelData.getSnapshot());

                // save person details, if violated.
                User user;
                Channel channel;
                try {
                    user = userRepo.findByUsername(entryExist.getId());
                    channel = channelRepo.findById(channelData.getChannelId()).get();
                }catch (Exception e){
                    throw new Exception("No user/channel found "+ e.getMessage());
                }
                AccessConfigs accessConfigs = accessConfigRepo.findByChannelIdAndPersonId(channelData.getChannelId(),
                            String.valueOf(user.getId()));
                if (accessConfigs == null && channel.isAccessEnabled()) {
                    entryViolation.setTime(now);
                    entryViolation.setType(channelData.getType());
                    entryViolation.setId(entryExit.getId());
                    entryViolation.setName(entryExit.getName());
                    entryViolation.setLocation(channelData.getChannelName());
                    entryViolation.setSnapshot(channelData.getSnapshot());
                    entryViolationRepo.save(entryViolation);
                    String body = "["+entryExit.getId()+"] "+entryExit.getName() + " has entered " + channelData.getChannelName();
                    firebaseMessagingServices.sendNotification("Violation", body,
                            "dsWwKEq9T5aR_uiuJlRXaW:APA91bE8fCkLtydbxdPr0u4-5Yy7wGiHkPaX7nMsNyi7L3bPw-KXUsTw76M1SUEV7z1TlmvAEf3uPxY6fEe8BKqpx_ZnYwE1FbdFx2bd9g8mxatlJ23eGpE5GldgaAoRG5Y0MjKqmnBV");
                }
                entryExitRepo.save(entryExist);
            }
        }
        if(channelData.getPeopleCount() != null){
            try {
                FRConfig frConfigValues = frConfigRepo.findByChannelId(channelData.getChannelId());
                if(!(frConfigValues.getMinimumPeople() >= channelData.getPeopleCount().getCount() &&
                        frConfigValues.getMaximumPeople() <= channelData.getPeopleCount().getCount())) {
                    channelData.getPeopleCount().setViolation("MIN_VIOLATION["+ frConfigValues.getMinimumPeople() +"]"+
                                                            "\nMAX_VIOLATION["+frConfigValues.getMaximumPeople() + "]");
                }
                peopleCountRepo.save(channelData.getPeopleCount());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public List<EntryExitEntity> getEntryExits() {
        Calendar date = new GregorianCalendar();
        int currpage = 0, size = 2;
        Integer page = 1;
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        // final PageRequest pageRequest = PageRequest.of(1, 2);
        Slice<EntryExitEntity> slice = entryExitRepo.getTodayAttendance(date.getTime(), CassandraPageRequest.first(10));
        while (slice.hasNext() && currpage < page) {
            slice = entryExitRepo.getTodayAttendance(date.getTime(), slice.nextPageable());
            currpage++;
        }
        return slice.getContent();
    }

    public List<EntryExitEntity> getTodayAttendance(int pageNUmber) {
        Calendar date = new GregorianCalendar();
        int currpage = 0, size = 2;
        Integer page = pageNUmber;
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        // final PageRequest pageRequest = PageRequest.of(1, 2);
        Slice<EntryExitEntity> slice = entryExitRepo.getTodayAttendance(date.getTime(), CassandraPageRequest.first(10));
        while (slice.hasNext() && currpage < page) {
            slice = entryExitRepo.getTodayAttendance(date.getTime(), slice.nextPageable());
            currpage++;
        }
        return slice.getContent();
    }

    public List<ExitView> getExitView() {
        Calendar date = new GregorianCalendar();
        int currpage = 0, size = 2;
        Integer page = 1;
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        // final PageRequest pageRequest = PageRequest.of(1, 2);
        Slice<ExitView> slice = exitViewRepo.getExitView(date.getTime(), CassandraPageRequest.first(10));
        while (slice.hasNext() && currpage < page) {
            slice = exitViewRepo.getExitView(date.getTime(), slice.nextPageable());
            currpage++;
        }
        return slice.getContent();
    }

    public List<ExitView> getTodayExit(int pagenumber,String id) {
        Calendar date = new GregorianCalendar();
        int currpage = 0, size = 2;
        Integer page = pagenumber;
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Slice<ExitView> slice = exitViewRepo.getTodayExit(date.getTime(), id,CassandraPageRequest.first(10));
        while (slice.hasNext() && currpage < page) {
            slice = exitViewRepo.getTodayExit(date.getTime(),id,slice.nextPageable());
            currpage++;
        }
        return slice.getContent();
    }

    public long getTodayCount() {
        Calendar date = new GregorianCalendar();
        Date startDate = this.getDayStTime(date.getTime());
        Date endDate = this.getDayEndTime(date.getTime());
        return entryExitRepo.getTodayAttendanceCount(startDate, endDate).size();
    }

    public  Object getAttendance(@RequestBody EntryExitFilter entryExitFilter, int pageNumber) throws Exception {
        List<EntryExit> entryExits = new ArrayList<>();

        if(!StringUtils.isBlank(entryExitFilter.getId())){

            // Person detailed report by id
            SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
            IdTraceVO traceVO = new IdTraceVO();
            String selectedDate = df.format(this.getDayStTime(entryExitFilter.getDate()));
            String endDate = df.format(this.getDayEndTime(entryExitFilter.getDate()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT * FROM ilens.EntryExit WHERE time >= " + "'" + selectedDate + "'" + " AND time<= "
                    + "'" + endDate + "'" +" AND id=" + "'" + entryExitFilter.getId() +"'" + " ALLOW FILTERING");
            log.info("Query {}, ", stringBuilder.toString());
            try {
                List<EntryExitEntity> slice = cassandraTemplate.select(stringBuilder.toString(), EntryExitEntity.class);
                List<IdTraceDetailsVO> idTraceDetailsVOList = new ArrayList<>();
                for (EntryExitEntity entryExitEntity : slice) {
                    traceVO.setId(entryExitEntity.getId());
                    traceVO.setName(entryExitEntity.getName());
                    IdTraceDetailsVO idTraceDetailsVO = new IdTraceDetailsVO();
                    idTraceDetailsVO.setTime(entryExitEntity.getTime());
                    idTraceDetailsVO.setType(entryExitEntity.getType());
                    Channel ch = this.getChannelDetailsById(Long.parseLong(entryExitEntity.getLocation()));
                    idTraceDetailsVO.setChannelId(ch.getName());
                    //idTraceDetailsVO.setChannelId(entryExitEntity.getLocation());
                    idTraceDetailsVO.setSnapshot(entryExitEntity.getSnapshot());
                    idTraceDetailsVOList.add(idTraceDetailsVO);
                }
                traceVO.setTrace(idTraceDetailsVOList);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("No Such Element Exception" + e.getMessage());
            }
            return traceVO;
        }else {
            //If filter values IDLE.
            if ((entryExitFilter.getDate() == null || StringUtils.isEmpty(entryExitFilter.getDate().toString())) &&
                    (entryExitFilter.getName() == null || StringUtils.isEmpty(entryExitFilter.getName())) &&
                    (entryExitFilter.getLocation() == null || StringUtils.isEmpty(entryExitFilter.getLocation()))) {
                try {
                    log.info("Without filter...");
                    List<EntryExitEntity> entities = this.getTodayAttendance(pageNumber);
                    //List<EntryExit> entryExits = new ArrayList<>(entities.size());
                    for (int val = 0; val < getTodayCount(); val++) {
                        List<ExitView> exitDetails = this.getTodayExit(pageNumber, entities.get(val).getId());
                        EntryExit entryExit = new EntryExit();
                        //entryExit.setId(entities.get(val).getId());
                        /*String name = "----";
                        try {
                            User userObj = userRepo.findById(Long.parseLong(entities.get(val).getId())).get();
                            name = String.join(" ", userObj.getFirstName(), userObj.getLastName());
                            entryExit.setName(name);
                            entryExit.setId(userObj.getUsername());
                        } catch (NoSuchElementException noSuchElementException) {
                            entryExit.setName(name);
                            log.info("Error {}", noSuchElementException.getMessage());
                        }*/
                        entryExit.setId(entities.get(val).getId());
                        entryExit.setName(entities.get(val).getName());
                        entryExit.setEntry_view(entities.get(val));
                        entryExit.setExit_view(exitDetails);
                        entryExits.add(entryExit);
                    }
                } catch (Exception exception) {
                    throw new Exception(exception.getMessage());
                }
            } else {

                //If filter values Presenting.
                log.info("With filter...");
                List<SearchCriteria> specificationValues = new ArrayList<>();
                if (entryExitFilter.getDate() != null && !StringUtils.isEmpty(entryExitFilter.getDate().toString())) {

                    //selected date
                    SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
                    String selectedDate = df.format(this.getDayStTime(entryExitFilter.getDate()));

                    //selected date end time.
                    String endDate = df.format(this.getDayEndTime(entryExitFilter.getDate()));

                    //start time.
                    specificationValues.add(new SearchCriteria("time", ">=", selectedDate));

                    //end time.
                    specificationValues.add(new SearchCriteria("time", "<=", endDate));
                }
                if (entryExitFilter.getLocation() != null && !StringUtils.isEmpty(entryExitFilter.getLocation())) {
                    //location
                    specificationValues.add(new SearchCriteria("location", "=", entryExitFilter.getLocation()));
                }
                if (entryExitFilter.getName() != null && !StringUtils.isEmpty(entryExitFilter.getName())) {
                    //name
                    specificationValues.add(new SearchCriteria("name", "=", entryExitFilter.getName()));
                }
                //generate query.
                StringBuilder stringBuilder = new StringBuilder();
                for (int k = 0; k < specificationValues.size(); k++) {
                    if (k == 0) {
                        stringBuilder.append("SELECT * FROM ilens.EntryExit WHERE ");
                    }
                    stringBuilder.append(specificationValues.get(k).getKey() + " " + specificationValues.get(k).getOperation() + " " + "'" + specificationValues.get(k).getValue() + "'");
                    if (!(k == specificationValues.size() - 1)) {
                        stringBuilder.append(" " + "AND" + " ");
                    }
                    if (k == specificationValues.size() - 1) {
                        stringBuilder.append(" " + "AND type='entry' group by id ALLOW FILTERING");
                    }
                }
                try {
                    log.info("Generated Query : " + stringBuilder);
                    List<EntryExitEntity> entities = cassandraTemplate.select(stringBuilder.toString(), EntryExitEntity.class);
                    for (EntryExitEntity entity : entities) {
                        List<ExitView> exitDetails = this.exitData(entity.getId(), entryExitFilter.getDate());
                        EntryExit entryExit = new EntryExit();
                        entryExit.setId(entity.getId());
                        entryExit.setName(entity.getName());
                        try {
                            Channel ch = this.getChannelDetailsById(Long.parseLong(entity.getLocation()));
                            entity.setLocation(ch.getName());

                            // exit
                            if(exitDetails.size() > 0){
                                Channel ch1 = this.getChannelDetailsById(Long.parseLong(exitDetails.get(0).getLocation()));
                                exitDetails.get(0).setLocation(ch1.getName());
                            }
                        }catch (Exception e){
                            log.info("no channel found");
                        }
                        entryExit.setEntry_view(entity);
                        entryExit.setExit_view(exitDetails);
                        entryExits.add(entryExit);
                    }
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            }
            return entryExits;
        }
    }

    public Channel getChannelDetailsById(long id) throws Exception {
        Channel channel = null;
        try {
            channel = channelRepo.findById(id).get();
            return channel;
        }catch (Exception e){
            throw new Exception("no channel found");
        }
    }

    public List<ExitView> exitData(String id, Date selectedDate){
        SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
        List<ExitView> exitView = new ArrayList<>();

        // start time
        String startDt = df.format(this.getDayStTime(selectedDate));

        // end time
        String endDt = df.format(this.getDayEndTime(selectedDate));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM ilens.ExitView WHERE time>=" + "'" + startDt + "'" + " AND time<="
                + "'" + endDt + "'" +" AND id=" + "'" + id +"'" + " AND type='exit' group by id ALLOW FILTERING");
        log.info("Query {}, ", stringBuilder.toString());
        try {
            List<ExitView> slice = cassandraTemplate.select(stringBuilder.toString(), ExitView.class);
            for (ExitView entity : slice){
                ExitView exitView1 = new ExitView();
                exitView1.setId(entity.getId());
                exitView1.setName(entity.getName());
                Channel ch = this.getChannelDetailsById(Long.parseLong(entity.getLocation()));
                exitView1.setLocation(ch.getName());
                exitView1.setSnapshot(entity.getSnapshot());
                exitView1.setTime(entity.getTime());
                exitView1.setType(entity.getType());
                exitView.add(exitView1);
            }
        }catch (Exception e){
            log.info("Exception {}", e.getMessage());
        }
        return exitView;
    }

    public List<EntryViolation> getEntryViolations() {
        Calendar date = new GregorianCalendar();
        int currpage = 0, size = 2;
        Integer page = 1;
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        // final PageRequest pageRequest = PageRequest.of(1, 2);
        Slice<EntryViolation> slice = entryViolationRepo.getEntryViolations(date.getTime(), CassandraPageRequest.first(10));
        while (slice.hasNext() && currpage < page) {
            slice = entryViolationRepo.getEntryViolations(date.getTime(), slice.nextPageable());
            currpage++;
        }
        return slice.getContent();
    }

    public long getLastOneHourEntry() {
        Calendar date = new GregorianCalendar();
        //get current time.
        Date currentTime = date.getTime();
        //get past time
        date.add(Calendar.HOUR_OF_DAY,-1);
        Date pastOneHourTime = date.getTime();
        Iterable slice = entryExitRepo.getLastHourEntryOrOnTimeEntry(pastOneHourTime,currentTime);

        return IterableUtils.size(slice);
    }

    public long getOnTimeEntry() {
        Calendar date = new GregorianCalendar();
        Configurations configuration = configurationServices.getList();
        String[] onTimeList = configuration.getOnTime().split(" ")[0].split(":");

        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date today = date.getTime();
        //set limit on 8'o clock.
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(onTimeList[0]));
        date.set(Calendar.MINUTE, Integer.parseInt(onTimeList[1]));
        Date onTime = date.getTime();
        Iterable slice = entryExitRepo.getLastHourEntryOrOnTimeEntry(today,onTime);

        return IterableUtils.size(slice);
    }

    public long getGraceTimeEntry() {
        Calendar date = new GregorianCalendar();
        Configurations configuration = configurationServices.getList();
        String[] onTimeList = configuration.getOnTime().split(" ")[0].split(":");
        String[] graceTimeList = configuration.getGraceTime().split(" ")[0].split(":");

        //set onTime.
        date.set(Calendar.HOUR_OF_DAY,Integer.parseInt(onTimeList[0]));
        date.set(Calendar.MINUTE, Integer.parseInt(onTimeList[1]));
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date onTime = date.getTime();

        //Set graceTime.
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(graceTimeList[0]));
        date.set(Calendar.MINUTE,Integer.parseInt(graceTimeList[1]));
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Date graceTime = date.getTime();
        Iterable slice = entryExitRepo.getLastHourEntryOrOnTimeEntry(onTime,graceTime);
        return IterableUtils.size(slice);
    }

    public float getAverageOnTillDate() {
        Calendar date = new GregorianCalendar();
        List<UserVo> userVos = userRepo.findByAll();
        int sum = 0;
        //get current date.
        int currentDate =date.get(Calendar.DATE);

        // configurations data.
        Configurations configuration = configurationServices.getList();
        String[] onTimeList = configuration.getOnTime().split(" ")[0].split(":");

        //Find on time entry on each data.
        for(int i=1;i<=currentDate;i++) {
            //reset all values.
            date.set(Calendar.DATE, i);
            date.set(Calendar.HOUR_OF_DAY,0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            Date monthTime = date.getTime();

            //set limit on 8'o clock.
            date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(onTimeList[0]));
            date.set(Calendar.MINUTE,Integer.parseInt(onTimeList[1]));
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            Date onTime = date.getTime();
            try {
                Iterable slice = entryExitRepo.getLastHourEntryOrOnTimeEntry(monthTime, onTime);
                sum = sum + IterableUtils.size(slice);
            }catch (Exception e){
                log.info("No Such Data in Date {}", monthTime);
            }
        }
        //return average on till the data.
        return Math.round(sum/(float)userVos.size());
    }

    public List<EveryTenMinutesVo> getEveryTenMinutes() {
        Calendar date = new GregorianCalendar();
        ArrayList<EveryTenMinutesVo> barChartValues = new ArrayList<>();
        //Every Ten minutes data..
        date.add(Calendar.HOUR_OF_DAY, -1);
        Date pastOneHourTime = date.getTime();
        for (int i = 0; i <= 5; i++) {
            EveryTenMinutesVo everyTenMinutesVo = new EveryTenMinutesVo();
            date.add(Calendar.MINUTE, 10);
            Date addTenMinutesWithPastOneHour = date.getTime();
            Iterable firstTenMinutes = entryExitRepo.getEveryTenMinutes(pastOneHourTime, addTenMinutesWithPastOneHour);
            //swap values.
            pastOneHourTime = addTenMinutesWithPastOneHour;
            //set values to the VO.
            everyTenMinutesVo.setTime(addTenMinutesWithPastOneHour);
            everyTenMinutesVo.setCount(IterableUtils.size(firstTenMinutes));
            barChartValues.add(everyTenMinutesVo);
        }
        return barChartValues;
    }

    public List<SixMonthAverageVo> getAverageOnSixMonth() {
        SimpleDateFormat df = new SimpleDateFormat("MMM yyyy");
        ArrayList<SixMonthAverageVo> totalSixMonthAverage = new ArrayList<>();
        Calendar date = new GregorianCalendar();

        // configurations data.
        Configurations configuration = configurationServices.getList();
        String[] onTimeList = configuration.getOnTime().split(" ")[0].split(":");

        //Find total number of users.
        List<UserVo> userVos = userRepo.findByAll();
        int sum = 0;

        //get before six month data.
        date.add(Calendar.MONTH,-6);

        //Loop over last six month.
        for(int j = 0; j <= 5; j++ ) {
            SixMonthAverageVo sixMonthAverageVo = new SixMonthAverageVo();
            int currentMonthLength = date.getActualMaximum(Calendar.DAY_OF_MONTH);
            //loop over all values in month within onTime.
            for (int i = 1; i <= currentMonthLength; i++) {
                //reset all values.
                date.set(Calendar.DATE, i);
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                Date monthTime = date.getTime();

                //set limit on 8'o clock.
                date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(onTimeList[0]));
                date.set(Calendar.MINUTE,Integer.parseInt(onTimeList[1]));
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                Date onTime = date.getTime();
                try {
                    Iterable slice = entryExitRepo.getLastHourEntryOrOnTimeEntry(monthTime, onTime);
                        sum = sum + IterableUtils.size(slice);
                } catch (Exception e) {
                    log.info("No Such Data in Date {}", monthTime);
                }
                sixMonthAverageVo.setMonth(df.format(onTime));
            }
            long average = Math.round(sum/(float)userVos.size());
            //set values to vo.
            sixMonthAverageVo.setAverage(average);
            totalSixMonthAverage.add(sixMonthAverageVo);
            //add month by one.
            date.add(Calendar.MONTH,1);
        }
        return totalSixMonthAverage;
    }

    public List<EntryViolationByLocationVo> getEntryViolationsByLocation() {
        ArrayList<EntryViolationByLocationVo> totalEntryViolationValues = new ArrayList<>();
        Map<String, Integer> locationWithCount = new HashMap<>();
        int count = 0;
        //get EntryViolation Values.
        List<EntryViolation> val = getEntryViolations();
        //loop over all the data.
        for (int k = 0;k < val.size();k++){
            String str = val.get(k).getLocation();
            if (locationWithCount.containsKey(str)) {
                locationWithCount.put(str, locationWithCount.get(str) + 1);
            } else {
                locationWithCount.put(str, 1);
            }
        }
        for (Map.Entry<String, Integer> entry : locationWithCount.entrySet()) {
            EntryViolationByLocationVo entryViolationByLocationVo = new EntryViolationByLocationVo();
            entryViolationByLocationVo.setLocation(entry.getKey());
            entryViolationByLocationVo.setCount(entry.getValue());
            totalEntryViolationValues.add(entryViolationByLocationVo);
        }
        return totalEntryViolationValues;

    }

    public List<EntryViolationByLocationVo> getEntryViolationsByLoc() {
        ArrayList<EntryViolationByLocationVo> totalEntryViolationValues = new ArrayList<>();
        ArrayList<String> totalLocationsList = new ArrayList<>();
        Calendar date = new GregorianCalendar();
        //get EntryViolation Values.
        List<EntryViolation> val = getEntryViolations();
        //reset values.
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        for (EntryViolation entryViolation : val) {
            EntryViolationByLocationVo entryViolationByLocationVo = new EntryViolationByLocationVo();
            if (!totalLocationsList.contains(entryViolation.getLocation())) {
                try {
                    List<EntryViolation> slice = entryViolationRepo.getEntryViolationsByLoc(date.getTime(), entryViolation.getLocation());
                    //set values to Vo.
                    entryViolationByLocationVo.setCount(slice.size());
                    entryViolationByLocationVo.setLocation(entryViolation.getLocation());
                    totalEntryViolationValues.add(entryViolationByLocationVo);
                    //add vo to the ArrayList.
                    totalLocationsList.add(entryViolation.getLocation());
                } catch (Exception e) {
                    log.info("No such a element" + entryViolation.getLocation());
                }
            }
        }
        return totalEntryViolationValues;
    }
    //Total count of Entry Violation.
    public long getEntryViolationsCount() {
        List<EntryViolation> val = getEntryViolations();

        return val.size();
    }

    public static String bs64Conversion(File file) throws IOException {
        String encodedFile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedFile = new String(Base64.getEncoder().encode(bytes), "UTF-8");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File Not Found " + e.getMessage());
        } catch (IOException e) {
            throw new IOException("Exception " + e.getMessage());
        }
        return encodedFile;
    }

    public List<String> attendanceSnapshot(String snapshot, String type) throws IOException {
            String encodedString = null;
            File file = null;
            List<String> val = new ArrayList<>();
            try {
                if(type.equalsIgnoreCase("Registry")) {
                    file = new File(dataLocation + "/" + snapshot + ".jpg");
                }else if(type.equalsIgnoreCase("Unknown")){
                    file = new File(unknownLocation + "/" + snapshot + ".jpg");
                }else if(type.equalsIgnoreCase("trainingImage")) {
                    File fileDir = new File(trainingImagePath + "/" + snapshot);
                    String[] fileNames = fileDir.list();
                    if (fileNames != null && fileNames.length > 0) {
                        file = new File(trainingImagePath + "/" + snapshot + "/" + fileNames[0]);
                    }else{
                        file = new File(dataLocation + "/noImageAvailable.jpg");
                    }
                }
                encodedString = bs64Conversion(file);
            }catch (FileNotFoundException e){
                throw new FileNotFoundException("File Not Found Exception " + e.getMessage());
            }
            val.add(encodedString);
            return val;
    }

    public long getLateEntry() throws Exception {
        long count = 0;

        // Configurations.
        Configurations configuration = configurationServices.getList();
        String[] graceTimeList = configuration.getGraceTime().split(" ")[0].split(":");

        GregorianCalendar date = new GregorianCalendar();
        EntryExitFilter entryExitFilter=new EntryExitFilter();

        // current dt.
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 1);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        entryExitFilter.setDate(date.getTime());

        // on time.
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(graceTimeList[0]));
        date.set(Calendar.MINUTE, Integer.parseInt(graceTimeList[1]));
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date compareDate = date.getTime();
        List<EntryExit> val = (List<EntryExit>) this.getAttendance(entryExitFilter, 0);

        for(EntryExit entryExit: val){
            if(entryExit.getEntry_view().getTime().after(compareDate)){
                count += 1;
            }
        }
        return count;
    }

    public List<EntryExit> attendanceFilter(EntryExitFilter entryExitFilter) throws Exception{
        List<EntryExit> entryExits = new ArrayList<>();
        List<SearchCriteria> specificationValues = new ArrayList<>();
        if (entryExitFilter.getDate() != null && !StringUtils.isEmpty(entryExitFilter.getDate().toString())) {
            SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
            String selectedDate = df.format(this.getDayStTime(entryExitFilter.getDate()));
            String endDate = df.format(this.getDayEndTime(entryExitFilter.getDate()));

            //start time
            specificationValues.add(new SearchCriteria("time", ">=", selectedDate));

            //end time.
            specificationValues.add(new SearchCriteria("time", "<=", endDate));
        }
        if (!StringUtils.isBlank(entryExitFilter.getId())) {
            //id
            specificationValues.add(new SearchCriteria("id", "=", entryExitFilter.getId()));
        }
        if (entryExitFilter.getName() != null && !StringUtils.isEmpty(entryExitFilter.getName())) {
            //name
            specificationValues.add(new SearchCriteria("name", "=", entryExitFilter.getName()));
        }
        //generate query.
        StringBuilder stringBuilder = new StringBuilder();
        for (int k = 0; k < specificationValues.size(); k++) {
            if (k == 0) {
                stringBuilder.append("SELECT * FROM ilens.EntryExit WHERE ");
            }
            stringBuilder.append(specificationValues.get(k).getKey() + " " + specificationValues.get(k).getOperation() + " " + "'" + specificationValues.get(k).getValue() + "'");
            if (!(k == specificationValues.size() - 1)) {
                stringBuilder.append(" " + "AND" + " ");
            }
            if (k == specificationValues.size() - 1) {
                stringBuilder.append(" " + "AND type='entry' group by id ALLOW FILTERING");
            }
        }
        try {
            log.info("Generated Query : " + stringBuilder);
            List<EntryExitEntity> entities = cassandraTemplate.select(stringBuilder.toString(), EntryExitEntity.class);
            EntryExit entryExit = new EntryExit();
            for (EntryExitEntity entity : entities) {
                Channel ch = this.getChannelDetailsById(Long.parseLong(entity.getLocation()));
                entity.setLocation(ch.getName());
                List<ExitView> exitDetails = this.exitData(entity.getId(), entryExitFilter.getDate());
                entryExit.setId(entity.getId());
                entryExit.setName(entity.getName());
                /* String name = "----";
                try {
                    User userObj = userRepo.findById(Long.parseLong(entity.getId())).get();
                    name = String.join(" ", userObj.getFirstName(), userObj.getLastName());
                    entryExit.setName(name);
                } catch (NoSuchElementException noSuchElementException) {
                    entryExit.setName(name);
                }*/
                entryExit.setEntry_view(entity);
                entryExit.setExit_view(exitDetails);
                entryExits.add(entryExit);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return entryExits;

    }

    public void unknownSaveDataset(UnknownInputVO unknownFilterVO) throws Exception {
        Date now = null;
        UnknownEntry unknownEntry = new UnknownEntry();
        if (unknownFilterVO != null) {
            try {
                now = dt.parse(unknownFilterVO.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            unknownEntry.setName(unknownFilterVO.getChannelName());
            unknownEntry.setLocation(Long.toString(unknownFilterVO.getChannelId()));
            unknownEntry.setTime(now);
            unknownEntry.setType(unknownFilterVO.getType());
            unknownEntry.setSnapshot(unknownFilterVO.getSnapshot());
            try {
                unknownEntryRepo.save(unknownEntry);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    public List<IdTraceDetailsVO> unknownList(UnknownFilterVO unknownFilterVO, long pageNumber) throws Exception {
        List<IdTraceDetailsVO> idTraceDetailsVOs = new ArrayList<>();
        int currpage = 0;
        if (unknownFilterVO != null) {
            Date selectedDate = this.getDayStTime(unknownFilterVO.getDate());;
            Date endDate = this.getDayEndTime(unknownFilterVO.getDate());
            Slice<UnknownEntry> unknownEntries = unknownEntryRepo.getUnknownList(selectedDate, endDate, CassandraPageRequest.first(10));
            while(unknownEntries.hasNext() && currpage < pageNumber) {
                unknownEntries = unknownEntryRepo.getUnknownList(selectedDate, endDate, unknownEntries.nextPageable());
                currpage++;
            }

            for(int i=0; i<unknownEntries.getContent().size(); i++){
                    IdTraceDetailsVO idTraceDetailsVO = new IdTraceDetailsVO();
                    Channel ch = this.getChannelDetailsById(Long.parseLong(unknownEntries.getContent().get(i).getLocation()));
                    idTraceDetailsVO.setChannelId(ch.getName());
                    //idTraceDetailsVO.setChannelId(unknownEntries.getContent().get(i).getLocation());
                    idTraceDetailsVO.setTime(unknownEntries.getContent().get(i).getTime());
                    idTraceDetailsVO.setType(unknownEntries.getContent().get(i).getType());
                    idTraceDetailsVO.setSnapshot(unknownEntries.getContent().get(i).getSnapshot());
                    idTraceDetailsVOs.add(idTraceDetailsVO);
                }
            }

        return idTraceDetailsVOs;
    }

    public long unknownCount(String date) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormatForDb);
        Date date1 = df.parse(date);
        Date selectedDate = this.getDayStTime(date1);;
        Date endDate = this.getDayEndTime(date1);
        List<UnknownEntry> unknownEntries = unknownEntryRepo.getUnknownCount(selectedDate, endDate);
        return unknownEntries.size();
    }

    public Date getDayStTime(Date startDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    public Date getDayEndTime(Date endDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 58);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public ReportVO totalEntries(long days, String type) throws Exception {
        List<String> day = new ArrayList<>(Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"));

        HashMap<String, Integer> map = new HashMap<>();
        List<ReportGen1VO> attendance = new ArrayList<>();
        ReportVO reportVO = new ReportVO();

        long repeatDt = days - 1;
        if(type == null){
            days-= 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -(int)days);

        List<Long> onTimeEntryListByDay = new ArrayList<>();
        List<Long> graceTimeEntryListByDay = new ArrayList<>();
        List<Long> beyondGraceEntryListByDay = new ArrayList<>();
        List<String> dateList = new ArrayList<>();

        WeekDays weekDays = weekDayServices.getList();
        List<String> storedWeekDays = new ArrayList<>(Arrays.asList(weekDays.getWeekDays().split(",")));
        Configurations configuration = configurationServices.getList();
        String[] graceTimeList = configuration.getGraceTime().split(" ")[0].split(":");
        String[] onTimeList = configuration.getOnTime().split(" ")[0].split(":");
        for(int i=-1; i<repeatDt; i++) {
            if (storedWeekDays.contains(day.get(cal.get(Calendar.DAY_OF_WEEK)-1))) {
                int onTimeCount = 0;
                int graceTimeCount = 0;
                int lateTimeCount = 0;

                ReportGenVO attList;
                List<ReportGenVO> lstEmployees = new ArrayList<>();
                ReportGen1VO attendanceList = new ReportGen1VO();

                // day timings.
                Date dayStTime = this.getDayStTime(cal.getTime());
                Date dayEdTime = this.getDayEndTime(cal.getTime());

                // set on time
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(onTimeList[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(onTimeList[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date onTime = cal.getTime();

                // set grace time.
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(graceTimeList[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(graceTimeList[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date graceTime = cal.getTime();

                // attendance data
                EntryExitFilter entryExitFilter = new EntryExitFilter();
                entryExitFilter.setId("");
                entryExitFilter.setLocation("");
                entryExitFilter.setName("");
                entryExitFilter.setDate(dayStTime);

                List<EntryExit> entryExit = (List<EntryExit>) this.getAttendance(entryExitFilter, 0);
                if (entryExit.size() > 0) {
                    for (int l = 0; l < entryExit.size(); l++) {
                        attList = new ReportGenVO();
                        attList.setName(entryExit.get(l).getEntry_view().getName());
                        Date curTime = entryExit.get(l).getEntry_view().getTime();
                        if (curTime.after(onTime) && curTime.before(graceTime)) {
                            graceTimeCount += 1;
                        } else if (curTime.after(onTime) && curTime.after(graceTime)) {
                            lateTimeCount += 1;
                        } else if (curTime.after(dayStTime) && curTime.before(onTime)) {
                            onTimeCount += 1;
                        }
                        attList.setEntryTime(timeFormatOnly.format(entryExit.get(l).getEntry_view().getTime()));
                        attList.setEntryLocation(entryExit.get(l).getEntry_view().getLocation());
                        String exitTime = "----";
                        String exitLocation = "----";
                        if (entryExit.get(l).getExit_view().size() > 0) {
                            exitTime = timeFormatOnly.format(entryExit.get(l).getExit_view().get(0).getTime());
                            exitLocation = entryExit.get(l).getExit_view().get(0).getLocation();
                        }
                        attList.setExitTime(exitTime);
                        attList.setExitLocation(exitLocation);
                        lstEmployees.add(attList);
                    }
                    attendanceList.setDate(dateWithDayFormat.format(cal.getTime()));
                    attendanceList.setEmployees(lstEmployees);
                    attendance.add(attendanceList);
                } else {
                    attendanceList.setDate(dateWithDayFormat.format(cal.getTime()));
                    attendanceList.setEmployees(lstEmployees);
                    attendance.add(attendanceList);
                }
                onTimeEntryListByDay.add((long) onTimeCount);
                graceTimeEntryListByDay.add((long) graceTimeCount);
                beyondGraceEntryListByDay.add((long) lateTimeCount);

                dateList.add(dateWithDayFormat.format(cal.getTime()));
                // users list.
                List<UserVo> userVos = userRepo.findByAll();
                for (UserVo userVo : userVos) {
                    if (!Objects.equals(userVo.getUserId(), "Admin")) {
                        // input for getAttendance method.
                        entryExitFilter.setId(userVo.getUserId());
                        entryExitFilter.setLocation("");
                        entryExitFilter.setName("");
                        entryExitFilter.setDate(dayStTime);
                        String swapType = "";
                        Date prevTime = null;
                        long totCount = 0;
                        IdTraceVO traceList = (IdTraceVO) this.getAttendance(entryExitFilter, 0);
                        for (int k = 0; k < traceList.getTrace().size(); k++) {
                            if (k == 0) {
                                prevTime = traceList.getTrace().get(k).getTime();
                                swapType = traceList.getTrace().get(k).getType();
                            } else if (!Objects.equals(swapType, traceList.getTrace().get(k).getType())) {
                                if (Objects.equals(traceList.getTrace().get(k).getType(), "entry")) {
                                    prevTime = traceList.getTrace().get(k).getTime();
                                } else if (Objects.equals(traceList.getTrace().get(k).getType(), "exit")) {
                                    Date curDt = traceList.getTrace().get(k).getTime();
                                    long diff = Math.abs(curDt.getTime() - prevTime.getTime());
                                    long inMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                                    totCount += inMinutes;
                                    prevTime = null;
                                }

                                swapType = traceList.getTrace().get(k).getType();
                            }
                        }
                        if (map.containsKey(userVo.getFirstName() + " " + userVo.getLastName() + "[" + userVo.getUserId() + "]")) {
                            Integer newVal = map.get(userVo.getFirstName() + " " + userVo.getLastName() + "[" + userVo.getUserId() + "]") + (int) totCount;
                            map.put(userVo.getFirstName() + " " + userVo.getLastName() + "[" + userVo.getUserId() + "]", newVal);
                        } else {
                            map.put(userVo.getFirstName() + " " + userVo.getLastName() + "[" + userVo.getUserId() + "]", (int) totCount);
                        }
                    }
                }
        }
            cal.add(Calendar.DATE, 1);
        }

        JSONObject reportGraphData = new JSONObject();
        reportGraphData.put("onTimeList", onTimeEntryListByDay);
        reportGraphData.put("graceTimeList", graceTimeEntryListByDay);
        reportGraphData.put("lateTimeList",  beyondGraceEntryListByDay);
        reportGraphData.put("dateList", dateList);
        reportGraphData.put("performance", map);

        try {
            FileWriter userList = new FileWriter(configsJsonPath + "/userList.json");
            userList.write(String.valueOf(reportGraphData));
            userList.close();
        }catch (Exception exception){
            throw new Exception("Exception" + exception.getMessage());
        }

        reportVO.setTotalOnTime(String.valueOf(onTimeEntryListByDay.stream().mapToInt(Long::intValue).sum()));
        reportVO.setTotalGraceTime(String.valueOf(graceTimeEntryListByDay.stream().mapToInt(Long::intValue).sum()));
        reportVO.setTotalBeyondGraceTime(String.valueOf(beyondGraceEntryListByDay.stream().mapToInt(Long::intValue).sum()));
        reportVO.setOnTime(configuration.getOnTime());
        reportVO.setGraceTime(configuration.getGraceTime());
        reportVO.setExitOnTime(configuration.getExitOnTime());
        reportVO.setExitGraceTime(configuration.getExitGraceTime());
        reportVO.setWeekDaysCount(onTimeEntryListByDay.size());
        reportVO.setAttendance(attendance);
        return reportVO;
    }

}