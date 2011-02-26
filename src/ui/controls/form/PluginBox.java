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
    private boolean state;
    private String text;
    private int edit;

    public PluginBox(String text, boolean state, int edit) {
        super(RosterIcons.getInstance());

        this.text = text;
        this.state = state;
        this.edit = edit;
    }
    
    public String toString() {
        return text;
    }

    public void onSelect(VirtualList view) {
        if (edit > 0) {
            state = !state;
            Config config = Config.getInstance();
            switch (edit) {
                case 1:
                    config.module_autostatus = !config.module_autostatus;
                    break;
                case 2:
                    config.userKeys = !config.userKeys;
                    break;
                case 3:
                    config.module_avatars = !config.module_avatars;
                    break;
                case 4:
                    config.module_history = !config.module_history;
                    break;
                case 5:
                    config.module_ie = !config.module_ie;
                    break;
                case 6:
                    config.module_tasks = !config.module_tasks;
                    break;
                case 7:
                    config.module_classicchat = !config.module_classicchat;
                    break;
                case 8:
                    config.debug = !config.debug;
                    break;
            }
        }
    }
    
    public int getImageIndex() {
        return edit > 0 ? (state ? 0x36 : 0x37) : 0x36;
    }

    public boolean getValue() {
        return state;
    }

    public boolean isSelectable() {
        return true;
    }
}