/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**package org.bombusqd.scrobbler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import org.bombusqd.BombusQDService;

/**
 *
 * @author modi & Ivansuper
 */
/**public class Receiver extends BroadcastReceiver {
    private BombusQDService qdService;
    public Receiver(BombusQDService svc){
        qdService = svc;
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        String artist = arg1.getStringExtra("artist");
	String track = arg1.getStringExtra("track");
        /* Sony Ericsson player uses "ARTIST_NAME" and "TRACK_NAME" 
         * instead of "artist" and "track"
         * need fix for it */
/**	if(artist == null && track == null) return;
	if(artist == null) artist = "Unknown";
	if(track == null) track = "Unknown";
	String now_playing = artist+" - "+track;
        Toast.makeText(arg0, "Now playing: "+now_playing, Toast.LENGTH_SHORT).show();
    }
}*/