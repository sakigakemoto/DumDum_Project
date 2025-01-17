/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.eurecom.connectivity;

import fr.eurecom.allmenus.BaseMenu;
import fr.eurecom.allmenus.ClientMenu;
import fr.eurecom.allmenus.HostMenu;
import fr.eurecom.dumdumgame.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private Context activity;
    private BaseMenu bm;
    private DeviceListFragment dlf;

    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Context ct, BaseMenu bm) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = ct;
        if (bm instanceof ClientMenu) {
            this.bm = (ClientMenu) bm;
            this.dlf = ((ClientMenu)this.bm).getDLF();
        }
        else {
            this.bm = (HostMenu) bm;
            this.dlf = ((HostMenu)this.bm).getDLF();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                if (bm instanceof ClientMenu)
                    ((ClientMenu) bm).setIsWifiP2pEnabled(true);
                else
                    ((HostMenu) bm).setIsWifiP2pEnabled(true);
            } else {
                if (bm instanceof ClientMenu) {
                    ((ClientMenu) bm).setIsWifiP2pEnabled(false);
                    ((ClientMenu) bm).resetData();
                } else {
                    ((HostMenu) bm).setIsWifiP2pEnabled(false);
                    ((HostMenu) bm).resetData();
                }
            }
            //Log.d(activity.conn.TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                //if(bm instanceof ClientMenu)
                    manager.requestPeers(channel, dlf);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            
                if (manager == null) {
                    return;
                }
    
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
    
                if (networkInfo.isConnected()) {
                	int level = 0;
                	int bet = 0;
                	if (bm instanceof ClientMenu) {
                		level = ((ClientMenu) bm).returnLevel();
                		bet = ((ClientMenu) bm).returnBet();
                	} else {
                		level = ((HostMenu) bm).returnLevel();
                		bet = ((HostMenu) bm).returnBet();
                	}
                    DeviceDetailFragment fragment = new DeviceDetailFragment(activity,level, bet);
                    manager.requestConnectionInfo(channel, fragment);
                }
        }
    }
}
