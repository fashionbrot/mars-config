package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.util.CookieUtil;
import com.github.fashionbrot.common.util.JwtTokenUtil;
import com.github.fashionbrot.common.util.PasswordUtils;
import com.github.fashionbrot.dao.entity.RoleInfo;
import com.github.fashionbrot.dao.entity.UserInfo;
import com.github.fashionbrot.dao.entity.UserRoleRelation;
import com.github.fashionbrot.dao.mapper.RoleInfoMapper;
import com.github.fashionbrot.dao.mapper.UserInfoMapper;
import com.github.fashionbrot.dao.mapper.UserRoleRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
@Slf4j
public class UserInfoDao  {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RoleInfoMapper roleInfoMapper;

    @Autowired
    private UserRoleRelationMapper userRoleRelationMapper;

    @Autowired
    private HttpServletRequest request;


    @Transactional(rollbackFor = Exception.class)
    public Integer add(UserInfo userInfo) {
        Date date =new Date();
        userInfo.setCreateDate(date);
        String salt = PasswordUtils.getSalt();
        String password = PasswordUtils.encryptPassword(userInfo.getPassword(),salt);
        userInfo.setSalt(salt);
        userInfo.setPassword(password);
        int count= userInfoMapper.selectCount(new QueryWrapper<UserInfo>().eq("user_name",userInfo.getUserName()));
        if (count>0){
            throw new MarsException("用户名已存在，请重新输入");
        }
        int result= userInfoMapper.insert(userInfo);
        if (userInfo.getId()!=null && userInfo.getId().intValue()!=0) {
            if (userInfo.getSuperAdmin()==0){
                RoleInfo roleInfo=roleInfoMapper.selectById(userInfo.getRoleId());
                if (roleInfo==null){
                    throw new MarsException("当前角色不存在，请刷新重试");
                }
                userRoleRelationMapper.insert(UserRoleRelation.builder()
                        .roleId(userInfo.getRoleId())
                        .userId(userInfo.getId())
                        .build());
            }
        }

        return result;
    }

    public int updateById(UserInfo userInfo){
        Date date =new Date();
        userInfo.setUpdateDate(date);
        return userInfoMapper.updateById(userInfo);
    }


    @Transactional(rollbackFor = Exception.class)
    public Integer update(UserInfo userInfo) {
        Date date =new Date();
        userInfo.setUpdateDate(date);
        if (userInfo.getSuperAdmin()==0){
            RoleInfo roleInfo=roleInfoMapper.selectById(userInfo.getRoleId());
            if (roleInfo==null){
                throw new MarsException("当前角色不存在，请刷新重试");
            }

            UserRoleRelation userRoleRelation=userRoleRelationMapper.selectOne(new QueryWrapper<UserRoleRelation>().eq("user_id",userInfo.getId()));
            if (userRoleRelation==null){
                userRoleRelationMapper.insert(UserRoleRelation.builder()
                        .roleId(userInfo.getRoleId())
                        .userId(userInfo.getId())
                        .build());
            }else{
                userRoleRelation.setRoleId(userInfo.getRoleId());
                userRoleRelationMapper.updateById(userRoleRelation);
            }
        }


        UserInfo user= userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("user_name",userInfo.getUserName()));
        if (user!=null && !userInfo.getUserName().equals(user.getUserName())){
            throw new MarsException("用户名已存在，请重新输入");
        }

        int result= userInfoMapper.updateById(userInfo);


        return result;
    }


    public Integer deleteById(Long id) {
        return userInfoMapper.deleteById(id);
    }


    public int updateLastLoginTime(Long id, Timestamp timestamp) {
        return userInfoMapper.updateLastLoginTime(id,timestamp);
    }

    public UserInfo selectOne(QueryWrapper queryWrapper) {
        return userInfoMapper.selectOne(queryWrapper);
    }


    public List<UserInfo> selectList(QueryWrapper wrapper) {
        return userInfoMapper.selectList(wrapper);
    }


    public List<UserInfo> queryAll() {
        return userInfoMapper.queryAll();
    }


    public UserInfo queryById(Long id) {
        UserInfo userInfo= userInfoMapper.selectById(id);
        if (userInfo!=null){
            UserRoleRelation userRoleRelation=userRoleRelationMapper.selectOne(new QueryWrapper<UserRoleRelation>().eq("user_id",userInfo.getId()));
            if (userRoleRelation!=null){
                userInfo.setRoleId(userRoleRelation.getRoleId());
            }
        }
        return userInfo;
    }

    public LoginModel getLogin() {
        String  authValue  = CookieUtil.getCookieValue(request, MarsConst.AUTH_KEY,false);
        if (!StringUtils.isEmpty(authValue)){
            LoginModel model  = JwtTokenUtil.getLogin(authValue);
            if (model==null){
                throw new MarsException(RespCode.SIGNATURE_MISMATCH);
            }
            return model;
        }
        return null;
    }

    public UserInfo getUser() {
        String  authValue  = CookieUtil.getCookieValue(request, MarsConst.AUTH_KEY,false);
        if (!StringUtils.isEmpty(authValue)){
            LoginModel model  = JwtTokenUtil.getLogin(authValue);
            if (model==null){
                throw new MarsException(RespCode.SIGNATURE_MISMATCH);
            }
            UserInfo userInfo = queryById(model.getUserId());
            if (userInfo==null){
                throw new MarsException(RespCode.USER_NOT_EXIST);
            }
            return userInfo;
        }
        return null;
    }

}
