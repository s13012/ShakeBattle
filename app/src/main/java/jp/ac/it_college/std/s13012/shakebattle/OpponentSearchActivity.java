package jp.ac.it_college.std.s13012.shakebattle;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class OpponentSearchActivity extends Activity
        implements WifiP2pManager.ChannelListener, WiFiDirectBroadcastReceiver.OnReceiveListener
        , DeviceListFragment.DeviceActionListener, WifiP2pManager.ConnectionInfoListener {

    private IntentFilter intentFilter;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectBroadcastReceiver receiver;
    private WifiP2pInfo info;
    private DeviceListFragment deviceListFragment;

    private Button researchButton;
    private Button disconnectionButton;

    public static String TAG = "OpponentSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponent_search);

        if (savedInstanceState == null) {
            deviceListFragment = new DeviceListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, deviceListFragment)
                    .commit();
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        researchButton = (Button) findViewById(R.id.button_retry_discover);
        researchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opponentsSearch();

            }
        });

        disconnectionButton = (Button) findViewById(R.id.disconnection);
        disconnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.v(TAG, "removeGroup success");
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.v(TAG, "removeGroup failure");
                    }
                });
            }
        });
    }

    private void opponentsSearch() {
        deviceListFragment.clearPeers();
        deviceListFragment.onInitiateDiscovery();
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(OpponentSearchActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(OpponentSearchActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(this);
        registerReceiver(receiver, intentFilter);

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(OpponentSearchActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(OpponentSearchActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /* implemented ChannelListener */
    @Override
    public void onChannelDisconnected() {

    }

    /* implemented OnReceiveListener */
    @Override
    public void onStateChanged() {
        Log.v(TAG, "onStateChanged");
    }

    @Override
    public void onPeersChanged() {
        Log.v(TAG, "onPeersChanged");
        manager.requestPeers(channel, deviceListFragment);
    }

    @Override
    public void onConnectionChanged() {
        Log.v(TAG, "onConnectionChanged");
        NetworkInfo networkInfo = (NetworkInfo) receiver.getIntent()
                .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            manager.requestConnectionInfo(channel, this);
        }

    }

    @Override
    public void onThisDeviceChanged() {
        Log.v(TAG, "onThisDeviceChanged");
        deviceListFragment.updateThisDevice((WifiP2pDevice) receiver.getIntent().getParcelableExtra(
                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
    }

    /* implemented DeviceActionListener */
    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(TAG, "onFailure");
            }
        });
    }

    /* implemented ConnectionInfoListener */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Log.v(TAG, "onConnectionInfoAvailable");
        this.info = wifiP2pInfo;
        Log.v(TAG, "host address = " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        Log.v(TAG, "device name = " + deviceListFragment.getDevice().deviceName);

        if (info.isGroupOwner) {
            Log.v(TAG, "isGroupOwner - " + String.valueOf(info.isGroupOwner));
            new DataServerAsyncTask(this).execute();
        } else {
            Log.v(TAG, "isGroupOwner - " + String.valueOf(info.isGroupOwner));
        }

    }

}
