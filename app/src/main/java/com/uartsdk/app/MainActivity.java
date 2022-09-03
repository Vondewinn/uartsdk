package com.uartsdk.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.lc31.uartsdk.ProcessData;
import com.lc31.uartsdk.SerialHelper;

public class MainActivity extends AppCompatActivity {

    private SerialHelper ttyS0;
    private String strData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttyS0 = new SerialHelper("ttyS0", 115200);
        ttyS0.open();
        ttyS0.uartRevData(new ProcessData() {
            @Override
            public void process(byte[] revData, int len) {

                String dataOut = "";
                String aa = new String(revData, 0, len);
                strData = strData + aa;

                int strGGA = strData.indexOf("$GPGGA,");
                String subData = strData.substring(strGGA+"$GPGGA,".length());

                if (subData.contains("$GPGGA,")){
                    dataOut = strData.substring(0, strGGA+subData.indexOf("$GPGGA,") + "$GPGGA,".length());
                    strData = subData.substring(subData.indexOf("$GPGGA,"));
                }

                if (!dataOut.equals("")) {
                    Log.i("TAG", "process: " + dataOut);
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttyS0.close();
    }
}