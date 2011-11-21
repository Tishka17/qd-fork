/*
 * ConfigurationMaster.java

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package account;

import client.Config;
import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualList;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;

public final class ConfigurationMaster extends DefForm {
    private static final int PAGES_COUNT = 3;

    private static final int CONTACTS_PAGE = 1;
    private static final int CHAT_PAGE = 2;
    private static final int UI_PAGE = 3;

    private static final int DEFAULT = 0;
    private static final int SIMPLE = 1;
    private static final int MEDIUM = 2;
    private static final int DETAILED = 3;

    private int cursorPos[];

    private int currentPage = 1;

    public ConfigurationMaster() {
        super("");
        cursorPos = new int[PAGES_COUNT];

        updateForm();
    }

    private void updateForm() {
        StringBuffer cap = new StringBuffer();

        itemsList = null;
        itemsList = new Vector();

        addControl(new MultiLine(
                SR.get(SR.MS_AS_IS),
                SR.get(SR.MS_KEEP_CURRENT_SETTINGS)));
        switch (currentPage) {
            case CONTACTS_PAGE:
                cap.append(SR.get(SR.MS_CONTACTS));
                addControl(new MultiLine(
                        SR.get(SR.MS_SIMPLE),
                        SR.get(SR.MS_SIMPLE_CONTACT_VIEW)));
                addControl(new MultiLine(
                        SR.get(SR.MS_MEDIUM),
                        SR.get(SR.MS_MEDIUM_CONTACT_VIEW)));
                addControl(new MultiLine(
                        SR.get(SR.MS_DETAILED),
                        SR.get(SR.MS_DETAILED_CONTACT_VIEW)));
                break;
            case CHAT_PAGE:
                cap.append(SR.get(SR.MS_CHATS));
                addControl(new MultiLine(
                        SR.get(SR.MS_SIMPLE),
                        SR.get(SR.MS_SIMPLE_CHAT_VIEW)));
                addControl(new MultiLine(
                        SR.get(SR.MS_MEDIUM),
                        SR.get(SR.MS_MEDIUM_CHAT_VIEW)));
                addControl(new MultiLine(
                        SR.get(SR.MS_DETAILED),
                        SR.get(SR.MS_DETAILED_CHAT_VIEW)));
                break;
            case UI_PAGE:
                cap.append(SR.get(SR.MS_APPEARANCE));
                addControl(new MultiLine(
                        SR.get(SR.MS_SIMPLE),
                        SR.get(SR.MS_SIMPLE_APPEARANCE)));
                addControl(new MultiLine(
                        SR.get(SR.MS_MEDIUM),
                        SR.get(SR.MS_MEDIUM_APPEARANCE)));
                addControl(new MultiLine(
                        SR.get(SR.MS_DETAILED),
                        SR.get(SR.MS_DETAILED_APPEARANCE)));
                break;
        }
        cap.append(" (");
        cap.append(currentPage);
        cap.append('/');
        cap.append(PAGES_COUNT);
        cap.append(')');

        setMainBarItem(new MainBar(cap.toString()));

        moveCursorTo(cursorPos[currentPage - 1]);
        redraw();
    }

    private void applySettings() {
        switch (getSelectedIndex(CONTACTS_PAGE)) {
            case SIMPLE:
                Config.simpleContacts = true;
//#ifdef AVATARS
//#                 Config.auto_queryPhoto = false;
//#endif
                Config.showResources = false;
                break;
            case MEDIUM:
                Config.simpleContacts = false;
                Config.rosterStatus = false;
//#ifdef CLIENTS_ICONS
//#                 Config.showClientIcon = true;
//#endif
//#ifdef AVATARS
//#                 Config.auto_queryPhoto = false;
//#endif
                Config.showResources = false;
                break;
            case DETAILED:
                Config.simpleContacts = false;
                Config.rosterStatus = true;
//#ifdef CLIENTS_ICONS
//#                 Config.showClientIcon = true;
//#endif
//#ifdef AVATARS
//#                 Config.auto_queryPhoto = true;
//#endif
                Config.showResources = true;
                break;
        }
        switch (getSelectedIndex(CHAT_PAGE)) {
            case SIMPLE:
                Config.showNickNames = false;
                Config.storeConfPresence = false;
                Config.hideMessageIcon = true;
                Config.showTimeInMsgs = false;
                Config.showCollapsedPresences = true;
                break;
            case MEDIUM:
                Config.showNickNames = false;
                Config.storeConfPresence = true;
                Config.hideMessageIcon = false;
                Config.showTimeInMsgs = true;
                Config.showCollapsedPresences = true;
                break;
            case DETAILED:
                Config.showNickNames = true;
                Config.storeConfPresence = true;
                Config.hideMessageIcon = false;
                Config.showTimeInMsgs = true;
                Config.showCollapsedPresences = true;
        }
        switch (getSelectedIndex(UI_PAGE)) {
            case SIMPLE:
                Config.panelsState = 0;
                Config.showTimeTraffic = false;
//#ifdef POPUPS
//#                 Config.popUps = false;
//#endif
                Config.showBalloons = false;
                Config.gradient_cursor = false;
                break;
            case MEDIUM:
                Config.panelsState = 2;
                Config.showTimeTraffic = false;
//#ifdef POPUPS
//#                 Config.popUps = true;
//#endif
                Config.showBalloons = false;
                Config.gradient_cursor = true;
                break;
            case DETAILED:
                Config.panelsState = 2;
                Config.showTimeTraffic = true;
//#ifdef POPUPS
//#                 Config.popUps = true;
//#endif
                Config.showBalloons = true;
                Config.gradient_cursor = true;
                break;
        }
        VirtualList.updatePanelsState();
        Config.getInstance().saveToStorage();
    }

    private int getSelectedIndex(int page) {
        return cursorPos[page - 1];
    }

    private void setSelectedIndex(int page, int index) {
        cursorPos[page - 1] = index;
    }

    public String touchLeftCommand() {
        return SR.get((currentPage == PAGES_COUNT) ?
                SR.MS_SAVE : SR.MS_NEXT);
    }

    public void cmdOk() {
        setSelectedIndex(currentPage, cursor);

        if (currentPage == PAGES_COUNT) {
            applySettings();
            destroyView();
        } else {
            ++currentPage;
            updateForm();
        }
    }

    public void cmdCancel() {
        if (currentPage == 1) {
            destroyView();
        } else {
            setSelectedIndex(currentPage, cursor);

            --currentPage;
            updateForm();
        }
    }

    public void eventOk(){
	cmdOk();
    }
}
