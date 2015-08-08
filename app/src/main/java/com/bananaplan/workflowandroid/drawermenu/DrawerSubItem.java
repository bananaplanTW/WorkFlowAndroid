package com.bananaplan.workflowandroid.drawermenu;


/**
 * @author Danny Lin
 * @since 2015/8/8.
 */
public class DrawerSubItem {

    public String text;
    public int clickId = -1;


    public DrawerSubItem(String text, int clickId) {
        this.text = text;
        this.clickId = clickId;
    }
}
