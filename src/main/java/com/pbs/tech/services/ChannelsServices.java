package com.pbs.tech.services;
/**
 *
 */

import com.pbs.tech.model.*;
import com.pbs.tech.repo.*;
import com.pbs.tech.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.*;

import static com.pbs.tech.config.LicenceValidate.isValidLicence;

@Service
public class ChannelsServices {

    @Autowired
    ChannelRepo channelRepo;

    @Autowired
    FRConfigRepo frConfigRepo;

    @Autowired
    NprConfigRepo nprConfigRepo;

    @Autowired
    AccessConfigRepo accessConfigRepo;

    @Autowired
    LicenceRepo licenceRepo;

    @Autowired
    ChannelResizeRepo channelResizeRepo;

    Logger log= LoggerFactory.getLogger(ChannelsServices.class);


  /*  public static void main(String args[]){
        log.info("Mine"+getIP());
        String s = "192.168.0.1";
        String[] data = s.split("\\.");
        System.out.println("Name = "+data[0]); //Pankaj
        System.out.println("Address = "+data[1]); //New York,USA
       // List<String> ips= new ChannelsServices().getConnected("192.168.0.x");

       // System.out.println(ips);
    }*/

    public List<ChannelVo> getConnected(String filter) {
        List<ChannelVo> channelVos=new ArrayList<>();
        try {
            InetAddress ip;
            ip = InetAddress.getLocalHost();
            String subnet = filter.substring(0, filter.length()-2);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            log.info("IP:"+ ip);
            if (filter == null) {
                subnet = "192.168.0";
            }
/*            if(!filter.equalsIgnoreCase("192.168.0.x")){
            }*/
            log.info("subnet : " + subnet);
                int timeout=50;
                for (int i=1;i<255;i++){
                    String host=subnet + "." + i;
                 //   if (InetAddress.getByName(host).isReachable(timeout)){

                        if(crunchifyAddressReachable(host,554,timeout)) {
                            System.out.println(host);
                           /* ChannelVo vo=new ChannelVo();
                            vo.setIp(host);*/
                           List obj= channelRepo.findByIp(host);
                           if(obj.size()==0) {
                               Channel channel = new Channel();
                               channel.setIp(host);
                               channel.setCreatedDt(new Date());
                               channel.setUpdatedDt(new Date());
                               channel.setUpdatedBy("admin");
                               channel.setCreatedBy("admin");
                               channelRepo.save(channel);
                               List<Channel> c  = channelRepo.findByIp(host);
                               if(c.size() != 0) {
                                   ChannelResize cr = new ChannelResize(0, c.get(0).getId(), 0, 0, 0, 0, 0, 0);
                                   channelResizeRepo.save(cr);
                               }
                           }
                           // channelVos.add(vo);
                            //ips.add(host);
                        }
                   // }
                }
        }  catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChannelFilterVO channelFilterVO = new ChannelFilterVO();
        channelFilterVO.setStatus("All");
        return getChannelsList(channelFilterVO, 0);

    }

    private static String getIP(){
        String ip=null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // *EDIT*
                    if (addr instanceof Inet6Address) continue;

                    ip = addr.getHostAddress();
                    System.out.println(iface.getDisplayName() + " " + ip);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }

    private static boolean crunchifyAddressReachable(String address, int port, int timeout) {
        try {

            try (Socket crunchifySocket = new Socket()) {
                // Connects this socket to the server with a specified timeout value.
                try {
                    crunchifySocket.connect(new InetSocketAddress(address, port), timeout);
                }catch (Exception e){
                    return false;
                }
            }
            // Return true if connection successful
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            // Return false if connection fails
            return false;
        }
    }

    public void saveChannel(ChannelVo vo) throws Exception {
        ArrayList<Long> runningChannels = new ArrayList<>();
        ChannelFilterVO ch = new ChannelFilterVO();
        ch.setStatus("true");
        List<ChannelVo> chList = this.getChannelsList(ch, 0);
        for(ChannelVo ch1: chList){
            runningChannels.add(ch1.getId());
        }
        Licence licence;
        try {
            licence = licenceRepo.findById(1L).get();
        }catch (Exception e){
           throw new Exception("No Licence Found! " + e.getMessage());
        }

        LicenceVo licenceVo = isValidLicence(licence.getLicenceStr());
        if(vo.isStatus() && runningChannels.size() >= Long.parseLong(licenceVo.getServerCount()) && !runningChannels.contains(vo.getId())){
            throw new Exception("Server count exceed");
        }

        //now save channelConfig
        Channel channel=new Channel();
        channel.setId(vo.getId());
        channel.setName(vo.getName());
        channel.setIp(vo.getIp());
        channel.setCamType(vo.getCamType());
        channel.setCreatedBy("admin");
        channel.setCreatedDt(new Date());
        channel.setUpdatedBy("admin");
        channel.setUpdatedDt(new Date());
        channel.setEntry(vo.isEntry());
        channel.setExit(vo.isExit());
        channel.setFrEnabled(vo.isFrEnabled());
        channel.setNprEnabled(vo.isNprEnabled());
        channel.setAccessEnabled(vo.isAccessEnabled());
        channel.setCountsEnabled(vo.isCountsEnabled());
        channel.setStatus(vo.isStatus());
        channelRepo.save(channel);

        if(vo.getId() == 0) {
            List<Channel> c  = channelRepo.findByIp(vo.getIp());
            if(c.size() != 0) {
                ChannelResize cr = new ChannelResize(0, c.get(0).getId(), 0, 0, 0, 0, 0, 0);
                channelResizeRepo.save(cr);
            }
        }

        //now save frConfig
        if(vo.getId()!=0){
                FRConfig frConfigObj = new FRConfig();
                frConfigObj.setCreatedBy("admin");
                frConfigObj.setCreatedDt(new Date());
                frConfigObj.setUpdatedBy("admin");
                frConfigObj.setUpdatedDt(new Date());
                frConfigObj.setId(vo.getId());
                frConfigObj.setChannelId(vo.getId());
                frConfigObj.setMinRatio(vo.getFrConfigsVo().getMinRatio());
                frConfigObj.setMaxRatio(vo.getFrConfigsVo().getMaxRatio());
                frConfigObj.setMaximumPeople(vo.getFrConfigsVo().getMaximumPeople());
                frConfigObj.setMinimumPeople(vo.getFrConfigsVo().getMinimumPeople());
                frConfigRepo.save(frConfigObj);
                //now save Npr config
                NprConfig nprConfigObj = new NprConfig();
                nprConfigObj.setCreatedBy("admin");
                nprConfigObj.setCreatedDt(new Date());
                nprConfigObj.setUpdatedBy("admin");
                nprConfigObj.setUpdatedDt(new Date());
                nprConfigObj.setId(vo.getId());
                nprConfigObj.setChannelId(vo.getId());
                nprConfigObj.setMinRatio(vo.getNprConfigsVo().getMinRatio());
                nprConfigObj.setMaxRatio(vo.getNprConfigsVo().getMaxRatio());
                nprConfigObj.setMinKernel(vo.getNprConfigsVo().getMinKernel());
                nprConfigObj.setMaxKernel(vo.getNprConfigsVo().getMaxKernel());
                nprConfigRepo.save(nprConfigObj);
                //now save Access config

                for(String id:vo.getConfigsVo().getMembersAdd()){
                    AccessConfigs accessConfigsObj = new AccessConfigs();
                    accessConfigsObj.setCreatedBy("admin");
                    accessConfigsObj.setCreatedDt(new Date());
                    accessConfigsObj.setUpdatedBy("admin");
                    accessConfigsObj.setUpdatedDt(new Date());
                    //add member
                    if(!StringUtils.isBlank(id)){
                        accessConfigsObj.setChannelId(vo.getId());
                        accessConfigsObj.setPersonId(id);
                        accessConfigRepo.save(accessConfigsObj);
                    }
                }
                    //remove member
                for(String id:vo.getConfigsVo().getMembersRemove()) {
                    if(!StringUtils.isBlank(id)) {
                        try {
                            AccessConfigs obj = accessConfigRepo.findByChannelIdAndPersonId(vo.getId(), id);
                            accessConfigRepo.delete(obj);
                        } catch (NoSuchElementException e) {
                            log.warn("No Such Person for id {}", id);
                        }
                    }

                }
                }
        log.info("Channel updated.");
    }

    public void deleteChannel(long id){
        try {
            Channel channel = channelRepo.findById(id).get();
            channelRepo.delete(channel);
        }catch (NoSuchElementException e){
           log.warn("No element exist for id {}",id);
        }
    }

    public ChannelVo getChannelById(long id){
        ChannelVo vo=new ChannelVo();
        //Channel values
        try {
            Channel channel = channelRepo.findById(id).get();
            vo.setId(channel.getId());
            vo.setName(channel.getName());
            vo.setCamType(channel.getCamType());
            vo.setIp(channel.getIp());
            vo.setRunning(isRunning(id));
            vo.setExit(channel.isExit());
            vo.setEntry(channel.isEntry());
            vo.setCreateBy(channel.getCreatedBy());
            vo.setCreatedDt(channel.getCreatedDt());
            vo.setUpdatedBy(channel.getUpdatedBy());
            vo.setUpdatedDt(channel.getUpdatedDt());
            vo.setFrEnabled(channel.isFrEnabled());
            vo.setNprEnabled(channel.isNprEnabled());
            vo.setAccessEnabled(channel.isAccessEnabled());
            vo.setCountsEnabled(channel.isCountsEnabled());
            vo.setStatus(channel.isStatus());
        }catch (Exception e){
            log.warn("No Such Element in Channel");
        }
        //FrConfig values
        try {
            FRConfig frConfig = frConfigRepo.findByChannelId(id);
            FrConfigsVo frConfigsVo = new FrConfigsVo();
            frConfigsVo.setId(frConfig.getId());
            frConfigsVo.setMinRatio(frConfig.getMinRatio());
            frConfigsVo.setMaxRatio(frConfig.getMaxRatio());
            frConfigsVo.setMaximumPeople(frConfig.getMaximumPeople());
            frConfigsVo.setMinimumPeople(frConfig.getMinimumPeople());
            vo.setFrConfigsVo(frConfigsVo);
        }catch (Exception e){
            log.warn("No Such Element in FrConfig");
        }
        //NprConfig values
        try {
            NprConfig nprConfig = nprConfigRepo.findByChannelId(id);
            NprConfigsVo nprConfigsVo = new NprConfigsVo();
            nprConfigsVo.setId(nprConfig.getId());
            nprConfigsVo.setMinRatio(nprConfig.getMinRatio());
            nprConfigsVo.setMaxRatio(nprConfig.getMaxRatio());
            nprConfigsVo.setMinKernel(nprConfig.getMinKernel());
            nprConfigsVo.setMaxKernel(nprConfig.getMaxKernel());
            vo.setNprConfigsVo(nprConfigsVo);
        }catch (Exception e){
            log.warn("No Such Element in NprConfig ");
        }
        //Access config values
        try {
            List<AccessConfigs> accessConfigs = accessConfigRepo.findByChannelId(id);
            List<String> mappedMembers = new ArrayList<>(accessConfigs.size());
            for (AccessConfigs member : accessConfigs) {
                mappedMembers.add(member.getPersonId());
            }
            AccessConfigsVo accessMappings = new AccessConfigsVo();
            accessMappings.setMembersAdd(mappedMembers);

            vo.setConfigsVo(accessMappings);
        }catch (Exception e){
            log.warn("No Such Element in AccessConfig");
        }
        return vo;
    }

    public List<ChannelVo> getChannelsList(ChannelFilterVO channelFilterVO, int pageNumber){
        List<ChannelVo> vos=new ArrayList<>();
        List<Channel> channelList;
        Pageable page = PageRequest.of(pageNumber, 10);
        try {
            if(channelFilterVO.getStatus().equalsIgnoreCase("All") && StringUtils.equalsIgnoreCase(channelFilterVO.getIp(), "null")) {
                channelList = channelRepo.findAllByOrderByCreatedDtDesc(page);
            }else if(!channelFilterVO.getStatus().equalsIgnoreCase("All") && StringUtils.equalsIgnoreCase(channelFilterVO.getIp(), "null")) {
                channelList = channelRepo.findAllByStatus(Boolean.parseBoolean(channelFilterVO.getStatus()), page);
            } else if(channelFilterVO.getStatus().equalsIgnoreCase("All") && !StringUtils.equalsIgnoreCase(channelFilterVO.getIp(), "null")){
                channelList = channelRepo.findByIp(channelFilterVO.getIp(), page);
            }else {
                channelList = channelRepo.findAllByStatusAndIp(Boolean.parseBoolean(channelFilterVO.getStatus()), channelFilterVO.getIp(), page);
            }
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Exception " + e.getMessage());
        }
        for(Channel channel:channelList){
            ChannelVo vo=new ChannelVo();
            vo.setId(channel.getId());
            vo.setRunning(isRunning(channel.getId()));
            vo.setName(channel.getName());
            vo.setCamType(channel.getCamType());
            vo.setIp(channel.getIp());
            vo.setUpdatedBy(channel.getUpdatedBy());
            vo.setUpdatedDt(channel.getUpdatedDt());
            vo.setCreatedDt(channel.getCreatedDt());
            vo.setCreateBy(channel.getCreatedBy());
            vo.setEntry(channel.isEntry());
            vo.setExit(channel.isExit());
            vo.setStatus(channel.isStatus());
            vos.add(vo);
        }
        return vos;
    }

    private boolean isRunning(long id){
        List<ChannelRunTime>  runtimes=IlenService.runtimes;
        log.info("Runtime services {}",runtimes.size());
        for(ChannelRunTime runTime:runtimes){
            if(runTime.getName().equalsIgnoreCase(String.valueOf(id))){
                return true;
            }
        }
        return false;

    }

    public long getPageCount(String filterWord, String ip){
        List<Channel> channelList;
        if(filterWord.equalsIgnoreCase("All") && StringUtils.equalsIgnoreCase(ip, "null")) {
            channelList = channelRepo.findAllByOrderByCreatedDtDesc();
        }else if(!filterWord.equalsIgnoreCase("All") && StringUtils.equalsIgnoreCase(ip, "null")) {
            channelList = channelRepo.findAllByStatus(Boolean.parseBoolean(filterWord));
        } else if(filterWord.equalsIgnoreCase("All") && !StringUtils.equalsIgnoreCase(ip, "null")){
            channelList = channelRepo.findByIp(ip);
        }else {
            channelList = channelRepo.findAllByStatusAndIp(Boolean.parseBoolean(filterWord), ip);
        }
        return  channelList.size();
    }

    public void setActive(long id){
        Channel channel=channelRepo.findById(id).get();
        channel.setStatus(true);
        channelRepo.save(channel);

    }

    public void setInActive(long id){
        Channel channel=channelRepo.findById(id).get();
        channel.setStatus(false);
        channelRepo.save(channel);
    }

    public List<Channel> channelList(){
        return (List<Channel>) channelRepo.findAll();
    }
}
