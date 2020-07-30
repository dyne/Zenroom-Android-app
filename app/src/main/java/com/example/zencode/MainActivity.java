package com.example.zencode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import decode.zenroom.Zenroom;

public class MainActivity extends AppCompatActivity {

    String script, data, keys, conf;

    static {
        try {
            System.loadLibrary("zenroom");
            Log.d("testseb", "Loaded zenroom native library");
        } catch (Throwable exc) {
            Log.d("testseb", "Could not load zenroom native library: " + exc.getMessage());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("testseb", "Starting the test....");

        script = "rule check version 1.0.0\n"
        + "Scenario 'ecdh':Create the keypair\n"
        + "Given that I am known as 'Alice'\n"
        + "When I create the keypair\n"
        + "Then print all data";
        keys = "";
        data= "";
        conf = "debug=0";

        String result = (new Zenroom()).execute(script, conf, keys, data);
           Log.d("testseb",(new Zenroom()).execute(script, conf, keys, data));
           Log.d("result",result);
        setContentView(R.layout.activity_main);
      //  findViewById(R.id.result).setText(result);

        setContentView(R.layout.activity_main);


    }

    public void generateAndPrintKeygen(View view) {
            //zencodeKeygen

        Log.d("testseb", "Starting the test....");

        script = "rule check version 1.0.0\n"
                + "Scenario 'ecdh':Create the keypair\n"
                + "Given that I am known as 'Alice'\n"
                + "When I create the keypair\n"
                + "Then print all data";
        keys = "";
        data= "";
    //   Use the conf below to give pass it a know random seed (must be 64 bytes) if you want to test determinism
     conf = "debug=0, rngseed=hex:74eeeab870a394175fae808dd5dd3b047f3ee2d6a8d01e14bff94271565625e98a63babe8dd6cbea6fedf3e19de4bc80314b861599522e44409fdd20f7cd6cfc" ;
     //   conf= "debug=0";

        TextView buttonResult2 = (TextView)findViewById(R.id.buttonResult2);
        buttonResult2.setText(script);

        String result = "empty string";

        // change the i below if you are passing a known rngseed and want to test determinism
        for (int i=0; i<1; i++ ){

            result = (new Zenroom()).execute(script, conf, keys, data);

        }
//           Log.d ( "result: ", result = (new Zenroom()).execute(script, conf, keys, data));  }

        TextView buttonResult = (TextView)findViewById(R.id.buttonResult);
        buttonResult.setText(result);



    }
}