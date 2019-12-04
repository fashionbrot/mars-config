package com.fashion.mars.dao.dao.provider;

import com.fashion.mars.dao.entity.MenuRoleRelation;
import com.fashion.mars.dao.entity.UserInfo;
import org.apache.ibatis.jdbc.SQL;

public class MenuRoleRelationProvider {


    public String selectMenuRole(MenuRoleRelation relation){
        return new SQL(){
            {
                SELECT("a.id,a.parent_menu_id,a.menu_name");
                FROM("  menu a");
                INNER_JOIN("  menu_role_relation b on a.id=b.menu_id ");
                INNER_JOIN("  role_info c on c.id=b.role_id ");
                WHERE ("b.role_id=#{roleId} and c.`status`=1 ");
                ORDER_BY("a.priority ASC");
            }
        }.toString();
    }

    public String selectMenuRoleByUser(UserInfo userInfo){
        return new SQL(){
            {
                SELECT("e.id,e.menu_name,e.menu_level,e.menu_url,e.parent_menu_id,e.priority");
                FROM("  user_role_relation b  ");
                INNER_JOIN("  role_info c on c.id=b.role_id ");
                INNER_JOIN(" menu_role_relation d on d.role_id=c.id ");
                INNER_JOIN(" menu e on e.id=d.menu_id ");

                WHERE ("b.user_id=#{id} and c.`status`='1' ");
                ORDER_BY("e.priority ASC");
            }
        }.toString();
    }


}
