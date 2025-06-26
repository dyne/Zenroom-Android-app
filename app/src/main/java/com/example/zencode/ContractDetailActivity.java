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

import org.json.JSONObject;
import org.json.JSONException;

class JsonUtils {
    public static String prettyPrintJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
            return json;
        }
    }
}


public class ContractDetailActivity extends AppCompatActivity {

    private EditText editTextContract, editTextKeys, editTextData;
    private Button buttonExecute;
    private TextView textViewResult;
    private ZencodeContract contract;

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
        editTextKeys = findViewById(R.id.editTextKeys);
        editTextData = findViewById(R.id.editTextData);
        buttonExecute = findViewById(R.id.buttonExecute);
        textViewResult = findViewById(R.id.textViewResult);

        // Populate the views if the contract is not null
        if (contract != null) {
            setTitle(contract.getTitle()); // Set activity title
            editTextContract.setText(contract.getContract());
            editTextKeys.setText(contract.getKeys());
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

    private void executeZencode() {
        // Get the (potentially edited) text from the fields
        String script = editTextContract.getText().toString();
        String keys = editTextKeys.getText().toString();
        String data = editTextData.getText().toString();

        // Define conf, extra, context (as in your original code)
        String conf = "logfmt=text, debug=1";
        String extra = "";
        String context = "";

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
        textViewResult.setText(JsonUtils.prettyPrintJson(result));
    }
}