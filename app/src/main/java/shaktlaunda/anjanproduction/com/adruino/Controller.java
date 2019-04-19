package shaktlaunda.anjanproduction.com.adruino;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Controller extends AppCompatActivity {

    private Button onBtn;
    private Button ofBtn;
    private Button disconnect;
    SeekBar brightness;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private Button blink;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        onBtn=(Button)findViewById(R.id.onbtn);
        ofBtn=(Button)findViewById(R.id.offbtn);
        disconnect=(Button)findViewById(R.id.disconnect);
        Intent intent=getIntent();
        blink=(Button)findViewById(R.id.blinkBtn);
        address=intent.getStringExtra("address");
        new connectBT().execute();
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLED();
            }
        });


        blink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blinkLed();
            }
        });


        ofBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ofLED();
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null) //If the btSocket is busy
                {
                    try
                    {
                        btSocket.close(); //close connection
                    }
                    catch (IOException e)
                    {Toast.makeText(Controller.this,"FAILED",Toast.LENGTH_SHORT).show(); }
                }
                finish();
            }
        });


    }

    private void blinkLed() {

    if(btSocket!=null){
        try{

            byte[] msg="B".getBytes();
            btSocket.getOutputStream().write(msg);
            Toast.makeText(Controller.this,"SUCCESS",Toast.LENGTH_SHORT).show();



        }catch(IOException e){

            Toast.makeText(Controller.this,"FAILED",Toast.LENGTH_SHORT).show();

        }


    }


    }

    private void ofLED() {

        {
            if (btSocket!=null)
            {
                try
                {
                    byte[] msg="F".getBytes();
                    btSocket.getOutputStream().write(msg);
                    Toast.makeText(Controller.this,"SUCCESS",Toast.LENGTH_SHORT).show();
                }
                catch (IOException e)
                {
                    Toast.makeText(Controller.this,"FAILED",Toast.LENGTH_SHORT).show();
                }
            }






    }}



    private void onLED() {



        if (btSocket!=null)
        {
            try
            {    byte[] msg="T".getBytes();
                btSocket.getOutputStream().write(msg);
                Toast.makeText(Controller.this,"SUCCESS",Toast.LENGTH_SHORT).show();
            }
            catch (IOException e)
            {
                Toast.makeText(Controller.this,"FAILED",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class connectBT extends AsyncTask<Void,Void,Void>{
        private boolean connectSuccess=true;


        @Override
        protected Void doInBackground(Void... voids) {
           try{
           if(btSocket==null||!isBtConnected){
                   myBluetooth=BluetoothAdapter.getDefaultAdapter();
                   BluetoothDevice device=myBluetooth.getRemoteDevice(address);
                   btSocket=device.createRfcommSocketToServiceRecord(myUUID);
                   BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                   btSocket.connect();
               }

           }catch (IOException e){
               connectSuccess=false;

           }
           return null;
        }

        @Override
        protected void onPreExecute() {
           progress=new ProgressDialog(Controller.this);
           progress.setMessage("CONNECTING...");
           progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!connectSuccess){
                Toast.makeText(Controller.this,"COULD N0T CONNECT",Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(Controller.this,"CONNECTED",Toast.LENGTH_SHORT).show();
                isBtConnected=true;
                progress.dismiss();
            }
        }
    }



}
