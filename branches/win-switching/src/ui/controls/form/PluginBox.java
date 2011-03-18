/*
 * PluginBox.java
 *
 * Created on 29.07.2009, 16:05
 *
 */

package ui.controls.form;

import client.Config;
import ui.IconTextElement;
import ui.VirtualList;
import images.RosterIcons;

/**
 *
 * @author aqent
 */

public class PluginBox extends IconTextElement {
    public static final int STANDART = 0;
    public static final int AUTOSTATUS = 1;
    public static final int USERKEYS = 2;
    public static final int AVATARS = 3;
    public static final int HISTORY = 4;
    public static final int IMPORT_EXPORT = 5;
    public static final int TASKS = 6;
    public static final int CLASSIC_CHAT = 7;
    public static final int DEBUG = 8;

    private boolean isChecked;
    private String text;
    private int type;

    public PluginBox(String text) {
        this(text, true, STANDART);
    }

    public PluginBox(String text, boolean isChecked, int type) {
        super(RosterIcons.getInstance());

        this.text = text;
        this.isChecked = isChecked;
        this.type = type;
    }

    public String toString() {
        return text;
    }

    public void onSelect(VirtualList view) {
        if (type != STANDART) {
            isChecked = !isChecked;
            Config config = Config.getInstance();
            switch (type) {
                case AUTOSTATUS:
                    config.module_autostatus = !config.module_autostatus;
                    break;
                case USERKEYS:
                    config.userKeys = !config.userKeys;
                    break;
                case AVATARS:
                    config.module_avatars = !config.module_avatars;
                    break;
                case HISTORY:
                    config.module_history = !config.module_history;
                    break;
                case IMPORT_EXPORT:
                    config.module_ie = !config.module_ie;
                    break;
                case TASKS:
                    config.module_tasks = !config.module_tasks;
                    break;
                case CLASSIC_CHAT:
                    config.module_classicchat = !config.module_classicchat;
                    break;
                case DEBUG:
                    config.debug = !config.debug;
                    break;
            }
        }
    }

    public int getImageIndex() {
        return type > 0 ? (isChecked ? 0x36 : 0x37) : 0x36;
    }

    public boolean isSelectable() {
        return true;
    }
}