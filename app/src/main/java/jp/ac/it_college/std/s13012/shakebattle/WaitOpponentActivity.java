package jp.ac.it_college.std.s13012.shakebattle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class WaitOpponentActivity extends Activity
        implements WifiP2pManager.ChannelListener, WiFiDirectBroadcastReceiver.OnReceiveListener
        , DeviceListFragment.DeviceActionListener, WifiP2pManager.ConnectionInfoListener{

    private Class destination;
    private int goal = 0;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectBroadcastReceiver receiver = null;
    private WifiP2pInfo info;
    private DeviceListFragment deviceListFragment;

    public static String TAG = "WaitOpponentActivity";
    public static final String TIME_ATTACK_MODE = "time_attack";
    public static final String COUNT_ATTACK_MODE = "count_attack";

    private Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_opponent);

        destination = (Class) getIntent().getSerializableExtra(BaseFragment.DESTINATION_CLASS);
        goal = getIntent().getIntExtra(BaseFragment.GOAL_VALUE, -1);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        deviceListFragment = new DeviceListFragment();
        sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceListFragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                    Intent serviceIntent = new Intent(getApplicationContext(), DataTransferService.class);
                    serviceIntent.setAction(DataTransferService.ACTION_SEND_DATA);
                    serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                            info.groupOwnerAddress.getHostAddress());
                    serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT
                            , DataTransferService.EXTRAS_PORT_NUMBER);
                    serviceIntent.putExtra(DataTransferService.GAME_MODE, getMode(destination));
                    serviceIntent.putExtra(DataTransferService.GOAL_VALUE, goal);
                    serviceIntent.putExtra(DataTransferService.OPPONENT_NAME
                            ,deviceListFragment.getDevice().deviceName);

                    startService(serviceIntent);
                }
            }
        });
    }

    private String getMode(Class nextActivity){
        if (nextActivity == TimeAttackActivity.class) {
            return TIME_ATTACK_MODE;
        }

        if (nextActivity == CountAttackActivity.class) {
            return COUNT_ATTACK_MODE;
        }

        return null;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Intent intent = new Intent(this, destination)
                    .putExtra(BaseFragment.GOAL_VALUE, goal);
            startActivity(intent);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(this);
        registerReceiver(receiver, intentFilter);

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "discoverPeers success");
            }

            @Override
            public void onFailure(int i) {
                Log.v(TAG, "discoverPeers failure");
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
    }

    @Override
    public void onConnectionChanged() {
        Log.v(TAG, "onConnectionChanged");
        NetworkInfo networkInfo = (NetworkInfo) receiver.getIntent()
                .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            Toast.makeText(this, "接続しました", Toast.LENGTH_SHORT).show();
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
        Log.v(TAG, "connect");
    }

    /* implemented ConnectionInfoListener */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Log.v(TAG, "onConnectionInfoAvailable");
        this.info = wifiP2pInfo;
        Log.v(TAG, "host address = " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        Log.v(TAG, "device address = " + deviceListFragment.getDevice().deviceName);

        if (info.groupFormed && info.isGroupOwner) {

        }
    }
}
