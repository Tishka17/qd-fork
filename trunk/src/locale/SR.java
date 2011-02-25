/*
 * SR.java
 *
 * Created on 19.03.2006, 15:06
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
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

package locale; 

import client.Config;
import midlet.Commands;
import util.StringLoader;

public class SR {

     public final static short MS_ONLINE  = 2; //online
     public final static short MS_CHAT  = 3; //chat
     public final static short MS_AWAY  = 4; //away
     public final static short MS_XA  = 5; //xa
     public final static short MS_INVISIBLE  = 6; //invisible
     public final static short MS_DND  = 7; //dnd
     public final static short MS_OFFLINE  = 8; //offline
     public final static short MS_JID  = 9; //Jid
     public final static short MS_PRIVACY_LISTS  = 10; //Privacy Lists
     public final static short MS_MESSAGE_FONT  = 11; //Message font
     public final static short MS_ROSTER_FONT  = 12; //Roster font
     public final static short MS_PASTE_BODY  = 13; //Paste Body
     public final static short MS_CONFIG_ROOM  = 14; //Configure Room
     public final static short MS_PASTE_SUBJECT  = 15; //Paste Subject
     public final static short MS_DISCO  = 16; //Service Discovery
     public final static short MS_USER_JID  = 17; //User JID
     public final static short MS_NEW_LIST  = 18; //New list
     public final static short MS_NOLOGIN  = 19; //Select (no login)
     public final static short MS_PRIVACY_RULE  = 20; //Privacy rule
     public final static short MS_SSL  = 21; //use SSL
     public final static short MS_MODIFY  = 22; //Modify
     public final static short MS_UPDATE  = 23; //Update
     public final static short MS_ACCOUNT_NAME  = 24; //Account name
     public final static short MS_GMT_OFFSET  = 25; //GMT offset
     public final static short MS_TIME_SETTINGS  = 26; //Time settings (hours)
     public final static short MS_CONNECTED  = 27; //Connected
     public final static short MS_CONNECT_TO_ = 28; //Connect to 
     public final static short MS_ALERT_PROFILE  = 29; //Alert Profile
     public final static short MS_MOVE_UP  = 30; //Move Up
     public final static short MS_OWNERS  = 31; //Owners
     public final static short MS_OK  = 32; //Ok
     public final static short MS_APP_MINIMIZE  = 33; //Minimize
     public final static short MS_ROOM  = 34; //Room
     public final static short MS_MESSAGES  = 35; //Messages
     public final static short MS_REFRESH  = 36; //Refresh
     public final static short MS_RESOLVE_NICKNAMES  = 37; //Resolve Nicknames
     public final static short MS_PRIVACY_ACTION  = 38; //Action
     public final static short MS_ACTION = 39; //Action
     public final static short MS_BAN  = 40; //Ban
     public final static short MS_LEAVE_ROOM  = 41; //Leave Room
     public final static short MS_PASSWORD  = 42; //Password
     public final static short MS_ITEM_ACTIONS  = 43; //Actions
     public final static short MS_ACTIVATE  = 44; //Activate
     public final static short MS_AFFILIATION  = 45; //Affiliation
     public final static short MS_ACCOUNTS  = 46; //Accounts
     public final static short MS_DELETE_LIST  = 47; //Delete list
     public final static short MS_ACCOUNT_ = 48; //Account >
     public final static short MS_SELECT  = 49; //Select
     public final static short MS_SUBJECT  = 50; //Subject
     public final static short MS_APP_QUIT  = 51; //Quit
     public final static short MS_EDIT_LIST  = 52; //Edit list
     public final static short MS_REGISTERING  = 53; //Registering
     public final static short MS_DONE  = 54; //Done
     public final static short MS_ERROR_  = 55; //Error: 
     public final static short MS_BROWSE  = 56; //Browse
     public final static short MS_DISCOVER  = 57; //Browse
     public final static short MS_SAVE_LIST  = 58; //Save list
     public final static short MS_KEEPALIVE_PERIOD  = 59; //Keep-Alive period
     public final static short MS_NEWGROUP  = 60; //<New Group>
     public final static short MS_SEND  = 61; //Send
     public final static short MS_PRIORITY  = 62; //Priority
     public final static short MS_FAILED  = 63; //Failed
     public final static short MS_SET_PRIORITY  = 64; //Set Priority
     public final static short MS_DELETE_RULE  = 65; //Delete rule
     public final static short MS_IGNORE_LIST  = 66; //Ignore-List
     public final static short MS_ROSTER_REQUEST  = 67; //Roster request
     public final static short MS_PRIVACY_TYPE  = 68; //Type
     public final static short MS_NAME  = 69; //Name
     public final static short MS_USER_PROFILE  = 70; //User Profile
     public final static short MS_FULLSCREEN  = 71; //fullscreen
     public final static short MS_ADD_BOOKMARK  = 72; //Add bookmark
     public final static short MS_CONFERENCES_ONLY  = 73; //conferences only
     public final static short MS_CLIENT_INFO  = 74; //Client Version
     public final static short MS_DISCARD  = 75; //Discard Search
     public final static short MS_SEARCH_RESULTS  = 76; //Search Results
     public final static short MS_MEMBERS  = 77; //Members
     public final static short MS_ADD_CONTACT  = 78; //Add Contact
     public final static short MS_SUBSCRIPTION  = 79; //Subscription
     public final static short MS_STATUS_MENU  = 80; //Status
     public final static short MS_STATUS  = 81; //Status
     public final static short MS_JOIN  = 82; //Join
     public final static short MS_STARTUP_ACTIONS  = 83; //Startup actions
     public final static short MS_SERVER  = 84; //Server
     public final static short MS_ADMINS  = 85; //Admins
     public final static short MS_MK_ILIST  = 86; //Make Ignore-List
     public final static short MS_GENERAL  = 87; //General Group
     public final static short MS_OPTIONS  = 88; //Options
     public final static short MS_DELETE  = 89; //Delete
     public final static short MS_DELETE_ASK  = 90; //Delete contact?
     public final static short MS_SUBSCRIBE  = 91; //Authorize
     public final static short MS_NICKNAMES  = 92; //Nicknames
     public final static short MS_ADD_ARCHIVE  = 93; //to Archive
     public final static short MS_BACK  = 94; //Back
     public final static short MS_HEAP_MONITOR  = 95; //heap monitor
     public final static short MS_KEYS_FOR_OLD_SE = 96; //change softkeys
     public final static short MS_MESSAGE  = 97; //Message
     public final static short MS_OTHER  = 98; //<Other>
     public final static short MS_APPEND  = 99; //Append
     public final static short MS_ACTIVE_CONTACTS  = 100; //Active Contacts
     public final static short MS_SELECT_NICKNAME  = 101; //Select nickname
     public final static short MS_GROUP  = 102; //Group
     public final static short MS_JOIN_CONFERENCE  = 103; //Join conference
     public final static short MS_NO  = 104; //No
     public final static short MS_REENTER  = 105; //Re-Enter Room
     public final static short MS_NEW_MESSAGE  = 106; //New Message
     public final static short MS_ADD  = 107; //Add
     public final static short MS_LOGON  = 108; //Logon
     public final static short MS_STANZAS  = 109; //Stanzas
     public final static short MS_AT_HOST  = 110; //at Host
     public final static short MS_AUTO_CONFERENCES  = 111; //join conferences
     public final static short MS_STATUS_MENU_  = 112; //Status
     public final static short MS_STATUS_  = 113; //Status
     public final static short MS_SMILES_TOGGLE  = 114; //Smiles
     public final static short MS_CONTACT  = 115; //Contact >
     public final static short MS_OFFLINE_CONTACTS  = 116; //offline contacts
     public final static short MS_TRANSPORT  = 117; //Transport
     public final static short MS_COMPOSING_EVENTS  = 118; //composing events
     public final static short MS_ADD_SMILE  = 119; //Add Smile
     public final static short MS_NICKNAME  = 120; //Nickname
     public final static short MS_REVOKE_VOICE  = 121; //Revoke Voice
     public final static short MS_NOT_IN_LIST  = 122; //Not-in-list
     public final static short MS_COMMANDS  = 123; //Commands
     public final static short MS_CHSIGN  = 124; //- (Sign)
     public final static short MS_SETDEFAULT  = 125; //Set default
     public final static short MS_BANNED  = 126; //Outcasts (Ban)
     public final static short MS_SET_AFFILIATION  = 127; //Set affiliation to
     public final static short MS_REGISTER_ACCOUNT  = 128; //Register Account
     public final static short MS_AUTOLOGIN  = 129; //autologin
     public final static short MS_LOGOFF  = 130; //Logoff
     public final static short MS_PUBLISH  = 131; //Publish
     public final static short MS_SUBSCR_REMOVE  = 132; //Remove subscription
     public final static short MS_SET  = 133; //Set
     public final static short MS_APPLICATION  = 134; //Application
     public final static short MS_appStr  = 135; //Application
     public final static short MS_BOOKMARKS  = 136; //Bookmarks
     public final static short MS_TEST_SOUND  = 137; //Test sound
     public final static short MS_STARTUP  = 138; //Startup
     public final static short MS_EDIT_RULE  = 139; //Edit rule
     public final static short MS_CANCEL  = 140; //Cancel
     public final static short MS_CLOSE  = 141; //Close
     public final static short MS_ARCHIVE  = 142; //Archive
     public final static short MS_CONFERENCE  = 143; //Conference
     public final static short MS_SOUND  = 144; //Sound
     public final static short MS_LOGIN_FAILED  = 145; //Login failed
     public final static short MS_BROWSE_  = 146; //Browse
     public final static short MS_DISCOVER_  = 147; //Browse
     public final static short MS_NEW_JID  = 148; //New Jid
     public final static short MS_PLAIN_PWD  = 149; //plain-text password
     public final static short MS_PASTE_NICKNAME  = 150; //Paste Nickname
     public final static short MS_KICK  = 151; //Kick
     public final static short MS_CLEAR_LIST  = 152; //Remove readed
     public final static short MS_GRANT_VOICE  = 153; //Grant Voice
     public final static short MS_MOVE_DOWN  = 154; //Move Down
     public final static short MS_QUOTE  = 155; //Quote
     public final static short MS_ROSTER_ELEMENTS  = 156; //Roster elements
     public final static short MS_ENABLE_POPUP  = 157; //popup from background
     public final static short MS_SMILES  = 158; //smiles
     public final static short MS_ABOUT  = 159; //About
     public final static short MS_RESOURCE  = 160; //Resource
     public final static short MS_DISCONNECTED  = 161; //Disconnected
     public final static short MS_EDIT  = 162; //Edit
     public final static short MS_HOST_IP  = 163; //Host name/IP (optional)
     public final static short MS_ADD_RULE  = 164; //Add rule
     public final static short MS_ALL_STATUSES  = 165; //for all status types
     public final static short MS_PASTE_JID  = 166; //Paste Jid
     public final static short MS_GOTO_URL  = 167; //Goto URL
     public final static short MS_CLOCK_OFFSET  = 168; //Clock offset
     public final static short MS_YES  = 169; //Yes
     public final static short MS_SUSPEND  = 170; //Suspend
     public final static short MS_ALERT_PROFILE_CMD  = 171; //Alert Profile >
     public final static short MS_MY_VCARD  = 172; //My vCard
     public final static short MS_TRANSPORTS  = 173; //transports
     public final static short MS_NEW_ACCOUNT  = 174; //Add Profile
     public final static short MS_SELF_CONTACT  = 175; //self-contact
     public final static short MS_VCARD  = 176; //vCard
     public final static short MS_SET_SUBJECT  = 177; //Set Subject
     public final static short MS_PORT  = 178; //Port
     public final static short MS_RESUME  = 179; //Resume Message
     public final static short MS_ARE_YOU_SURE_WANT_TO_DISCARD  = 180; //Are You sure want to discard 
     public final static short MS_FROM_OWNER_TO  = 181; // from OWNER to 
     public final static short MS_MODIFY_AFFILIATION  = 182; //Modify affiliation
     public final static short MS_CLEAR = 183; //Clear
     public final static short MS_SELLOGIN  = 184; //Connect
     public final static short MS_UNAFFILIATE  = 185; //Unaffiliate
     public final static short MS_GRANT_MODERATOR  = 186; //Grant Moderator
     public final static short MS_REVOKE_MODERATOR  = 187; //Revoke Moderator
     public final static short MS_GRANT_ADMIN  = 188; //Grant Admin
     public final static short MS_GRANT_OWNERSHIP  = 189; //Grant Ownership
     public final static short MS_VIZITORS_FORBIDDEN = 190; //Visitors are not allowed to send messages to all occupants
     public final static short MS_IS_INVITING_YOU = 191; // is inviting You to 
     public final static short MS_ASK_SUBSCRIPTION = 192; //Ask subscription
     public final static short MS_GRANT_SUBSCRIPTION = 193; //Grant subscription
     public final static short MS_INVITE = 194; //Invite to conference
     public final static short MS_REASON = 195; //Reason
     public final static short MS_YOU_HAVE_BEEN_INVITED = 196; //You have been invited to 
     public final static short MS_DISCO_ROOM = 197; //Participants
     public final static short MS_CAPS_STATE = 198; //Abc
     public final static short MS_STORE_PRESENCE  = 199; //room presences
     public final static short MS_IS_NOW_KNOWN_AS = 200; // is now known as 
     public final static short MS_WAS_BANNED = 201; // was banned 
     public final static short MS_WAS_KICKED = 202; // was kicked 
     public final static short MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY = 203; // has been kicked because room became members-only
     public final static short MS_HAS_LEFT_CHANNEL = 204; // has left the channel
     public final static short MS_HAS_JOINED_THE_CHANNEL_AS = 205; // has joined the channel as 
     public final static short MS_AND = 206; // and 
     public final static short MS_IS_NOW = 207; // is now 
     public final static short MS_ERROR = 208; //error
     public final static short MS_SELECT_HISTORY_FOLDER = 209; //Select history folder
     public final static short MS_SOUND_VOLUME = 210; //Sound volume
     public final static short MS_LANGUAGE = 211; //Language
     public final static short MS_SAVE_HISTORY = 212; //Save history
     public final static short MS_SAVE_PRESENCES = 213; //Save presences
     public final static short MS_SAVE_HISTORY_CONF = 214; //Save conference history
     public final static short MS_SAVE_PRESENCES_CONF = 215; //Save conference presences
     public final static short MS_1251_CORRECTION = 216; //convert to cp1251
     public final static short MS_HISTORY_FOLDER = 217; //History folder
     public final static short MS_COPY = 218; //Copy
     public final static short MS_PASTE = 219; //Paste
     public final static short MS_SAVE_TEMPLATE = 220; //Save template
     public final static short MS_TEMPLATE = 221; //Template
     public final static short MS_HAS_SET_TOPIC_TO = 222; //has set topic to
     public final static short MS_SEEN = 223; //Seen
     public final static short MS_IDLE = 224; //Idle
     public final static short MS_MAIN_MENU = 225; //Main menu
     public final static short MS_CHOOSE_STATUS = 226; //Choose status
     public final static short MS_ADD_STATUS = 227; //Add status
     public final static short MS_REMOVE_STATUS = 228; //Remove status
     public final static short MS_ADRESS = 229; //Address
     public final static short MS_EXPORT_TO_FILE = 230; //Make BackUp
     public final static short MS_IMPORT_TO_FILE = 231; //Import from BackUp
     public final static short MS_VIEW = 232; //View
     public final static short MS_STOP = 233; //Stop
     public final static short MS_FILE_TRANSFERS = 234; //File transfers
     public final static short MS_PATH = 235; //Path
     public final static short MS_ACCEPT_FILE = 236; //Accept file
     public final static short MS_FILE = 237; //File
     public final static short MS_SAVE_TO = 238; //Save To
     public final static short MS_SENDER = 239; //Sender
     public final static short MS_REJECTED = 240; //Rejected
     public final static short MS_SEND_FILE = 241; //Send file
     public final static short MS_CANT_OPEN_FILE = 242; //Can't open file
     public final static short MS_NEW = 243; //New
     public final static short MS_NEW_TEMPLATE = 244; //New Template
     public final static short MS_SAVE_PHOTO = 245; //Save photo
     public final static short MS_BALLOON_INK = 246; //balloon ink
     public final static short MS_BALLOON_BGND = 247; //balloon background
     public final static short MS_LIST_BGND = 248; //messagelist & roster background
     public final static short MS_LIST_BGND_EVEN = 249; //messagelist & roster even lines
     public final static short MS_LIST_INK = 250; //messagelist & roster & common font
     public final static short MS_MSG_SUBJ = 251; //message subject
     public final static short MS_MSG_HIGHLIGHT = 252; //message highlight
     public final static short MS_DISCO_CMD = 253; //service discovery commands
     public final static short MS_BAR_BGND = 254; //panels background
     public final static short MS_BAR_INK = 255; //header font
     public final static short MS_CONTACT_DEFAULT = 256; //contact default
     public final static short MS_CONTACT_CHAT = 257; //contact chat
     public final static short MS_CONTACT_AWAY = 258; //contact away
     public final static short MS_CONTACT_XA = 259; //contact extended away
     public final static short MS_CONTACT_DND = 260; //contact do not disturb
     public final static short MS_GROUP_INK = 261; //group color
     public final static short MS_BLK_INK = 262; //keylock font
     public final static short MS_BLK_BGND = 263; //keylock background
     public final static short MS_MESSAGE_IN = 264; //message incoming
     public final static short MS_MESSAGE_OUT = 265; //message outgoing
     public final static short MS_MESSAGE_PRESENCE = 266; //message presence
     public final static short MS_MESSAGE_AUTH = 267; //message auth
     public final static short MS_MESSAGE_HISTORY = 268; //message history
     public final static short MS_PGS_REMAINED = 269; //progress bar remained
     public final static short MS_PGS_COMPLETE = 270; //progress bar complete
     public final static short MS_PGS_INK = 271; //progress bar font
     public final static short MS_HEAP_TOTAL = 272; //Heap mon total
     public final static short MS_HEAP_FREE = 273; //Heap mon free
     public final static short MS_CURSOR_BGND = 274; //Cursor background
     public final static short MS_CURSOR_OUTLINE = 275; //Cursor ink & outline
     public final static short MS_SCROLL_BRD = 276; //Scroll border
     public final static short MS_SCROLL_BAR = 277; //Scroll bar
     public final static short MS_SCROLL_BGND = 278; //Scroll back
     public final static short MS_MESSAGE_IN_S = 279; //other message incoming
     public final static short MS_MESSAGE_OUT_S = 280; //other message outgoing
     public final static short MS_MESSAGE_PRESENCE_S = 281; //other message presence
     public final static short MS_POPUP_MESSAGE = 282; //Popup font
     public final static short MS_POPUP_MESSAGE_BGND = 283; //Popup background
     public final static short MS_POPUP_SYSTEM = 284; //Popup system font
     public final static short MS_POPUP_SYSTEM_BGND = 285; //Popup system background
     public final static short MS_CONTACT_STATUS = 286; //Contact status font
     public final static short MS_CONTROL_ITEM = 287; //Control color
     public final static short MS_GRADIENT_BGND_LEFT = 288; //Gradient_Background_left color
     public final static short MS_GRADIENT_BGND_RIGHT = 289; //Gradient_Background_right color
     public final static short MS_COLOR_TUNE = 290; //Themes
     public final static short MS_SOUNDS_OPTIONS = 291; //Sounds options
     public final static short MS_TIME = 292; //Time
     public final static short MS_ROLE_PARTICIPANT = 293; //participant
     public final static short MS_ROLE_MODERATOR = 294; //moderator
     public final static short MS_ROLE_VISITOR = 295; //visitor
     public final static short MS_AFFILIATION_NONE = 296; //none
     public final static short MS_SUBSCR_NONE  = 297; //none
     public final static short MS_AFFILIATION_MEMBER = 298; //member
     public final static short MS_AFFILIATION_ADMIN = 299; //admin
     public final static short MS_AFFILIATION_OWNER = 300; //owner
     public final static short MS_SEC3 = 301; //second's
     public final static short MS_SEC2 = 302; //seconds
     public final static short MS_SEC1 = 303; //second
     public final static short MS_MIN3 = 304; //minute's
     public final static short MS_MIN2 = 305; //minutes
     public final static short MS_MIN1 = 306; //minute
     public final static short MS_HOUR3 = 307; //hour's
     public final static short MS_HOUR2 = 308; //hours
     public final static short MS_HOUR1 = 309; //hour
     public final static short MS_DAY3 = 310; //day's
     public final static short MS_DAY2 = 311; //days
     public final static short MS_DAY1 = 312; //day
     public final static short MS_AWAY_PERIOD  = 313; //Minutes before away
     public final static short MS_AWAY_TYPE  = 314; //Automatic Away
     public final static short MS_AWAY_OFF  = 315; //disabled
     public final static short MS_DISABLED = 316; //disabled
     public final static short MS_AWAY_LOCK  = 317; //keyblock
     public final static short MS_MESSAGE_LOCK  = 318; //by message
     public final static short MS_AUTOSTATUS = 319; //AutoStatus
     public final static short MS_AUTOSTATUS_TIME = 320; //AutoStatus time (min)
     public final static short MS_AUTO_XA = 321; //Auto xa since %t
     public final static short MS_AUTO_AWAY = 322; //Auto away since %t
     public final static short MS_AUTOFOCUS  = 323; //autofocus
     public final static short MS_GRANT_MEMBERSHIP  = 324; //Grant Membership
     public final static short MS_SURE_CLEAR  = 325; //Are You sure want to clear messagelist?
     public final static short MS_TOKEN  = 326; //Google token request
     public final static short MS_FEATURES  = 327; //Features
     public final static short MS_SHOWPWD  = 328; //Show password
     public final static short MS_NO_VERSION_AVAILABLE  = 329; //No client version available
     public final static short MS_MSG_LIMIT  = 330; //Message limit
     public final static short MS_OPENING_STREAM  = 331; //Opening stream
     public final static short MS_ZLIB  = 332; //Using compression
     public final static short MS_AUTH  = 333; //Authenticating
     public final static short MS_RESOURCE_BINDING  = 334; //Resource binding
     public final static short MS_SESSION  = 335; //Initiating session
     public final static short MS_TEXTWRAP  = 336; //Text wrapping
     public final static short MS_TEXTWRAP_CHARACTER  = 337; //by chars
     public final static short MS_TEXTWRAP_WORD  = 338; //by words
     public final static short MS_INFO  = 339; //Info
     public final static short MS_REPLY  = 340; //Reply
     public final static short MS_DIRECT_PRESENCE  = 341; //Send status
     public final static short MS_CONFIRM_BAN  = 342; //Are you sure want to BAN this person?
     public final static short MS_NO_REASON  = 343; //No reason
     public final static short MS_RECENT  = 344; //Recent
     public final static short MS_CAMERASHOT  = 345; //Shot
     public final static short MS_SELECT_FILE  = 346; //Select file
     public final static short MS_LOAD_PHOTO  = 347; //Load Photo
     public final static short MS_CLEAR_PHOTO  = 348; //Clear Photo
     public final static short MS_CAMERA  = 349; //Camera
     public final static short MS_HIDE_FINISHED  = 350; //Hide finished
     public final static short MS_TRANSFERS  = 351; //Transfer tasks
     public final static short MS_SURE_DELETE  = 352; //Are you sure want to delete this message?
     public final static short MS_NEW_BOOKMARK  = 353; //New conference
     public final static short MS_ROOT  = 354; //Root
     public final static short MS_DECLINE  = 355; //Decline
     public final static short MS_AUTH_NEW  = 356; //Authorize new contacts
     public final static short MS_AUTH_AUTO  = 357; //[auto-subscribe]
     public final static short MS_KEEPALIVE  = 358; //Keep-Alive
     public final static short MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM  = 359; // has been unaffiliated and kicked from members-only room
     public final static short MS_RENAME  = 360; //Rename
     public final static short MS_MOVE  = 361; //Move
     public final static short MS_SAVE  = 362; //Save
     public final static short MS_TRANSLIT  = 363; //Translit
     public final static short MS_DETRANSLIT  = 364; //ReTranslit
     public final static short MS_AUTODETRANSLIT  = 365; //Auto translit2Cyr
     public final static short MS_CHECK_UPDATE  = 366; //Check Updates
     public final static short MS_SHOW_RESOURCES  = 367; //Show Resources
     public final static short MS_COLLAPSED_GROUPS  = 368; //Collapsed groups
     public final static short MS_SEND_BUFFER  = 369; //Send Buffer
     public final static short MS_CHANGE_NICKNAME  = 370; //Change nickname
     public final static short MS_MESSAGE_COLLAPSE_LIMIT  = 371; //Message collapse limit
     public final static short MS_NO_CLIENT_INFO  = 372; //No client info
     public final static short MS_CLEAN_ALL_MESSAGES  = 373; //Clear all chats
     public final static short MS_DO_AUTOJOIN  = 374; //Join marked (auto)
     public final static short MS_STATS  = 375; //Statistics
     public final static short MS_STARTED = 376; //Started: 
     public final static short MS_TRAFFIC_STATS = 377; //Traffic stats: 
     public final static short MS_ALL = 378; //All: 
     public final static short MS_CURRENT = 379; //Current: 
     public final static short MS_LAST_MESSAGES  = 380; //Last Messages
     public final static short MS_EDIT_JOIN  = 381; //Edit/join
     public final static short MS_USE_COLOR_SCHEME  = 382; //Use this Color scheme
     public final static short MS_DELETE_ALL  = 383; //Delete All
     public final static short MS_HISTORY_OPTIONS  = 384; //History options
     public final static short MS_1251_TRANSLITERATE_FILENAMES  = 385; //Filenames transliterate
     public final static short MS_SAVE_CHAT  = 386; //Save chat
     public final static short MS_SHOW_STATUSES  = 387; //show statuses
     public final static short MS_SHOW_HARDWARE  = 388; //shared platform info
     public final static short MS_DELIVERY  = 389; //delivery events
     public final static short MS_NIL_DROP_MP  = 390; //drop all
     public final static short MS_NIL_DROP_P  = 391; //receive messages
     public final static short MS_NIL_ALLOW_ALL  = 392; //messages & presences
     public final static short MS_FONTSIZE_NORMAL  = 393; //normal
     public final static short MS_FONTSIZE_SMALL  = 394; //small
     public final static short MS_FONTSIZE_LARGE  = 395; //large
     public final static short MS_ALERT_PROFILE_ALLSIGNALS  = 396; //All signals
     public final static short MS_ALERT_PROFILE_VIBRA  = 397; //Vibra
     public final static short MS_ALERT_PROFILE_NOSIGNALS  = 398; //No signals
     public final static short MS_IS_DEFAULT  = 399; // (default)
     public final static short MS_QUIT_ASK  = 400; //Quit?
     public final static short MS_SURE_QUIT  = 401; //Are you sure want to Quit?
     public final static short MS_CONFIRM_EXIT  = 402; //exit confirmation
     public final static short MS_SHOW_LAST_APPEARED_CONTACTS  = 403; //show last appeared contacts
     public final static short MS_CUSTOM_KEYS  = 404; //custom keys
     public final static short MS_ADD_CUSTOM_KEY  = 405; //Add custom key
     public final static short MS_KEYS_ACTION  = 406; //keys action
     public final static short MS_ENABLED  = 407; //enabled
     public final static short MS_KEY  = 408; //key
     public final static short MS_RECONNECT  = 409; //Reconnect
     public final static short MS_SORT  = 410; //Sort list
     public final static short MS_FLASHLIGHT  = 411; //Turn on light
     public final static short MS_SHOW_TIME_TRAFFIC = 412; //show time and traffic
     public final static short MS_CLEAR_POPUPS  = 413; //Clear popups
     public final static short MS_MESSAGE_COUNT_LIMIT  = 414; //Chat history length
     public final static short MS_SUBSCRIPTION_REQUEST_FROM_USER  = 415; //This user wants to subscribe to your presence
     public final static short MS_SUBSCRIPTION_RECEIVED  = 416; //You are now authorized
     public final static short MS_SUBSCRIPTION_DELETED  = 417; //Your authorization has been removed!
     public final static short MS_SEND_FILE_TO  = 418; //To: 
     public final static short MS_FILE_SIZE  = 419; //size:
     public final static short MS_SUN  = 420; //Sun
     public final static short MS_MON  = 421; //Mon
     public final static short MS_TUE  = 422; //Tue
     public final static short MS_WED  = 423; //Wed
     public final static short MS_THU  = 424; //Thu
     public final static short MS_FRI  = 425; //Fri
     public final static short MS_SAT  = 426; //Sat
     public final static short MS_SUBSCR_AUTO = 427; //Automatic subscription
     public final static short MS_SUBSCR_ASK = 428; //Ask me
     public final static short MS_SUBSCR_DROP = 429; //Drop subscription
     public final static short MS_SUBSCR_REJECT = 430; //Deny subscription
     public final static short MS_SEARCH = 431; //Search
     public final static short MS_REGISTER = 432; //Register
     public final static short MS_CHECK_GOOGLE_MAIL = 433; //Check Google mail
     public final static short MS_BLINKING = 434; //Blink
     public final static short MS_NOTICES_OPTIONS = 435; //Notices options
     public final static short MS_MESSAGE_SOUND = 436; //Message sound
     public final static short MS_ONLINE_SOUND = 437; //Online sound
     public final static short MS_OFFLINE_SOUND = 438; //Offline sound
     public final static short MS_MESSAGE_FOR_ME_SOUND = 439; //"Message for me" sound
     public final static short MS_COMPOSING_SOUND = 440; //Composing sound
     public final static short MS_CONFERENCE_SOUND = 441; //Conference sound
     public final static short MS_STARTUP_SOUND = 442; //StartUP sound
     public final static short MS_OUTGOING_SOUND = 443; //Outgoing sound
     public final static short MS_VIP_SOUND = 444; //Vip sound
     public final static short MS_COPY_JID = 445; //Copy JID
     public final static short MS_PING = 446; //Ping request
     public final static short MS_ONLINE_TIME = 447; //Online time
     public final static short MS_BREAK_CONECTION = 448; //Break connection
     public final static short MS_AUTOSCROLL = 449; //autoScroll
     public final static short MS_EMULATE_TABS = 450; //emulate tabs
     public final static short MS_SHOW_BALLONS = 451; //show balloons
     public final static short MS_POPUPS = 452; //popUps
     public final static short MS_AUTOSTATUS_MESSAGE = 453; //AutoStatus Message
     public final static short MS_MEMORY = 454; //Memory:
     public final static short MS_FREE  = 455; //Free: 
     public final static short MS_TOTAL = 456; //Total: 
     public final static short MS_CONN = 457; //Session(s): 
     public final static short MS_DESCRIPTION = 458; //Description
     public final static short MS_USER  = 459; //User
     public final static short MS_IMPORT_EXPORT = 460; //Import/Export
     public final static short MS_ieStr  = 461; //Import/Export
     public final static short MS_BOLD_FONT = 462; //bold font for contacts
     public final static short MS_RUNNING_MESSAGE = 463; //running message
     public final static short MS_NOTIFY_MSGEDIT = 464; //Show latest message in alt message edit
     public final static short MS_MSG_EDIT_TYPE = 465; //Message Edit Type
     public final static short MS_MES_EDIT_OLD = 466; //Standart
     public final static short MS_MES_EDIT_ALT = 467; //Alternative
     public final static short MS_MES_EDIT_ALT_GUI = 468; //Alternative with GUI panels
     public final static short MS_COMPOSING_NOTIFY = 469; //Composing message to you
     public final static short MS_COMPRESSION  = 470; //compression
     public final static short MS_NEW_ROOM_CREATED  = 471; //New room created
     public final static short MS_IRCLIKESTATUS  = 472; //IRC-like conference nick-status
     public final static short MS_SEND_RECEIVE_USERMOODS = 473; //user moods send/receive
     public final static short MS_SIMULATED_BREAK = 474; //Simulated break
     public final static short MS_BUILD_NEW = 475; //Build new version on constructor
     public final static short MS_AUTOTASKS = 476; //Auto tasks
     public final static short MS_AUTOTASK_TYPE = 477; //Auto task type
     public final static short MS_BY_TIMER_ = 478; //by timer 
     public final static short MS_BY_TIME_ = 479; //by time 
     public final static short MS_AUTOTASK_ACTION_TYPE = 480; //Auto task action type
     public final static short MS_AUTOTASK_QUIT_BOMBUSMOD = 481; //Quit BombusQD
     public final static short MS_AUTOTASK_QUIT_CONFERENCES = 482; //Quit conferences
     public final static short MS_AUTOTASK_LOGOFF = 483; //Disconnection
     public final static short MS_AWAY_OFF_  = 484; //disabled
     public final static short MS_DISABLED_ = 485; //disabled
     public final static short MS_AUTOTASK_DELAY = 486; //Delay (min.)
     public final static short MS_AUTOTASK_TIME = 487; //Start time
     public final static short MS_AUTOTASK_HOUR = 488; //Hour
     public final static short MS_AUTOTASK_MIN = 489; //Min
     public final static short MS_LOAD_ROOMLIST  = 490; //Browse rooms
     public final static short MS_AUTORESPOND  = 491; //Autorespond
     public final static short MS_INVERT  = 492; //Invert colors
     public final static short MS_XML_CONSOLE  = 493; //XML console
     public final static short MS_CLIPBOARD  = 494; //Clipboard
     public final static short MS_USERMOOD = 495; //User moods
     public final static short MS_USERACTIVITY = 496; //User activity
     public final static short MS_PEP_NOT_SUPPORTED  = 497; //Personal events not supported
     public final static short MS_USERTUNE = 498; //User tune
     public final static short MS_BAR_FONT = 499; //Bar font
     public final static short MS_POPUP_FONT = 500; //Popup & ballon font
     public final static short MS_FONTS_OPTIONS = 501; //Fonts options
     public final static short MS_LOAD_HISTORY = 502; //Load history
     public final static short MS_CONNECT_TO = 503; //Connect to
     public final static short MS_SEND_COLOR_SCHEME = 504; //Send my color scheme
     public final static short MS_UNREAD_MESSAGES = 505; //Unread messages
     public final static short MS_VIBRATE_ONLY_HIGHLITED = 506; //vibrate only highlited
     public final static short MS_EXTENDED_SETTINGS = 507; //Extended settings
     public final static short MS_SAVE_TO_FILE = 508; //Save to file
     public final static short MS_LOAD_FROM_FILE = 509; //Load from file
     public final static short MS_SHOW_IQ_REQUESTS = 510; //Show IQ requests
     public final static short MS_SHOW_CLIENTS_ICONS = 511; //Show clients icons
     public final static short MS_PRIVACY_ACTION_  = 512; //Action
     public final static short MS_ACTION_ = 513; //Action
     public final static short MS_VALUE = 514; //Value
     public final static short MS_RECONNECT_COUNT_RETRY = 515; //Quantity of attempts
     public final static short MS_RECONNECT_WAIT = 516; //Delay before reconnect(sec.)
     public final static short MS_MENU = 517; //Menu
     public final static short MS_PREVIOUS = 518; //Previous
     public final static short MS_PREVIOUS_ = 519; //Previous: 
     public final static short MS_END_OF_VCARD = 520; //[End of vCard]
     public final static short MS_NO_VCARD = 521; //[No vCard available]
     public final static short MS_NO_PHOTO = 522; //[No photo available]
     public final static short MS_UNSUPPORTED_FORMAT = 523; //[Unsupported format]
     public final static short MS_PHOTO_TOO_LARGE = 524; //[large photo was dropped]
     public final static short MS_DELETE_GROUP_ASK = 525; //Delete group?
     public final static short MS_PANELS = 526; //Panels
     public final static short MS_NO_BAR = 527; //[ ]
     public final static short MS_MAIN_BAR = 528; //[main bar]
     public final static short MS_INFO_BAR = 529; //[info bar]
     public final static short MS_FLASH_BACKLIGHT = 530; //Flash backlight
     public final static short MS_EXECUTE_MENU_BY_NUMKEY = 531; //execute menu by numkey
     public final static short MS_SHOW_NACKNAMES = 532; //show nicknames
     public final static short MS_SEND_PHOTO = 533; //Send photo
     public final static short MS_ENABLE_AUTORESPOND = 534; //enable autorespond
     public final static short MS_FILE_MANAGER = 535; //File manager
     public final static short MS_ADHOC = 536; //remote control
     public final static short MS_SAVING_CURSORE  = 537; //Saving cursor
     public final static short MS_TRANSLATE  = 538; //Translate
     public final static short MS_DESTROY_ROOM  = 539; //Destroy room
     public final static short MS_STATICSTICS  = 540; //Stats users for
     public final static short MS_CONTACT_HELPER  = 541; //Contact Helper
     public final static short MS_CHANGE_PASSWORD  = 542; //Change password
     public final static short MS_REMOVE_ACCOUNT  = 543; //Remove from server
     public final static short MS_NEW_PASSWORD  = 544; //New password
     public final static short MS_SEND_EVIL_MSG  = 545; //Send Evil Message
     public final static short MS_ADD_SEL_TO_ARCHIVE  = 546; //save selection to archive
     public final static short MS_COPY_CLIPBOARD  = 547; //To Clipboard
     public final static short MS_COLLAPS_ALL  = 548; //Collapse All
     public final static short MS_ADD_SEARCH_QUERY  = 549; //Add Search Query
     public final static short MS_FIND_TEXT  = 550; //Find Text
     public final static short MS_END_SEARCH  = 551; //End search!
     public final static short MS_MOOD_IN_MSG  = 552; //Send Mood in Msg
     public final static short MS_ANIMATION  = 553; //Turn OFF Animation
     public final static short MS_ICON_COLP  = 554; //Turn OFF Message Icon
     public final static short MS_PEP_OFF  = 555; //PEP is now OFF
     public final static short MS_DYNAMIC_EVENTS  = 556; //Dynamic PEP
     public final static short MS_DRAW_CONTACT_VCARD  = 557; //Draw vcard image on contact
     public final static short MS_AUTOLOAD_VCARD  = 558; //Autoloading vcard from server
     public final static short MS_AUTOLOAD_VCARD_FROMFS  = 559; //Autoloading vcard from FS Phone
     public final static short MS_ACTIVITY  = 560; //MyActivity
     public final static short MS_UPDATE_PEP  = 561; //Update Moods+Actions+Music
     public final static short MS_STOP_MUSIC  = 562; //Stop Music
     public final static short MS_NOT_FOUND  = 563; //Your text not found!
     public final static short MS_UP_MOODS  = 564; //Send my Moods+Actions(PEP) to all contacts(YES-recommended)?
     public final static short MS_DRAWED_FONT  = 565; //Drawed font
     public final static short MS_REQUIRED_FIELDS  = 566; //Required field
     public final static short MS_GENERATE  = 567; //Generate
     public final static short MS_CONTACT_HELPER_  = 568; //Contact Helper
     public final static short MS_FILL_VCARD_FIELDS  = 569; //Fill Required field(Full name,nickname and birthday)
     public final static short MS_TRANSLATE_  = 570; //Translate
     public final static short MS_DELETE_ALL_STATUSES  = 571; //Delete all statuses
     public final static short MS_BOLD_AND_COLORS_NICKS  = 572; //Colored Bold String in chat(+many others strings)
     public final static short MS_SAVE_CURSOR  = 573; //Autosave cursor position in chat
     public final static short MS_MSG_BY_ME = 574; //Messages,sended by me
     public final static short MS_MSG_ME = 575; //Messages with /me
     public final static short MS_MSG_GET = 576; //All incoming messages
     public final static short MS_BGND_IMAGE = 577; //Background Jimm image
     public final static short MS_MY_BGND_IMAGE = 578; //Background image
     public final static short MS_BGND_SCROLL = 579; //Draw Bgnd of Scroll
     public final static short MS_SCROLL_OPTIONS = 580; //Scroll Options
     public final static short MS_SCROLL_WIDTH = 581; //Scroll Width
     public final static short MS_TYPE_BACKGROUND = 582; //Type of Background
     public final static short MS_BGND_NONE = 583; //Background Default
     public final static short MS_BGND_GRADIENT_ = 584; //Background Gradient
     public final static short MS_MY_BGND_IMAGE_ = 585; //Background image
     public final static short MS_MAX_AVATAR_WIDTH = 586; //Max Avatar Width
     public final static short MS_MAX_AVATAR_HEIGHT = 587; //Max Avatar Height
     public final static short MS_CLIENT_ICONS_LEFT = 588; //ClientIcons Left
     public final static short MS_RESTART_APP = 589; //After Restart App
     public final static short MS_LOAD_SJIN = 590; //Load skin.png from File
     public final static short MS_SET_DEF_SKIN = 591; //Set Default skin.png
     public final static short MS_SKIN_SUPPORT_ERROR = 592; //Error!Only 16x16 size support!
     public final static short MS_CLCHAT_ON  = 593; //Classic Chat
     public final static short MS_clchatStr  = 594; //Classic Chat
     public final static short MS_CLCHAT_AUTO_SCR  = 595; //Auto Scrolling
     public final static short MS_CLCHAT_BGNG_PHONE  = 596; //Background Phone theme
     public final static short MS_CLCHAT_AOPEN  = 597; //Auto Open Classic Chat
     public final static short MS_CLCHAT_HEIGHT  = 598; //Chat Height(max-320)
     public final static short MS_CLCHAT_SCRLSPEED  = 599; //Scroll speed(msec,10sec-max)
     public final static short MS_CLCHAT_MSGLIMIT  = 600; //Show messages limit(1000-max)
     public final static short MS_USERS_SEARCH = 601; //Users search
     public final static short MS_SUPPORT  = 602; //Support
     public final static short MS_GRADIENT_CURSOR  = 603; //Gradient cursor
     public final static short MS_ALTERNATING_CHAT_COLORS  = 604; //Alternating chat colors
     public final static short MS_TRANSPARENCY_ARGB  = 605; //Transparency bgnd(argb)
     public final static short MS_GRAPHICS_MENU_BGNG_ARGB  = 606; //Graphics menu bgnd(argb)
     public final static short MS_GRAPHICS_MENU_FONT  = 607; //Graphics menu font
     public final static short MS_SERVICE  = 608; //My Services
     public final static short MS_MY_JABBER  = 609; //My Jabber>
     public final static short AVATAR_HIGH_TRAFFIC  = 610; //(HIGH TRAFFIC!)
     public final static short AVATAR_DRAW_RECT  = 611; //Draw Rect Avatar
     public final static short AVATAR_AUTOSAVE_FS  = 612; //AutoSave In FS
     public final static short AVATAR_FOLDER  = 613; //Avatar Folder
     public final static short AVATAR_ROSTER_LOADFS  = 614; //Roster:Load all Avatars from FS
     public final static short AVATAR_MUC_LOADFS  = 615; //Muc:Load all Avatars from FS
     public final static short MS_AVATARS  = 616; //Avatars
     public final static short MS_avatarStr  = 617; //Avatars
     public final static short MS_QD_NEWS  = 618; //News
     public final static short MS_SHOW_PRS_TIME = 619; //Show Presence Time
     public final static short MS_ANIMATION_MENU_ROSTER  = 620; //Animation of Graphics Menu&Roster
     public final static short MS_EDIT_COLORS  = 621; //Edit colors
     public final static short MS_TRANSPARENT  = 622; //Transparent
     public final static short MS_BGND_MIDLET  = 623; //Background of midlet
     public final static short MS_GR_MENU  = 624; //Graphics Menu
     public final static short MS_CURSOR_TR  = 625; //Cursor
     public final static short MS_Bold  = 626; //Bold
     public final static short MS_Italic  = 627; //Italic
     public final static short GAME_CHECKERS_MSG  = 628; //User wants to play checkers with you
     public final static short GAME_SEND_GAME  = 629; //Checkers:send invite
     public final static short GAME_APPLIED  = 630; //Checkers:start game
     public final static short GAME_STOP  = 631; //Checkers:stop game
     public final static short GAME_RESUME  = 632; //Checkers:resume
     public final static short DONT_LOAD_MUCCONTACTS  = 633; //Don't load conference contacts
     public final static short MS_MODULES_CONFIG  = 634; //Modules config
     public final static short MS_ADVANCED_OPT  = 635; //Advanced Options
     public final static short MS_contactStr  = 636; //Contacts
     public final static short MS_msgStr  = 637; //Chat
     public final static short MS_netStr  = 638; //Network
     public final static short MS_grStr  = 639; //Graphics
     public final static short MS_APPLICATION_  = 640; //Application
     public final static short MS_appStr_  = 641; //Application
     public final static short MS_hotkeysStr  = 642; //Hotkeys
     public final static short MS_astatusStr  = 643; //Autostatus
     public final static short MS_CLCHAT_ON_  = 644; //Classic Chat
     public final static short MS_clchatStr_  = 645; //Classic Chat
     public final static short MS_cthemesStr  = 646; //Color Themes
     public final static short MS_casheStr  = 647; //Cashe
     public final static short MS_historyStr  = 648; //History
     public final static short MS_fontsStr  = 649; //Fonts
     public final static short MS_IMPORT_EXPORT_ = 650; //Import/Export
     public final static short MS_ieStr_  = 651; //Import/Export
     public final static short MS_notifyStr  = 652; //Notifications&Light Control
     public final static short MS_taskstr  = 653; //Tasks
     public final static short MS_AVATARS_  = 654; //Avatars
     public final static short MS_avatarStr_  = 655; //Avatars
     public final static short MS_config  = 656; //Config
     public final static short MS_ANI_SMILES  = 657; //Use animated smiles
     public final static short MS_NOKIA_RECONNECT_HACK  = 658; //Nokia Reconnect Hack
     public final static short MS_DEBUG_MENU  = 659; //Debug Menu
     public final static short MS_EDIT_ACCOUNT_MSG  = 660; //Please,edit this account to save new password!
     public final static short MS_ACCOUNT_DELETED  = 661; //Account deleted!
     public final static short MS_INSERT_NEW_PASSWORD  = 662; //Insert New Password
     public final static short MS_SELECT_OUT_MESSAGES  = 663; //Highlite Out Messages
     public final static short MS_ALERT_CONTACT_OFFLINE  = 664; //Contact is now offline.Continue?
     public final static short MS_CLIPBOARD_SENDERROR  = 665; //clipboard NOT sended
     public final static short MS_COLLAPSE_PRESENCE  = 666; //Collapse presences
     public final static short MS_PRIVACY_ANY  = 667; //ANY
     public final static short MS_PRIVACY_ALLOW  = 668; //allow
     public final static short MS_PRIVACY_DENY  = 669; //deny
     public final static short MS_PRIVACY_PRESENCE_IN  = 670; //presence-in
     public final static short MS_PRIVACY_PRESENCE_OUT  = 671; //presence-out
     public final static short MS_PRIVACY_IQ  = 672; //iq
     public final static short MS_PRIVACY_IF  = 673; //if
     public final static short MS_AFFILIATION_NONE_ = 674; //none
     public final static short MS_SUBSCR_NONE_  = 675; //none
     public final static short MS_SUBSCR_FROM  = 676; //from
     public final static short MS_SUBSCR_TO  = 677; //to
     public final static short MS_SUBSCR_BOTH  = 678; //both
     public final static short MS_PRIVACY_ALL_STANZAS  = 679; //all stanzas
     public final static short MS_CLOSE_ALL_ROOMS  = 680; //Exit from ALL Rooms
     public final static short MS_CREATE_ANNOTATION  = 681; //Create Annotation
     public final static short MS_REMOVE_ANNOTATION  = 682; //Remove Annotation
     public final static short MS_CONTACT_ANNOTATIONS  = 683; //Contact Annotations
     public final static short MS_ANNOTATION  = 684; //Note
     public final static short MS_CHANGE_TRANSPORT  = 685; //Change transport
     public final static short MS_TEST_VIBRATION  = 686; //Test Vibration
     public final static short MS_VIBRATION_LEN  = 687; //Vibration Len
     public final static short MS_VIBRATION_REPEAT  = 688; //Vibration Repeats
     public final static short MS_VIBRATION_INTERVAL  = 689; //Vibration Repeats Interval
     public final static short L_CONFIG  = 690; //Light control
     public final static short L_ENABLED  = 691; //Enable light control
     public final static short L_IDLE_VALUE  = 692; //Idle screen brightness
     public final static short L_KEYPRESS_VALUE  = 693; //Keypress screen brightness
     public final static short L_KEYPRESS_TIMEOUT  = 694; //Keypress idle timeout
     public final static short L_MESSAGE_VALUE  = 695; //Message screen brightness
     public final static short L_MESSAGE_TIMEOUT  = 696; //Message idle timeout
     public final static short MS_METACONTACTS  = 697; //Metacontacts
     public final static short MS_USE_FIVE_TO_CREATEMSG  = 698; //Use 5 key to create Message
     public final static short MS_USE_LIGHT_TO_DRWPANELS  = 699; //Use Light to draw panels
     public final static short MS_MAINBAR_GRADIENTLIGHT  = 700; //MainBar Gradient Light #
     public final static short MS_GRAPHICSMENU_POS  = 701; //Graphisc Menu Screen Position
     public final static short MS_GRMENU_CENTER  = 702; //[center]
     public final static short MS_GRMENU_RIGHT  = 703; //[    :Right]
     public final static short MS_MSGBUFFER_NOT_EMPTY  = 704; //Message Buffer is not empty.Clear it?
     public final static short MS_MULTI_MESSAGE  = 705; //Create Multi Message
     public final static short MS_SORT_TYPE  = 706; //Sort Type
     public final static short MS_SORT_TYPE_DEF  = 707; //OFF (default)
     public final static short MS_SORT_TYPE_STATUS  = 708; //By status
     public final static short MS_SORT_TYPE_MSGS  = 709; //By Msgs count
     public final static short MS_AUTOCONNECT_TRANSPORTS  = 710; //Auto-Load Transports after connect
     public final static short MS_HISTORY_TYPE  = 711; //Type History
     public final static short MS_HISTORY_RMS  = 712; //History in RMS
     public final static short MS_HISTORY_FS  = 713; //History in FS
     public final static short MS_HISTORY_SERVER  = 714; //History on server
     public final static short MS_DELETE_VCARD  = 715; //vCard: delete current
     public final static short MS_DELETE_ALL_VCARD  = 716; //vCard: delete ALL
     public final static short MS_DELETE_AVATAR_VCARD  = 717; //User Avatar: delete current
     public final static short MS_DELETE_ALL_AVATAR_VCARD  = 718; //User Avatar: delete from ALL vcards
     public final static short MS_SUCCESS  = 719; //Success
     public final static short MS_SIMPLE_CONTACTS_DRAW  = 720; //Simple Contacts

     public final static short MS_ENABLE_DISABLE = 721;
     public final static short MS_GRADIENT_CURSOR_1 =    722;
     public final static short MS_GRADIENT_CURSOR_2 =  723;
     public final static short MS_SLASHME =  724;
     public final static short MS_VIP_GROUP =  725;
     public final static short MS_VISIBLE_GROUP =   726;
     public final static short MS_BGND_GRADIENT =  727;
     public final static short MS_GRMENU_LEFT =  728;
     public final static short MS_PEP =  729;
     
     public final static short MS_USER_APP_LEVEL =  730;
     public final static short MS_USER_APP_LEVEL_0 =  731;
     public final static short MS_USER_APP_LEVEL_1 =  732;
     public final static short MS_USER_APP_LEVEL_2 =  733;
     public final static short MS_WAS_ADDED =  734;  
     public final static short MS_CONFIGURATION_MASTER =  735;
     public final static short MS_WARNING_MESSAGE_INSTALL =  736;
     
     public final static short MS_IM_NETWORKS = 737;
     public final static short MS_ADD_SERVER = 738;     
     public final static short MS_MY_SERVERS = 739;
     public final static short MS_MENU_FONT = 740;
     
     public final static short MS_HISTORY_SHOW = 741;
     public final static short MS_APPRUN_COUNT = 742;

     public final static short LA_ATTENTION = 743;
     public final static short LA_WAKEUP = 744;
     public final static short LA_ENABLE = 745;
     public final static short LA_REQUEST = 746;
     public final static short LA_SOUND = 747;

     public final static short MS_SHADOW_BAR =  748;
     

    private static String[] localeItemsDefault = new String[0];
    private static String[] localeItems = {
        "en",  "en",
        
        "online",
        "chat",
        "away",
        "xa",
        "invisible",
        "dnd",
        "offline",
        "Jid",
        "Privacy Lists",
        "Message font",
        "Roster font",
        "Paste Body",
        "Configure Room",
        "Paste Subject",
        "Service Discovery",
        "User JID",
        "New list",
        "Select (no login)",
        "Privacy rule",
        "use SSL",
        "Modify",
        "Update",
        "Account name",
        "GMT offset",
        "Time settings (hours)",
        "Connected",
        "Connect to ",
        "Alert Profile",
        "Move Up",
        "Owners",
        "Ok",
        "Minimize",
        "Room",
        "Messages",
        "Refresh",
        "Resolve Nicknames",
        "Action",
        "Action",
        "Ban",
        "Leave Room",
        "Password",
        "Actions",
        "Activate",
        "Affiliation",
        "Accounts",
        "Delete list",
        "Account >",
        "Select",
        "Subject",
        "Quit",
        "Edit list",
        "Registering",
        "Done",
        "Error: ",
        "Browse",
        "Browse",
        "Save list",
        "Keep-Alive period",
        "<New Group>",
        "Send",
        "Priority",
        "Failed",
        "Set Priority",
        "Delete rule",
        "Ignore-List",
        "Roster request",
        "Type",
        "Name",
        "User Profile",
        "fullscreen",
        "Add bookmark",
        "conferences only",
        "Client Version",
        "Discard Search",
        "Search Results",
        "Members",
        "Add Contact",
        "Subscription",
        "Status",
        "Status",
        "Join",
        "Startup actions",
        "Server",
        "Admins",
        "Make Ignore-List",
        "General Group",
        "Options",
        "Delete",
        "Delete contact?",
        "Authorize",
        "Nicknames",
        "to Archive",
        "Back",
        "heap monitor",
        "change softkeys",
        "Message",
        "<Other>",
        "Append",
        "Active Contacts",
        "Select nickname",
        "Group",
        "Join conference",
        "No",
        "Re-Enter Room",
        "New Message",
        "Add",
        "Logon",
        "Stanzas",
        "at Host",
        "join conferences",
        "Status",
        "Status",
        "Smiles",
        "Contact >",
        "offline contacts",
        "Transport",
        "composing events",
        "Add Smile",
        "Nickname",
        "Revoke Voice",
        "Not-in-list",
        "Commands",
        "- (Sign)",
        "Set default",
        "Outcasts (Ban)",
        "Set affiliation to",
        "Register Account",
        "autologin",
        "Logoff",
        "Publish",
        "Remove subscription",
        "Set",
        "Application",
        "Application",
        "Bookmarks",
        "Test sound",
        "Startup",
        "Edit rule",
        "Cancel",
        "Close",
        "Archive",
        "Conference",
        "Sound",
        "Login failed",
        "Browse",
        "Browse",
        "New Jid",
        "plain-text password",
        "Paste Nickname",
        "Kick",
        "Remove readed",
        "Grant Voice",
        "Move Down",
        "Quote",
        "Roster elements",
        "popup from background",
        "smiles",
        "About",
        "Resource",
        "Disconnected",
        "Edit",
        "Host name/IP",
        "Add rule",
        "for all status types",
        "Paste Jid",
        "Goto URL",
        "Clock offset",
        "Yes",
        "Suspend",
        "Alert Profile >",
        "My vCard",
        "transports",
        "Add Profile",
        "self-contact",
        "vCard",
        "Set Subject",
        "Port",
        "Resume Message",
        "Are You sure want to discard ",
        " from OWNER to ",
        "Modify affiliation",
        "Clear",
        "Connect",
        "Unaffiliate",
        "Grant Moderator",
        "Revoke Moderator",
        "Grant Admin",
        "Grant Ownership",
        "Visitors are not allowed to send messages to all occupants",
        " is inviting You to ",
        "Ask subscription",
        "Grant subscription",
        "Invite to conference",
        "Reason",
        "You have been invited to ",
        "Participants",
        "Abc",
        "room presences",
        " is now known as ",
        " was banned ",
        " was kicked ",
        " has been kicked because room became members-only",
        " has left the channel",
        " has joined the channel as ",
        " and ",
        " is now ",
        "error",
        "Select history folder",
        "Sound volume",
        "Language",
        "Save history",
        "Save presences",
        "Save conference history",
        "Save conference presences",
        "convert to cp1251",
        "History folder",
        "Copy",
        "Paste",
        "Save template",
        "Template",
        "has set topic to",
        "Seen",
        "Idle",
        "Main menu",
        "Choose status",
        "Add status",
        "Remove status",
        "Address",
        "Make BackUp",
        "Import from BackUp",
        "View",
        "Stop",
        "File transfers",
        "Path",
        "Accept file",
        "File",
        "Save To",
        "Sender",
        "Rejected",
        "Send file",
        "Can't open file",
        "New",
        "New Template",
        "Save photo",
        "balloon ink",
        "balloon background",
        "messagelist & roster background",
        "messagelist & roster even lines",
        "messagelist & roster & common font",
        "message subject",
        "message highlight",
        "service discovery commands",
        "panels background",
        "header font",
        "contact default",
        "contact chat",
        "contact away",
        "contact extended away",
        "contact do not disturb",
        "group color",
        "keylock font",
        "keylock background",
        "message incoming",
        "message outgoing",
        "message presence",
        "message auth",
        "message history",
        "progress bar remained",
        "progress bar complete",
        "progress bar font",
        "Heap mon total",
        "Heap mon free",
        "Cursor background",
        "Cursor ink & outline",
        "Scroll border",
        "Scroll bar",
        "Scroll back",
        "other message incoming",
        "other message outgoing",
        "other message presence",
        "Popup font",
        "Popup background",
        "Popup system font",
        "Popup system background",
        "Contact status font",
        "Control color",
        "Gradient_Background_left color",
        "Gradient_Background_right color",
        "Themes",
        "Sounds options",
        "Time",
        "participant",
        "moderator",
        "visitor",
        "none",
        "none",
        "member",
        "admin",
        "owner",
        "second's",
        "seconds",
        "second",
        "minute's",
        "minutes",
        "minute",
        "hour's",
        "hours",
        "hour",
        "day's",
        "days",
        "day",
        "Minutes before away",
        "Automatic Away",
        "disabled",
        "disabled",
        "keyblock",
        "by message",
        "AutoStatus",
        "AutoStatus time (min)",
        "Auto xa since %t",
        "Auto away since %t",
        "autofocus",
        "Grant Membership",
        "Are You sure want to clear messagelist?",
        "Google token request",
        "Features",
        "Show password",
        "No client version available",
        "Message limit",
        "Opening stream",
        "Using compression",
        "Authenticating",
        "Resource binding",
        "Initiating session",
        "Text wrapping",
        "by chars",
        "by words",
        "Info",
        "Reply",
        "Send status",
        "Are you sure want to BAN this person?",
        "No reason",
        "Recent",
        "Shot",
        "Select file",
        "Load Photo",
        "Clear Photo",
        "Camera",
        "Hide finished",
        "Transfer tasks",
        "Are you sure want to delete this message?",
        "New conference",
        "Root",
        "Decline",
        "Authorize new contacts",
        "[auto-subscribe]",
        "Keep-Alive",
        " has been unaffiliated and kicked from members-only room",
        "Rename",
        "Move",
        "Save",
        "Translit",
        "ReTranslit",
        "Auto translit2Cyr",
        "Check Updates",
        "Show Resources",
        "Collapsed groups",
        "Send Buffer",
        "Change nickname",
        "Message collapse limit",
        "No client info",
        "Clear all chats",
        "Join marked (auto)",
        "Statistics",
        "Started: ",
        "Traffic stats: ",
        "All: ",
        "Current: ",
        "Last Messages",
        "Edit/join",
        "Use this Color scheme",
        "Delete All",
        "History options",
        "Filenames transliterate",
        "Save chat",
        "show statuses",
        "shared platform info",
        "delivery events",
        "drop all",
        "receive messages",
        "messages & presences",
        "normal",
        "small",
        "large",
        "All signals",
        "Vibra",
        "No signals",
        " (default)",
        "Quit?",
        "Are you sure want to Quit?",
        "exit confirmation",
        "show last appeared contacts",
        "custom keys",
        "Add custom key",
        "keys action",
        "enabled",
        "key",
        "Reconnect",
        "Sort list",
        "Turn on light",
        "show time and traffic",
        "Clear popups",
        "Chat history length",
        "This user wants to subscribe to your presence",
        "You are now authorized",
        "Your authorization has been removed!",
        "To: ",
        "size:",
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
        "Automatic subscription",
        "Ask me",
        "Drop subscription",
        "Deny subscription",
        "Search",
        "Register",
        "Check Google mail",
        "Blink",
        "Notices options",
        "Message sound",
        "Online sound",
        "Offline sound",
        "\"Message for me\" sound",
        "Composing sound",
        "Conference sound",
        "StartUP sound",
        "Outgoing sound",
        "Vip sound",
        "Copy JID",
        "Ping request",
        "Online time",
        "Break connection",
        "autoScroll",
        "emulate tabs",
        "show balloons",
        "popUps",
        "AutoStatus Message",
        "Memory:",
        "Free: ",
        "Total: ",
        "Session(s): ",
        "Description",
        "User",
        "Import/Export",
        "Import/Export",
        "bold font for contacts",
        "running message",
        "Show latest message in alt message edit",
        "Message Edit Type",
        "Standart",
        "Alternative",
        "Alternative with GUI panels",
        "Composing message to you",
        "compression",
        "New room created",
        "IRC-like conference nick-status",
        "user moods send/receive",
        "Simulated break",
        "Build new version on constructor",
        "Auto tasks",
        "Auto task type",
        "by timer ",
        "by time ",
        "Auto task action type",
        "Quit BombusQD",
        "Quit conferences",
        "Disconnection",
        "disabled",
        "disabled",
        "Delay (min.)",
        "Start time",
        "Hour",
        "Min",
        "Browse rooms",
        "Autorespond",
        "Invert colors",
        "XML console",
        "Clipboard",
        "User moods",
        "User activity",
        "Personal events not supported",
        "User tune",
        "Bar font",
        "Popup & ballon font",
        "Fonts options",
        "Load history",
        "Connect to",
        "Send my color scheme",
        "Unread messages",
        "vibrate only highlited",
        "Extended settings",
        "Save to file",
        "Load from file",
        "Show IQ requests",
        "Show clients icons",
        "Action",
        "Action",
        "Value",
        "Quantity of attempts",
        "Delay before reconnect(sec.)",
        "Menu",
        "Previous",
        "Previous: ",
        "[End of vCard]",
        "[No vCard available]",
        "[No photo available]",
        "[Unsupported format]",
        "[large photo was dropped]",
        "Delete group?",
        "Panels",
        "[ ]",
        "[main bar]",
        "[info bar]",
        "Flash backlight",
        "execute menu by numkey",
        "show nicknames",
        "Send photo",
        "enable autorespond",
        "File manager",
        "remote control",
        "Saving cursor",
        "Translate",
        "Destroy room",
        "Stats users for",
        "Contact Helper",
        "Change password",
        "Remove from server",
        "New password",
        "Send Evil Message",
        "save selection to archive",
        "To Clipboard",
        "Collapse All",
        "Add Search Query",
        "Find Text",
        "End search!",
        "Send Mood in Msg",
        "Turn OFF Animation",
        "Turn OFF Message Icon",
        "PEP is now OFF",
        "Dynamic PEP",
        "Draw vcard image on contact",
        "Autoloading vcard from server",
        "Autoloading vcard from FS Phone",
        "MyActivity",
        "Update Moods+Actions+Music",
        "Stop Music",
        "Your text not found!",
        "Send my Moods+Actions(PEP) to all contacts(YES-recommended)?",
        "Drawed font",
        "Required field",
        "Generate",
        "Contact Helper",
        "Fill Required field(Full name,nickname and birthday)",
        "Translate",
        "Delete all statuses",
        "Colored Bold String in chat(+many others strings)",
        "Autosave cursor position in chat",
        "Messages,sended by me",
        "Messages with /me",
        "All incoming messages",
        "Background Jimm image",
        "Background image",
        "Draw Bgnd of Scroll",
        "Scroll Options",
        "Scroll Width",
        "Type of Background",
        "Background Default",
        "Background Gradient",
        "Background image",
        "Max Avatar Width",
        "Max Avatar Height",
        "ClientIcons Left",
        "After Restart App",
        "Load skin.png from File",
        "Set Default skin.png",
        "Error!Only 16x16 size support!",
        "Classic Chat",
        "Classic Chat",
        "Auto Scrolling",
        "Background Phone theme",
        "Auto Open Classic Chat",
        "Chat Height(max-320)",
        "Scroll speed(msec,10sec-max)",
        "Show messages limit(1000-max)",
        "Users search",
        "Support",
        "Gradient cursor",
        "Alternating chat colors",
        "Transparency bgnd(argb)",
        "Graphics menu bgnd(argb)",
        "Graphics menu font",
        "My Services",
        "My Jabber",
        "(HIGH TRAFFIC!)",
        "Draw Rect Avatar",
        "AutoSave In FS",
        "Avatar Folder",
        "Roster:Load all Avatars from FS",
        "Muc:Load all Avatars from FS",
        "Avatars",
        "Avatars",
        "News",
        "Show Presence Time",
        "Animation of Graphics Menu&Roster",
        "Edit colors",
        "Transparent",
        "Background of midlet",
        "Graphics Menu",
        "Cursor",
        "Bold",
        "Italic",
        "User wants to play checkers with you",
        "Checkers:send invite",
        "Checkers:start game",
        "Checkers:stop game",
        "Checkers:resume",
        "Don't load conference contacts",
        "Modules config",
        "Advanced Options",
        "Contacts",
        "Chat",
        "Network",
        "Interface",
        "Application",
        "Application",
        "Hotkeys",
        "Autostatus",
        "Classic Chat",
        "Classic Chat",
        "Color Themes",
        "Cashe",
        "History",
        "Fonts",
        "Import/Export",
        "Import/Export",
        "Notifications&Light Control",
        "Tasks",
        "Avatars",
        "Avatars",
        "Config",
        "Use animated smiles",
        "Nokia Reconnect Hack",
        "Debug Menu",
        "Please,edit this account to save new password!",
        "Account deleted!",
        "Insert New Password",
        "Highlite Out Messages",
        "Contact is now offline.Continue?",
        "clipboard NOT sended",
        "Collapse presences",
        "ANY",
        "allow",
        "deny",
        "presence-in",
        "presence-out",
        "iq",
        "if",
        "none",
        "none",
        "from",
        "to",
        "both",
        "all stanzas",
        "Exit from ALL Rooms",
        "Create Annotation",
        "Remove Annotation",
        "Contact Annotations",
        "Note",
        "Change transport",
        "Test Vibration",
        "Vibration Len",
        "Vibration Repeats",
        "Vibration Repeats Interval",
        "Light control",
        "Enable light control",
        "Idle screen brightness",
        "Keypress screen brightness",
        "Keypress idle timeout",
        "Message screen brightness",
        "Message idle timeout",
        "Metacontacts",
        "Use 5 key to create Message",
        "Use Light to draw panels",
        "MainBar Gradient Light #",
        "Graphisc Menu Screen Position",
        "[center]",
        "[    :Right]",
        "Message Buffer is not empty.Clear it?",
        "Create Multi Message",
        "Sort Type",
        "OFF (default)",
        "By status",
        "By Msgs count",
        "Auto-Load Transports after connect",
        "Type History",
        "History in RMS",
        "History in FS",
        "History on server",
        "vCard: delete current",
        "vCard: delete ALL",
        "User Avatar: delete current",
        "User Avatar: delete from ALL vcards",
        "Success",
        "Simple Contacts",
        
        "Enable/Disable",
        "Gradient cursor(1)",
        "Gradient cursor(2)",
        "/me",
        "VIP",
        "Visible",
        "MS_BGND_GRADIENT",
        "[Left:     ]",
        "Pubsub events",
        
        "User App Level",
        "Started",
        "Confident",
        "Experienced",
        "Was added:",
        "Configuration Master",
        "Please reinstall the program to avoid possible future problems",
        
        "IM Networks",
        "Add server",
        "My servers",
        "Menu font",
        
        "Show History",
        "AppRun count: ",

	"Attention!",
	"Wake Up!!!",
	"Enable attention requests",
	"Request attention",
	"Attention sound",

        "Shadow bar"
    };

    private SR() { }

    public final static String get(short id){
        return localeItems[id];
    }

    public static String getPresence(String presenceName) {
        if (presenceName.equals("online")) {
            return get(MS_ONLINE);
        } else if (presenceName.equals("chat")) {
            return get(MS_CHAT);
        } else if (presenceName.equals("away")) {
            return get(MS_AWAY);
        } else if (presenceName.equals("xa")) {
            return get(MS_XA);
        } else if (presenceName.equals("invisible")) {
            return get(MS_INVISIBLE);
        } else if (presenceName.equals("dnd")) {
            return get(MS_DND);
        } else if (presenceName.equals("unavailable")) {
            return get(MS_OFFLINE);
        }
        return null;
    }

    public final static short  MS_XMLLANG = 0;
    public final static short  MS_IFACELANG = 1;

    private static void resetLang() {
        int size = localeItems.length;
        if (localeItemsDefault.length == 0) {
            localeItemsDefault = new String[localeItems.length];
            for (int i = 0; i < size; ++i) {
                localeItemsDefault[i] = localeItems[i];//en==const
            }
        }

        size = localeItemsDefault.length;
        for (int i = 0; i < size; ++i) {
            localeItems[i] = localeItemsDefault[i];
        }
        localeItems[0] = "en";
        localeItems[1] = "en";
    }   
    
    public static void changeLocale() {
        String langFile = Config.getInstance().langFileName();
//#ifdef DEBUG
//#         System.out.print("   Loading locale -> " + langFile + "\n"); /* lang/ru.txt */
//#endif
        resetLang();
        if (langFile == null){ //en
           return;
        }
        int index = langFile.indexOf('.');
        String langName = Config.getInstance().lang;
        localeItems[0] = langName;
        localeItems[1] = langName;
        localeItems  = new StringLoader().arrayLoader(langFile, localeItems);
        Commands.initCommands();
    }
}
