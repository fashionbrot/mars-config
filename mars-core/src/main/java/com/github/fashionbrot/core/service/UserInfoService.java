package com.github.fashionbrot.core.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.util.CookieUtil;
import com.github.fashionbrot.common.util.JwtTokenUtil;
import com.github.fashionbrot.common.util.PasswordUtils;
import com.github.fashionbrot.common.vo.PageDataVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.RoleInfoDao;
import com.github.fashionbrot.dao.dao.UserInfoDao;
import com.github.fashionbrot.dao.entity.RoleInfo;
import com.github.fashionbrot.dao.entity.UserInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Component
@Slf4j
public class UserInfoService {


    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RoleInfoDao roleInfoDao;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserLoginService userLoginService;


    public RespVo login(String userName, String password, HttpServletRequest request, HttpServletResponse response) {

        UserInfo userInfo = userInfoDao.selectOne(new QueryWrapper<UserInfo>().eq("user_name", userName));
        if (userInfo == null) {
            throw new MarsException(RespCode.USER_OR_PASSWORD_IS_ERROR);
        }
        if (userInfo.getStatus() == 0) {
            throw new MarsException("用户已关闭");
        }
        String salt = userInfo.getSalt();
        String encryptPassword = PasswordUtils.encryptPassword(password, salt);
        if (!userInfo.getPassword().equals(encryptPassword)) {
            throw new MarsException(RespCode.USER_OR_PASSWORD_IS_ERROR);
        }
        userInfoDao.updateLastLoginTime(userInfo.getId(), new Timestamp(System.currentTimeMillis()));

        RoleInfo roleInfo = roleInfoDao.findByUserId(userInfo.getId());
        String roleName ="";
        if (roleInfo!=null){
            roleName =roleInfo.getRoleName();
        }
        String token = JwtTokenUtil.createToken(userInfo.getId(),userInfo.getRealName(),roleName,userInfo.getSuperAdmin()==1);

        CookieUtil.setCookie(request,response,userInfo.getRealName(),roleName,token,true);

        return RespVo.success();
    }


    public void add(UserInfo userInfo) {
        LoginModel login = userLoginService.getLogin();
        if (!login.isSuperAdmin()){
            userInfo.setSuperAdmin(0);
        }
        if (userInfoDao.add(userInfo) != 1) {
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    public void update(UserInfo userInfo) {
        LoginModel login = userLoginService.getLogin();
        if (!login.isSuperAdmin()){
            userInfo.setSuperAdmin(0);
        }
        UserInfo oldUser = queryById(userInfo.getId());
        if (oldUser != null && !oldUser.getPassword().equals(userInfo.getPassword())) {
            String salt = PasswordUtils.getSalt();
            String password = PasswordUtils.encryptPassword(userInfo.getPassword(), salt);
            userInfo.setSalt(salt);
            userInfo.setPassword(password);
        }
        int result = userInfoDao.update(userInfo);
        if (result == 1) {
            menuService.clearMenuList();
        }else{
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        UserInfo userInfo=userInfoDao.queryById(id);
        if (userInfo!=null && userInfo.getSuperAdmin()==1){
            LoginModel login = userLoginService.getLogin();
            if (!login.isSuperAdmin()){
                throw new MarsException("不是管理员，无法操作");
            }
        }
        if (userInfoDao.deleteById(id) !=1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public UserInfo queryById(Long id) {
        return userInfoDao.queryById(id);
    }


    public  PageDataVo<UserInfo> queryAll(Integer start,Integer length) {
        Page page = PageHelper.startPage((start/length)+1, length);
        List<UserInfo>  userInfoList = userInfoDao.queryAll();
        PageDataVo<UserInfo> pageData = new PageDataVo<>();
        pageData.setData(userInfoList);
        pageData.setITotal(page.getTotal());
        pageData.setITotalDisplayRecords(page.getTotal());
        pageData.setFnRecordsTotal(page.getTotal());
        return pageData;
    }


    public List<RoleInfo> queryRoleAll() {
        return roleInfoDao.queryAll(null);
    }


    @Transactional(rollbackFor = Exception.class)
    public RespVo resetPwd(String pwd, String newPwd) {
        if (pwd.equals(newPwd)) {
            throw new MarsException("新密码和原密码一致，请修改");
        }
        LoginModel login = userLoginService.getLogin();
        UserInfo user = userInfoDao.queryById(login.getUserId());
        if (user != null) {
            String salt =user.getSalt();
            String encryptPassword = PasswordUtils.encryptPassword(pwd, salt);
            if (!user.getPassword().equals(encryptPassword)) {
                throw new MarsException("原密码输入错误，请重新输入");
            }


            String password =PasswordUtils.encryptPassword(newPwd,salt);
            user.setPassword(password);
            int result = userInfoDao.updateById(user);
            if (result != 1) {
                throw new MarsException(RespCode.UPDATE_ERROR);
            }
        }

        return RespVo.success();
    }





}
