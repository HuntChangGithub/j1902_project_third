package com.qf.j1902.shiro;

import com.qf.j1902.pojo.User;
import com.qf.j1902.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by 赵国林 on 2019/7/2.
 */
public class MyRealm extends AuthorizingRealm{
    @Autowired
    private UserService userService;
    @Override//权限
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override//登录
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String principal = (String) authenticationToken.getPrincipal();
        User user = userService.getUserByName(principal);
        SimpleAuthenticationInfo authenticationInfo=null;
        if (user!=null){
            authenticationInfo=new SimpleAuthenticationInfo(principal,user.getUpassword(),this.getName());
        }
        return authenticationInfo;
    }
}
