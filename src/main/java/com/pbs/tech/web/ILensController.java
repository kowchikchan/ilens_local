package com.pbs.tech.web;

import com.pbs.tech.common.exception.AuthenticationException;
import com.pbs.tech.model.DataApi;
import com.pbs.tech.repo.DataApiRepo;
import com.pbs.tech.services.UserService;
import com.pbs.tech.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/")
public class ILensController {

    private static final Logger LOG= LoggerFactory.getLogger(ILensController.class);

    @Autowired
    UserService userService;

    @Autowired
    DataApiRepo dataApiRepo;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/auth")
    @ResponseBody
    public String authenticate(HttpServletRequest request, HttpServletResponse response, @RequestBody UserVo userVo) throws AuthenticationException {
        try {
            UserVo user = userService.authUser(userVo);
            if (user != null) {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("USER"));
                Authentication myToken = new UsernamePasswordAuthenticationToken(userVo.getUserId(), userVo.getUserSecret(), authorities);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(myToken);
                userVo.setUserSecret("");

                userVo.setSessionToken("iLens" + "~" + myToken);

                HttpSession session = request.getSession(true);
                session.setAttribute("USER", userVo);
                session.setAttribute("ROLE", userVo.getRole());
                session.setAttribute("USER_ID", userVo.getUserId());
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
                DataApi dataApi;
                try {
                    dataApi = dataApiRepo.findById(0L).get();
                }catch (NoSuchElementException e){
                    throw new NoSuchElementException("Exception {}" + e.getMessage());
                }
                session.setAttribute("DATA_API", dataApi.getDataApi());
                session.setAttribute("API_TOKEN", dataApi.getApiToken());
                session.setAttribute("REPORT_API", dataApi.getReportApi());
                String systemIp = this.getIpAddress();
                session.setAttribute("IP", systemIp);
                return "/";
            }

        }catch (AuthenticationException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new AuthenticationException("Invalid Credentials");
        }
        return "fail";
    }

    @GetMapping("/channels")
    public String channels() {
        return "channels";
    }

    @GetMapping("/setup/channellist")
    public String channellist() {
        return "/setup/channellist";
    }

    @GetMapping("/data_training")
    public String data_training() {
        return "data_training";
    }

    @GetMapping("/configuration")
    public String configuration() {
        return "configuration";
    }


    @GetMapping("/members")
    public String members() {
        return "members";
    }


    @GetMapping("/new_user")
    public String new_user() {
        return "new_user";
    }

    @GetMapping("/attendance")
    public String attendance() {
        return "attendance";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/attendanceById")
    public String trace(){return "trace";}

    @GetMapping("/upload")
    public String upload(){return "upload";}

    @GetMapping("/data_api")
    public String data_api(){return "data_api";}

    @GetMapping("/licence")
    public String licence(){return "licence";}

     @GetMapping("/new_device")
    public String new_device(){return "new_device";}
    
    @GetMapping("/import_licence")
    public String import_licence(){return "import_licence";}

    @GetMapping("/grid")
    public String grid(){return "grid";}

    @GetMapping(path = "/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        LOG.info("Session Invalidated");
        return "login";
    }

    public String getIpAddress(){
        String ip="";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet6Address) continue;
                    ip = address.getHostAddress();
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }

}
