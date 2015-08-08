package com.bananaplan.workflowandroid.drawermenu;

import android.graphics.drawable.Drawable;


/**
 * @author Danny Lin
 * @since 2015/8/8.
 */
public class DrawerItem {

    public static final class LayoutTemplate {
        public static final int SETTING = 0;
        public static final int INFO_COUNT = 1;
        public static final int LR_ICON = 2;
        public static final int GROUP = 3;
    }

    public enum Type {
        SETTING, INFO_COUNT, NORMAL, GROUP
    }

    public int clickId = -1;
    public int layoutTemplate;
    public Type type;

    public String text;
    public Drawable leftIcon;
    public Drawable rightIcon;
    public int infoCount = 0;
    public DrawerSubItem[] subItems;


    /**
     * For setting item.
     */
    public DrawerItem(Drawable leftIcon, String text) {
        this.layoutTemplate = LayoutTemplate.SETTING;
        this.type = Type.SETTING;
        this.text = text;
        this.leftIcon = leftIcon;
    }

    /**
     * For normal items.
     */
    public DrawerItem(int clickId, String text, Drawable leftIcon) {
        this.clickId = clickId;
        this.layoutTemplate = LayoutTemplate.LR_ICON;
        this.type = Type.NORMAL;
        this.text = text;
        this.leftIcon = leftIcon;
    }

    /**
     * For Info count page
     */
    public DrawerItem(int clickId, String text, Drawable leftIcon, int infoCount) {
        this.clickId = clickId;
        this.layoutTemplate = LayoutTemplate.INFO_COUNT;
        this.type = Type.INFO_COUNT;
        this.text = text;
        this.leftIcon = leftIcon;
        this.infoCount = infoCount;
    }

    /**
     * For group items.
     */
    public DrawerItem(String text, Drawable leftIcon, Drawable rightIcon, DrawerSubItem[] subItems) {
        this.layoutTemplate = LayoutTemplate.GROUP;
        this.type = Type.GROUP;
        this.text = text;
        this.leftIcon = leftIcon;
        this.rightIcon = rightIcon;
        this.subItems = subItems;
    }
}
