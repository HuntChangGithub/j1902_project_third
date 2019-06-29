package com.qf.j1902.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.qf.j1902.pojo.User;
import com.qf.j1902.service.UserService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2019/6/28.
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;
    //通过阿里云第三方服务发送手机注册验证码
    @RequestMapping(value = "sendphone")
    public void phoneYzm(@RequestParam("telphone") String telphone , HttpSession session) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI50OiPXnu9r7M", "xxvzB7ukfABXay5CQqdGWzd5jbKwbu");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        int yzm = (int)((Math.random()*9+1)*100000);
        session.setAttribute("yzm",yzm);
        Map<String , Integer> map=new HashMap<>();
        map.put("code",yzm);
        String json = new Gson().toJson(map);
        System.out.println(json);
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", telphone);
        request.putQueryParameter("SignName", "伴我汽车");
        request.putQueryParameter("TemplateCode", "SMS_169101820");
        request.putQueryParameter("TemplateParam", json);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
    //根据username、upassword、telphone进行用户注册
    @RequestMapping(value = "adduser" , method = RequestMethod.POST)
    public String addUser(@RequestParam("username") String username ,
                          @RequestParam("upassword") String upassword ,
                          @RequestParam("telphone") String telphone ,
                          @RequestParam("telphoneyzm") int telphoneyzm ,
                          HttpSession session , Model model){
        //获取存在session中的短信验证码
        int yzm = (Integer)session.getAttribute("yzm");
        if (yzm == telphoneyzm){
            //添加当前时间作为用户注册时间
            Date date = new Date();
            SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd");
            String regtime = time.format(date);
            Md5Hash md5Hash = new Md5Hash(upassword,null,1024);
            String md5str = md5Hash.toString();
            User user = new User();
            user.setUsername(username);
            user.setUpassword(upassword);
            user.setTelphone(telphone);
            user.setRegtime(regtime);
            //查用户表中有没有这个username
            User user1 = userService.getUserByName(username);
            if (user1 == null){
                //没有则进行添加程序
                userService.addUser(user);
                User user2 = userService.getUserByName(username);

                String string = "注册成功,请登录!";
                model.addAttribute("string",string);
                return "login";
            }else {
                String string = "该用户名已存在,请重新输入!";
                model.addAttribute("string",string);
                return "reg";
            }
        }else {
            String string = "验证码输入错误!";
            model.addAttribute("string",string);
            return "login";
        }
    }
}
