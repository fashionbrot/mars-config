package com.gitee.mars.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.gitee.mars.common.annotation.IsMenu;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.common.vo.RespVo;
import com.gitee.mars.dao.dao.MenuDao;
import com.gitee.mars.dao.dao.MenuRoleRelationDao;
import com.gitee.mars.dao.entity.Menu;
import com.gitee.mars.dao.entity.MenuRoleRelation;
import com.gitee.mars.dao.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MenuService {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private MenuRoleRelationDao menuRoleRelationDao;

    @Autowired
    private UserInfoService userInfoService;

    private static Map<Long, List<Menu>> MENU_LIST_MAP = new ConcurrentHashMap<Long, List<Menu>>();


    /**
     * true 有权限  false 无权限
     *
     * @return
     */

    public boolean checkPermissionUrl(Object handler, HttpServletRequest request) {

        if (handler instanceof HandlerMethod) {

            String requestUrl = request.getRequestURI();
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 判断接口是否需要登录
            IsMenu methodAnnotation = method.getAnnotation(IsMenu.class);
            if (methodAnnotation != null && methodAnnotation.checkMenuUrlPermission()) {
                Long userId = userInfoService.getSessionUser();
                if (userId == null) {
                    log.info(" checkPermissionUrl userInfo is null");
                    return true;
                }
                List<Menu> menuBarList = getMenus(userId);

                if (CollectionUtils.isNotEmpty(menuBarList)) {
                    for (Menu m : menuBarList) {
                        if (m.getMenuLevel() == 2 && equRequestUrl(m.getMenuUrl(), requestUrl)) {
                            return true;
                        }
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 清除菜单缓存
     */

    public void clearMenuList() {
        MENU_LIST_MAP.clear();
    }


    public void add(Menu appInfo) {
        if (menuDao.add(appInfo) != 1) {
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    public void update(Menu appInfo) {
        Integer result = menuDao.update(appInfo);
        if (result == 1) {
            clearMenuList();
        } else {
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if (menuDao.deleteById(id) != 1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public Menu queryById(Long id) {
        return menuDao.queryById(id);
    }


    public List<Menu> queryAll() {
        List<Menu> menuBarList = menuDao.queryAll(null);
        if (CollectionUtils.isNotEmpty(menuBarList)) {
            for (Menu m : menuBarList) {
                if (m.getMenuLevel() != 1) {
                    m.setParentMenuName(parentMenuName(menuBarList, m.getParentMenuId()));
                }
            }
        }

        return menuBarList;
    }

    private String parentMenuName(List<Menu> menuBarList, Long parentMenuId) {
        if (CollectionUtils.isNotEmpty(menuBarList)) {
            for (Menu m : menuBarList) {
                if (m.getMenuLevel() == 1 && Objects.equals(m.getId(), parentMenuId)) {
                    return m.getMenuName();
                }
            }
        }
        return "";
    }


    public List<Menu> queryMenuLevel(Menu menuBar) {
        return menuDao.queryAll(new QueryWrapper<Menu>().eq("menu_level", menuBar.getMenuLevel()));
    }


    public void loadMenuList(Object handler, HttpServletRequest request) {

        if (handler instanceof HandlerMethod) {
            Long userId = userInfoService.getSessionUser();
            if (userId == null) {
                log.info(" loadMenuList userInfo is null");
                return;
            }
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            String requestUrl = request.getRequestURI();
            // 判断接口是否需要登录
            IsMenu methodAnnotation = method.getAnnotation(IsMenu.class);

            Optional.ofNullable(methodAnnotation).ifPresent(mm -> {
                List<Menu> menuBarList = getMenus(userId);
                if (CollectionUtils.isNotEmpty(menuBarList)) {
                    menuBarList = searchChildMenu(menuBarList, requestUrl);
                }
                request.setAttribute("menuList", menuBarList);
            });
        }
    }

    private List<Menu> getMenus(Long userId) {
        List<Menu> menuBarList;
        /*if (CollectionUtils.isNotEmpty(MENU_LIST_MAP) && MENU_LIST_MAP.containsKey(userId)) {
            menuBarList = MENU_LIST_MAP.get(userId);
        } else {*/
        menuBarList = menuDao.selectMenuRoleByUser(userId);
            /*if (CollectionUtils.isNotEmpty(menuBarList)) {
                MENU_LIST_MAP.put(userId, menuBarList);
            }*/
        /*}*/
        return menuBarList;
    }


    public List<Menu> loadCheckedMenu(Long roleId) {

        return menuDao.selectMenuRole(roleId);
    }


    public List<Menu> loadAllMenu(Long roleId) {
        List<Menu> checkedList = menuDao.selectMenuRole(roleId);
        Map<String, Boolean> checkedMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(checkedList)) {
            for (Menu mm : checkedList) {
                checkedMap.put(mm.getId().toString(), true);
            }
        }

        List<Menu> menuBarList = menuDao.queryAll(new QueryWrapper<Menu>().orderByAsc("priority"));
        if (CollectionUtils.isNotEmpty(menuBarList)) {
            menuBarList = loadChildMenu(menuBarList, checkedMap);
        }


        return menuBarList;
    }


    @Transactional(rollbackFor = Exception.class)
    public RespVo updateRoleMenu(Long roleId, String ids) {
        menuRoleRelationDao.delete(new QueryWrapper<MenuRoleRelation>().eq("role_id", roleId));
        if (StringUtils.isNotEmpty(ids)) {
            String[] menuIds = ids.split(",");
            Date date = new Date();
            for (String menuId : menuIds) {
                menuRoleRelationDao.insert(MenuRoleRelation.builder()
                        .createDate(date)
                        .roleId(roleId)
                        .menuId(Long.valueOf(menuId))
                        .build());
            }
        }
        clearMenuList();
        return RespVo.success();
    }


    private List<Menu> loadChildMenu(List<Menu> menuBarList, Map<String, Boolean> checkedMap) {
        if (CollectionUtils.isNotEmpty(menuBarList)) {
            List<Menu> menuList = new ArrayList<>(15);
            for (Menu m : menuBarList) {
                if (m.getMenuLevel() != 1) {
                    continue;
                }
                if (checkedMap.containsKey(m.getId().toString())) {
                    m.setActive(1);
                }
                m.setChildMenu(loadChildMenu(menuBarList, m, checkedMap));
                menuList.add(m);
            }
            return menuList;
        }
        return null;
    }

    private List<Menu> loadChildMenu(List<Menu> menuBarList, Menu parentMenu, Map<String, Boolean> checkedMap) {
        if (CollectionUtils.isNotEmpty(menuBarList)) {
            List<Menu> menuList = new ArrayList<>(10);
            for (Menu m : menuBarList) {
                if (Objects.equals(m.getParentMenuId(), parentMenu.getId())) {
                    if (checkedMap.containsKey(m.getId().toString())) {
                        m.setActive(1);
                    }
                    menuList.add(m);
                }
            }
            return menuList;
        }
        return null;
    }


    private List<Menu> searchChildMenu(List<Menu> menuBarList, String requestUrl) {
        if (CollectionUtils.isNotEmpty(menuBarList)) {
            List<Menu> menuList = new ArrayList<>(10);
            for (Menu m : menuBarList) {
                if (m.getMenuLevel() != 1) {
                    continue;
                }
                m.setChildMenu(getChildMenu(menuBarList, m, requestUrl));
                menuList.add(m);
            }
            return menuList;
        }
        return null;
    }

    private boolean equRequestUrl(String url, String requestUrl) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(requestUrl)) {
            return false;
        }
        url = url.replaceAll("/", "");
        requestUrl = requestUrl.replaceAll("/", "");
        return url.equals(requestUrl);
    }

    private List<Menu> getChildMenu(List<Menu> menuBarList, Menu parentMenu, String requestUrl) {
        if (CollectionUtils.isNotEmpty(menuBarList)) {
            List<Menu> menuList = new ArrayList<>(5);
            boolean parentMenuActive = false;
            for (Menu m : menuBarList) {
                if (Objects.equals(m.getParentMenuId(), parentMenu.getId())) {
                    if (equRequestUrl(m.getMenuUrl(), requestUrl)) {
                        parentMenu.setActive(1);
                        m.setActive(1);
                        parentMenuActive = true;
                    } else {
                        m.setActive(0);
                        if (!parentMenuActive) {
                            parentMenu.setActive(0);
                        }
                    }
                    menuList.add(m);
                }
            }
            return menuList;
        }
        return null;
    }

}
