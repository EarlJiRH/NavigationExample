package com.example.navigation.model;

/**
 * ================================================
 * 类名：com.example.navigation.model
 * 时间：2021/8/31 17:30
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 *
 * @author Admin
 */
public class Destination {
    public boolean asStarter;//是否作为路由的第一个启动页
    public String clazName;//全类名
    public String destType;//路由节点(页面)的类型,activity,fragment,dialog
    public int id;//路由节点(页面)的id
    public String pageUrl;//页面url
}
