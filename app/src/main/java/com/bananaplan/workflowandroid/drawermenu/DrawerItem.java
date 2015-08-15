package com.bananaplan.workflowandroid.drawermenu;

import android.graphics.drawable.Drawable;


/**
 * Data structure of an item in drawer
 *
 * @author Danny Lin
 * @since 2015/8/8.
 */
public class DrawerItem {

    public static final class LayoutTemplate {
        public static final int SETTING = 0;
        public static final int INFO = 1;
        public static final int NORMAL = 2;
        public static final int GROUP = 3;
    }

    public int clickId = -1;
    public int layoutTemplate;

    public String text;
    public Drawable leftIcon;
    public int infoCount = 0;
    public DrawerSubItem[] subItems;


    public static DrawerItem generateSettingItem(Drawable leftIcon, String text) {
        return new DrawerItem(leftIcon, text);
    }

    public static DrawerItem generateInfoItem(int clickId, int infoCount) {
        return new DrawerItem(clickId, infoCount);
    }

    public static DrawerItem generateNormalItem(int clickId, String text, Drawable leftIcon) {
        return new DrawerItem(clickId, text, leftIcon);
    }

    public static DrawerItem generateGroupItem(String text, Drawable leftIcon, DrawerSubItem[] subItems) {
        return new DrawerItem(text, leftIcon, subItems);
    }

    /**
     * For setting item.
     */
    private DrawerItem(Drawable leftIcon, String text) {
        this.layoutTemplate = LayoutTemplate.SETTING;
        this.text = text;
        this.leftIcon = leftIcon;
    }

    /**
     * For main info item
     */
    private DrawerItem(int clickId, int infoCount) {
        this.clickId = clickId;
        this.layoutTemplate = LayoutTemplate.INFO;
        this.infoCount = infoCount;
    }

    /**
     * For normal items.
     */
    private DrawerItem(int clickId, String text, Drawable leftIcon) {
        this.clickId = clickId;
        this.layoutTemplate = LayoutTemplate.NORMAL;
        this.text = text;
        this.leftIcon = leftIcon;
    }

    /**
     * For group items.
     */
    private DrawerItem(String text, Drawable leftIcon, DrawerSubItem[] subItems) {
        this.layoutTemplate = LayoutTemplate.GROUP;
        this.text = text;
        this.leftIcon = leftIcon;
        this.subItems = subItems;
    }
}
