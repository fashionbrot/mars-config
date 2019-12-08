package com.gitee.mars.dao.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.dao.entity.Menu;
import com.gitee.mars.dao.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class MenuDao  {
    @Autowired
    private MenuMapper menuMapper;


    public int add(Menu envInfo) {
        envInfo.setCreateDate(new Date());
        return menuMapper.insert(envInfo);
    }


    public Integer update(Menu envInfo) {
        envInfo.setUpdateDate(new Date());
        return menuMapper.updateById(envInfo);
    }


    public Integer deleteById(Long id) {
        return menuMapper.deleteById(id);
    }


    public Menu queryById(Long id) {
        return menuMapper.selectById(id);
    }


    public List<Menu> queryAll(QueryWrapper queryWrapper) {
        return menuMapper.selectList(queryWrapper);
    }


    public List<Menu> selectMenuRole(Long roleId) {
        return menuMapper.selectMenuRole(roleId);
    }


    public List<Menu> selectMenuRoleByUser(Long userId) {
        return menuMapper.selectMenuRoleByUser(userId);
    }
}
