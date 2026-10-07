package com.console.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 布局配置
 */
@Getter
@Setter
@TableName(value = "s_layout_config")
public class LayoutConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;                     //站点布局配置ID
    private Integer layoutType;             //布局类型：1-熊猫
    private String  previewImg;             //布局预览图片
    private String  showName;               //PWA/快捷方式名称
    private String  showIcon;               //PWA/快捷方式展示图标
    private String  tabOneOffIcon;          //底部导航栏选项卡一未选中状态图片
    private String  tabOneOnIcon;           //底部导航栏选项卡一选中状态图片
    private String  tabTwoOffIcon;          //底部导航栏选项卡二未选中状态图片
    private String  tabTwoOnIcon;           //底部导航栏选项卡二选中状态图片
    private String  tabThreeOffIcon;        //底部导航栏选项卡三未选中状态图片
    private String  tabThreeOnIcon;         //底部导航栏选项卡三选中状态图片
    private String  tabFourOffIcon;         //底部导航栏选项卡四未选中状态图片
    private String  tabFourOnIcon;          //底部导航栏选项卡四选中状态图片
    private String  tabFiveOffIcon;         //底部导航栏选项卡五未选中状态图片
    private String  tabFiveOnIcon;          //底部导航栏选项卡五选中状态图片
    private String  bottomNavBgImg;         //底部导航栏背景图
    private String  topNavLogo;             //顶部导航栏Logo
    private String  downLoadLogo;           //下载栏Logo
    private String  webIcon;                //浏览器标签页Icon(favicon)
    private String  drawerIcon;             //侧拉抽屉Icon
    private String  placeholderIcon;        //占位Icon
    private String  topIcon;                //置顶图标
    private String  dominantColor;          //主色调
    private String  backgroundColor;        //背景色
    private Integer devModeEnabled;         //是否允许开启开发者模式：0-不允许，1-允许
}
