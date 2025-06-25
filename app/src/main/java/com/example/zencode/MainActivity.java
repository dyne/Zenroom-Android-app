package com.example.zencode;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import dyne.zenroom.Zencode;

public class MainActivity extends AppCompatActivity {

  String script, data, keys, conf;
  // Define placeholders or actual values for the new parameters
  String extra = ""; // Or some meaningful string
  String context = ""; // Or some meaningful string, or null if appropriate

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//
//    script = "rule check version 1.0.0\n"
//      + "Scenario 'ecdh':Create the keypair\n"
//      + "Given that I am known as 'Alice'\n"
//      + "When I create the ecdh key\n"
//      + "Then print 'keyring'";
//    keys = "";
//    data = "";
//    conf = "logfmt=text, debug=3";
//
//    Log.d("testconsole", "Executing Zenroom in verbose mode and printing the keypair only to console...");
//
//    Zencode zencodeInstance = new Zencode();
//    String result = "Error calling native method";
//    try {
//      // PASS THE TWO NEW PARAMETERS
//      result = zencodeInstance.zenroom(script, conf, keys, data, extra, context);
//    } catch (UnsatisfiedLinkError ule) {
//      Log.e("testconsole", "Failed to link Zenroom native method: " + ule.getMessage(), ule);
//    } catch (Exception e) {
//      Log.e("testconsole", "Exception calling Zenroom native method: " + e.getMessage(), e);
//    }
//
//    Log.d("testconsole", result);
//    Log.d("testconsole", "...finished printing the keypair only to console.");
    setContentView(R.layout.activity_main);
  }

  public void generateAndPrintKeygen(View view) {
    script = "rule check version 1.0.0\n"
      + "Scenario 'ecdh':Create the keypair\n"
      + "Given that I am known as 'Bob'\n"
      + "When I create the ecdh key\n"
      + "Then print the 'keyring'";
    keys = "";
    data = "";
    conf = "logfmt=text, debug=2";
    // You might need to define conf, extraValue, contextValue here too if they differ
    // String localConf = "debug=0";
    // String localExtra = "some_extra_for_bob";
    // String localContext = "bob_context";


    TextView buttonResult2 = (TextView) findViewById(R.id.buttonResult2);
    buttonResult2.setText(script);

    String resultFromButton = "empty string";

    Zencode zencodeInstance = new Zencode();
    try {
      // PASS THE TWO NEW PARAMETERS
      resultFromButton = zencodeInstance.zenroom(script, conf, keys, data, extra, context);
      // Or use localConf, localExtra, localContext if defined for this method
    } catch (UnsatisfiedLinkError ule) {
      Log.e("testapp", "Failed to link Zenroom native method: " + ule.getMessage(), ule);
      resultFromButton = "Error: " + ule.getMessage();
    } catch (Exception e) {
      Log.e("testapp", "Exception calling Zenroom native method: " + e.getMessage(), e);
      resultFromButton = "Error: " + e.getMessage();
    }

    Log.d("testconsole", resultFromButton);
    Log.d("testconsole", "...finished printing the keypair only to console.");

    TextView buttonResult = (TextView) findViewById(R.id.buttonResult);
    buttonResult.setText(resultFromButton);
  }
}
