package com.gitee.mars.dao.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.common.constant.MarsConst;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.common.util.CookieUtil;
import com.gitee.mars.common.util.JwtTokenUtil;
import com.gitee.mars.common.util.PasswordUtils;
import com.gitee.mars.dao.entity.RoleInfo;
import com.gitee.mars.dao.entity.UserInfo;
import com.gitee.mars.dao.entity.UserRoleRelation;
import com.gitee.mars.dao.mapper.RoleInfoMapper;
import com.gitee.mars.dao.mapper.UserInfoMapper;
import com.gitee.mars.dao.mapper.UserRoleRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


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
            RoleInfo roleInfo=roleInfoMapper.selectById(userInfo.getRoleId());
            if (roleInfo==null){
                throw new MarsException("当前角色不存在，请刷新重试");
            }
            userRoleRelationMapper.insert(UserRoleRelation.builder()
                    .createDate(date)
                    .roleId(userInfo.getRoleId())
                    .userId(userInfo.getId())
                    .build());
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

        RoleInfo roleInfo=roleInfoMapper.selectById(userInfo.getRoleId());
        if (roleInfo==null){
            throw new MarsException("当前角色不存在，请刷新重试");
        }

        UserRoleRelation userRoleRelation=userRoleRelationMapper.selectOne(new QueryWrapper<UserRoleRelation>().eq("user_id",userInfo.getId()));
        if (userRoleRelation==null){
            userRoleRelationMapper.insert(UserRoleRelation.builder()
                    .createDate(date)
                    .roleId(userInfo.getRoleId())
                    .userId(userInfo.getId())
                    .build());
        }else{
            userRoleRelation.setRoleId(userInfo.getRoleId());
            userRoleRelationMapper.updateById(userRoleRelation);
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

    public Long getUserId() {
        String  authValue  = CookieUtil.getCookieValue(request, MarsConst.AUTH_KEY,false);
        if (!StringUtils.isEmpty(authValue)){
            Long userId  = JwtTokenUtil.verifyTokenAndGetUser(authValue);
            if (userId==null){
                throw new MarsException(RespCode.SIGNATURE_MISMATCH);
            }
            return userId;
        }
        return null;
    }

    public UserInfo getUser() {
        String  authValue  = CookieUtil.getCookieValue(request, MarsConst.AUTH_KEY,false);
        if (!StringUtils.isEmpty(authValue)){
            Long userId  = JwtTokenUtil.verifyTokenAndGetUser(authValue);
            if (userId==null){
                throw new MarsException(RespCode.SIGNATURE_MISMATCH);
            }
            UserInfo userInfo = queryById(userId);
            if (userInfo==null){
                throw new MarsException(RespCode.USER_NOT_EXIST);
            }
            return userInfo;
        }
        return null;
    }

}
