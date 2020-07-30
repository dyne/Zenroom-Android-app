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


    // In the function below, Zenroom is executed in verbose mode (conf="debug=3"), so the whole execution and the output
    // will be printed to console.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        script = "rule check version 1.0.0\n"
        + "Scenario 'ecdh':Create the keypair\n"
        + "Given that I am known as 'Alice'\n"
        + "When I create the keypair\n"
        + "Then print all data";
        keys = "";
        data= "";
        conf = "debug=3";

        Log.d("testconsole", "Executing Zenroom in verbose mode and printing the keypair only to console...");
        Log.d("testconsole", (new Zenroom()).execute(script, conf, keys, data));
        Log.d("testconsole", "...finished printing the keypair only to console.");
        setContentView(R.layout.activity_main);

    }

    // in this other function, Zenroom is executed in default mode (conf="debug=0"), so a minimal output is printed to
    // console, not including the output of the script, which here is visible only in the app

    public void generateAndPrintKeygen(View view) {

        script = "rule check version 1.0.0\n"
                + "Scenario 'ecdh':Create the keypair\n"
                + "Given that I am known as 'Alice'\n"
                + "When I create the keypair\n"
                + "Then print all data";
        keys = "";
        data= "";

    // Uncomment the line below, to use the "conf" in order to pass Zenroom a know random seed (must be 64 bytes) if you want to test determinism
    // conf = "debug=0 , rngseed=hex:74eeeab870a394175fae808dd5dd3b047f3ee2d6a8d01e14bff94271565625e98a63babe8dd6cbea6fedf3e19de4bc80314b861599522e44409fdd20f7cd6cfc" ;

        TextView buttonResult2 = (TextView)findViewById(R.id.buttonResult2);
        buttonResult2.setText(script);

        String result = "empty string";

        // change the i below if you are passing a known rngseed and want to test determinism
        for (int i=0; i<1; i++ ){
            Log.d("testapp", "Executing the script visible in the app....");
                result = (new Zenroom()).execute(script, conf, keys, data);
            Log.d("testapp", "...done executing the script visible in the app.");
        }

        TextView buttonResult = (TextView)findViewById(R.id.buttonResult);
        buttonResult.setText(result);

    }
}