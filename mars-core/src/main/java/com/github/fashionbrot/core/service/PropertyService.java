package com.github.fashionbrot.core.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.OptionEnum;
import com.github.fashionbrot.dao.dao.TableColumnDao;
import org.apache.commons.lang3.StringUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.req.PropertyReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.PropertyDao;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.fashionbrot.dao.mapper.TableColumnMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * 属性表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */
@Service
public class PropertyService  {

    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Autowired
    private TableColumnDao tableColumnDao;


    public Collection<PropertyEntity> queryList(Map<String, Object> params) {
        PageHelper.orderBy("priority asc");
        return propertyDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<PropertyEntity>
     */
    public Collection<PropertyEntity> propertyByMap(Map<String, Object> params){
        return propertyDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(PropertyReq req){
        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        QueryWrapper<PropertyEntity> queryWrapper=new QueryWrapper();
        if (StringUtils.isNotEmpty(req.getAppName())){
            queryWrapper.eq("app_name",req.getAppName());
        }
        queryWrapper.eq("template_key",req.getTemplateKey());
        if (StringUtils.isNotEmpty(req.getPropertyKey())){
            queryWrapper.like("property_key",req.getPropertyKey());
        }
        queryWrapper.orderByAsc("priority");
        List<PropertyEntity> list = propertyDao.list(queryWrapper);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    @Transactional(rollbackFor = Exception.class)
    public void insert(PropertyEntity entity) {
        if (MarsConst.propertySet.contains(entity.getPropertyKey())){
            throw new MarsException("系统保留字段，请重新输入");
        }
        if(!propertyDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }

        //tableColumnDao.operateTableColumn(entity, OptionEnum.ADD);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<PropertyEntity> entityList) {
        return propertyDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<PropertyEntity> entityList, int batchSize) {
        return propertyDao.saveBatch(entityList,batchSize);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateById(PropertyEntity entity) {
        if (MarsConst.propertySet.contains(entity.getPropertyKey())){
            throw new MarsException("系统保留字段，请重新输入");
        }

        PropertyEntity byId = propertyDao.getById(entity.getId());
        if (byId==null){
            throw new MarsException("属性不存在");
        }
        if(!propertyDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }

       /* String tableName = entity.getAppName()+"_"+entity.getTemplateKey();
        List<PropertyEntity> list=new ArrayList<>(2);
        list.add(entity);
        if (StringUtils.isNotEmpty(byId.getVariableKey()) && StringUtils.isEmpty(entity.getVariableKey())){
            PropertyEntity p=PropertyEntity.builder()
                    .templateKey(entity.getTemplateKey())
                    .propertyKey(byId.getPropertyKey()+MarsConst.PROPERTY_PREFIX)
                    .option(OptionEnum.DROP.getOption())
                    .build();
            list.add(p);
        }
        tableColumnDao.editTableColumn(tableName,list, OptionEnum.MODIFY);*/
    }


    public void update(PropertyEntity entity, Wrapper<PropertyEntity> updateWrapper) {
        if(!propertyDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<PropertyEntity> entityList) {
        return propertyDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<PropertyEntity> entityList, int batchSize) {
        return propertyDao.updateBatchById(entityList,batchSize);
    }


    public PropertyEntity selectById(Serializable id) {
        return propertyDao.getById(id);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Serializable id) {
        PropertyEntity byId = propertyDao.getById(id);
        if (byId==null){
            throw new MarsException("属性不存在");
        }
        if(!propertyDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }

        /*String tableName = byId.getAppName()+"_"+byId.getTemplateKey();
        List<PropertyEntity> list=new ArrayList<>(2);
        list.add(byId);
        if (StringUtils.isNotEmpty(byId.getVariableKey()) ){
            PropertyEntity p=PropertyEntity.builder()
                    .templateKey(byId.getTemplateKey())
                    .propertyKey(byId.getPropertyKey()+ MarsConst.PROPERTY_PREFIX)
                    .build();
            list.add(p);
        }
        tableColumnDao.editTableColumn(tableName,list, OptionEnum.DROP);*/
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            for (Serializable id:idList) {
                deleteById(id);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return propertyDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<PropertyEntity> queryWrapper) {
        return propertyDao.remove(queryWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    public void copyProperty(Long[] ids, String appName, String templateKey) {
        List<PropertyEntity> propertyEntities = (List<PropertyEntity>) propertyDao.listByIds(Arrays.asList(ids));
        if (CollectionUtils.isEmpty(propertyEntities)) {
            throw new MarsException("请选择要复制的模板");
        }
        List<PropertyEntity> list = new ArrayList<>(ids.length);
        for (PropertyEntity p : propertyEntities) {
            QueryWrapper<PropertyEntity> q = new QueryWrapper();
            q.eq("app_name", appName);
            q.eq("template_key", templateKey);
            q.eq("property_key", p.getPropertyKey());
            int count = propertyDao.count(q);
            if (count > 0) {
                continue;
            }
            PropertyEntity newP = new PropertyEntity();
            BeanUtils.copyProperties(p, newP);
            newP.setAppName(appName);
            newP.setTemplateKey(templateKey);
            newP.setId(null);
            list.add(newP);
        }
        if (CollectionUtils.isNotEmpty(list)){
            propertyDao.saveBatch(list);


           /* String tableName = appName+"_"+templateKey;
            List<PropertyEntity> propertyList =new ArrayList<>();

            for(PropertyEntity pp : list){
                propertyList.add(pp);
                if(StringUtils.isNotEmpty(pp.getVariableKey())){
                    PropertyEntity p=PropertyEntity.builder()
                            .templateKey(pp.getTemplateKey())
                            .propertyKey(pp.getPropertyKey()+ MarsConst.PROPERTY_PREFIX)
                            .propertyType("varchar")
                            .columnLength(200)
                            .build();
                    propertyList.add(p);
                }
            }
            tableColumnDao.editTableColumn(tableName,propertyList,OptionEnum.ADD);*/
        }
    }

    public Object codeProperty(String appName, String templateKey) {
        QueryWrapper<PropertyEntity> q = new QueryWrapper();
        q.eq("app_name", appName);
        q.eq("template_key", templateKey);
        List<PropertyEntity> list = propertyDao.list(q);
        if (CollectionUtils.isNotEmpty(list)){
            for(PropertyEntity p: list){

                if ("varchar".equals(p.getPropertyType()) || "text".equals(p.getPropertyType())){
                    p.setPropertyType("String");
                }else if ("double".equalsIgnoreCase(p.getPropertyType())){
                    p.setPropertyType("Double");
                }else if ("int".equalsIgnoreCase(p.getPropertyType()) || "tinyint".equalsIgnoreCase(p.getPropertyType())){
                    p.setPropertyType("Integer");
                }else if ("bigint".equalsIgnoreCase(p.getPropertyType())){
                    p.setPropertyType("Long");
                }else if ("decimal".equalsIgnoreCase(p.getPropertyType())){
                    p.setPropertyType("BigDecimal");
                }else if ("date".equalsIgnoreCase(p.getPropertyType()) || "datetime".equalsIgnoreCase(p.getPropertyType()) || "time".equalsIgnoreCase(p.getPropertyType()) || "year".equalsIgnoreCase(p.getPropertyType())){
                    p.setPropertyType("Date");
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("columns",list);
            map.put("className",captureName(templateKey));
            VelocityContext context = new VelocityContext(map);
            StringWriter sw = new StringWriter();

            Template tpl = Velocity.getTemplate("bean.java.vm", "UTF-8");
            tpl.merge(context, sw);

            return sw.toString();
        }

        return null;
    }

    @PostConstruct
    public  void initVelocity() {
        Properties p = new Properties();
        //加载classpath目录下的vm文件
        p.setProperty("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //定义字符集
        p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        // 初始化Velocity引擎，指定配置Properties
        Velocity.init(p);
    }

    /**
     * 将字符串的首字母转大写
     * @param str 需要转换的字符串
     * @return
     */
    private static String captureName(String str) {
        if (StringUtils.isNotEmpty(str)) {
            // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
            char[] cs = str.toCharArray();
            cs[0] -= 32;
            return String.valueOf(cs);
        }
        return "";
    }
}