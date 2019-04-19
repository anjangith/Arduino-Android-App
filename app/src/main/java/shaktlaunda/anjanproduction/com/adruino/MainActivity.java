package shaktlaunda.anjanproduction.com.adruino;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button button,pairbtn;
    private ListView listView;
    private BluetoothAdapter mBlueToothAdapter;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progressDialog;
    private BluetoothSocket mBluetoothSocket;
    private static final int REQUEST_BT=1;
    private TextView statusText;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Set<BluetoothDevice> mDevices;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.searchbtn);
        listView=(ListView)findViewById(R.id.list);
        pairbtn=(Button)findViewById(R.id.pairbtn) ;
        statusText=(TextView)findViewById(R.id.status);
        mBTArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(mBTArrayAdapter);
        listView.setOnItemClickListener(listClick);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("SEARCHING");
                progressDialog.setCancelable(false);
                progressDialog.show();
                mBlueToothAdapter=BluetoothAdapter.getDefaultAdapter();
                if(mBlueToothAdapter==null){
                    Toast.makeText(MainActivity.this,"NO BT FOUND",Toast.LENGTH_SHORT).show();
                }

                else if(!mBlueToothAdapter.isEnabled()){
                    Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent,REQUEST_BT);
                }

                else{

                    ListDevices();

                }
            }
        });


        pairbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBlueToothAdapter=BluetoothAdapter.getDefaultAdapter();
                mDevices=mBlueToothAdapter.getBondedDevices();
                if(mDevices.size()>0){
                for(BluetoothDevice device:mDevices){
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mBTArrayAdapter.notifyDataSetChanged();
                }
            }}
        });




    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(blReceiver);
        super.onDestroy();

    }

    private void ListDevices() {


        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mBlueToothAdapter.startDiscovery();
        registerReceiver(blReceiver,filter);


    }


    final BroadcastReceiver blReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
                statusText.setText("NEW DEVICES FOUND");


        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                statusText.setText("SEARCHING");

            }

            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            progressDialog.dismiss();
            statusText.setText("NO DEVICE FOUND");

            }

    }};


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==REQUEST_BT)||resultCode==RESULT_OK){
            ListDevices();

        }
    }
    private AdapterView.OnItemClickListener listClick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);
            Toast.makeText(getApplicationContext(),"ADDRESS:"+address+"\n"+"NAME:"+name,Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,Controller.class);
            intent.putExtra("address",address);
            startActivity(intent);
        }
    };
}
