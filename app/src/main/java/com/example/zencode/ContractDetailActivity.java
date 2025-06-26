package com.example.zencode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import dyne.zenroom.Zencode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ContractDetailActivity extends AppCompatActivity {

    private EditText editTextContract, editTextKeys, editTextData;
    private Button buttonExecute;
    private TextView textViewResult;
    private ZencodeContract contract;

    private String circuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        // Get the contract from the intent
        contract = getIntent().getParcelableExtra("SELECTED_CONTRACT");

        // Initialize views
        editTextContract = findViewById(R.id.editTextContract);
        editTextKeys = findViewById(R.id.editTextKeys); // This is the target EditText
        editTextData = findViewById(R.id.editTextData);
        buttonExecute = findViewById(R.id.buttonExecute);
        textViewResult = findViewById(R.id.textViewResult);

        // Populate the views if the contract is not null
        if (contract != null) {
            setTitle(contract.getTitle()); // Set activity title
            if (contract.getTitle().equals("lf")) {
                 circuit = loadStringFromRawResource(R.raw.circuit);
                 Log.e("caopsap", circuit);
            } else {
                editTextKeys.setText(contract.getKeys());
            }
            editTextContract.setText(contract.getContract());
            editTextData.setText(contract.getData());
        }

        // Set listener for the execute button
        buttonExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeZencode();
            }
        });
    }

    // Helper method to read a string from a raw resource file
    private String loadStringFromRawResource(int resourceId) {
        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = getResources().openRawResource(resourceId);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == '\n') {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            Log.e("LoadRawFile", "Error reading raw resource file: " + getResources().getResourceEntryName(resourceId), e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("LoadRawFile", "Error closing input stream", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("LoadRawFile", "Error closing buffered reader", e);
                }
            }
        }
    }


    private void executeZencode() {
        String script = editTextContract.getText().toString();
        String keys = editTextKeys.getText().toString();
        String data = editTextData.getText().toString();

        String conf = "logfmt=text, debug=1";
        String extra = "";
        String context = "";

        if (contract.getTitle().equals("lf")) {
            keys = circuit;
            Log.e("<<<<<<<<<<<<<<", keys);
        }

        Log.d("ZencodeExecute", "Executing contract...");
        String result;
        try {
            Zencode zencodeInstance = new Zencode();
            result = zencodeInstance.zenroom(script, conf, keys, data, extra, context);
        } catch (UnsatisfiedLinkError ule) {
            String errorMsg = "Failed to link Zenroom native method: " + ule.getMessage();
            Log.e("ZencodeExecute", errorMsg, ule);
            result = "ERROR: " + errorMsg;
        } catch (Exception e) {
            String errorMsg = "Exception calling Zenroom native method: " + e.getMessage();
            Log.e("ZencodeExecute", errorMsg, e);
            result = "ERROR: " + errorMsg;
        }

        Log.d("ZencodeExecute", "Result: " + result);
        textViewResult.setText(result);
    }
}