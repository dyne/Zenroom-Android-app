package com.example.zencode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Looper;  // For posting to main thread
import android.os.Handler;  // For posting to main thread
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import dyne.zenroom.Zencode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.json.JSONException;
import android.webkit.WebView;

public class ContractDetailActivity extends AppCompatActivity {
    public static final String LONGFELLOW_CONTRACT_TITLE = "lf";
    private EditText editTextContract, editTextKeys, editTextData;
    private TextView textViewResult;
    private Button buttonExecute;
    private ZencodeContract contract;
    private ProgressBar progressBar;
    private WebView webViewResult;

    private String circuit;

    // ExecutorService for background tasks
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // Handler to post results back to the main thread
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());


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
        webViewResult = findViewById(R.id.webViewResult);
        progressBar = findViewById(R.id.progressBar);

        // Populate the views if the contract is not null
        if (contract != null) {
            setTitle(contract.getTitle()); // Set activity title
            if (contract.getTitle().equals("lf")) {
                circuit = loadStringFromRawResource(R.raw.circuit);
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
        progressBar.setVisibility(View.VISIBLE);
        buttonExecute.setEnabled(false);
        String script = editTextContract.getText().toString();
        String keys = editTextKeys.getText().toString();
        String data = editTextData.getText().toString();

        String conf = "logfmt=text, debug=3";
        String extra = "";
        String context = "";


        executorService.execute(() -> {
            final String actualKeys;
            if (contract.getTitle().equals(LONGFELLOW_CONTRACT_TITLE)) {
                actualKeys = circuit;
            } else {
                actualKeys = keys;
            }
            Log.d("ZencodeExecute", "Executing contract...");
            String result;
            try {
                Zencode zencodeInstance = new Zencode();
                Log.d("ZencodeExecute", "🚀 Preparing to execute Zenroom with parameters:");
                Log.d("ZencodeExecute", "📜 Script: " + script);
                Log.d("ZencodeExecute", "⚙️ Conf: " + conf);
                Log.d("ZencodeExecute", "🔑 Keys: " + actualKeys);
                Log.d("ZencodeExecute", "💾 Data: " + data);
                Log.d("ZencodeExecute", "➕ Extra: " + extra);
                Log.d("ZencodeExecute", "🌍 Context: " + context);

                result = zencodeInstance.zenroom(script, conf, actualKeys, data, extra, context);
            } catch (UnsatisfiedLinkError ule) {
                String errorMsg = "Failed to link Zenroom native method: " + ule.getMessage();
                Log.e("ZencodeExecute", errorMsg, ule);
                result = "ERROR: " + errorMsg;
            } catch (Exception e) {
                String errorMsg = "Exception calling Zenroom native method: " + e.getMessage();
                Log.e("ZencodeExecute", errorMsg, e);
                result = "ERROR: " + errorMsg;
            }


            String finalResult = result;
            mainThreadHandler.post(() -> {
                progressBar.setVisibility(View.GONE);
                buttonExecute.setEnabled(true);

                // Wrap in basic HTML, using <pre> for preformatted text
                // The CSS ensures long lines wrap correctly
                String htmlContent = "<html><head><style>pre { white-space: pre-wrap; word-wrap: break-word; }</style></head><body><pre>"
                        + finalResult
                        + "</pre></body></html>";

                // Load the HTML content into the WebView
                webViewResult.loadDataWithBaseURL(
                        null,        // baseUrl (null for local content)
                        htmlContent, // data (your HTML string)
                        "text/html", // mimeType
                        "UTF-8",     // encoding
                        null         // historyUrl (null)
                );
                Log.d("ZencodeExecute", "UI updated with result.");
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // It's good practice to shut down the executor when the Activity is destroyed,
        // though running tasks will complete unless you call shutdownNow().
        if (executorService != null && !executorService.isShutdown()) {
            // executorService.shutdown(); // Allows existing tasks to complete
            executorService.shutdownNow(); // Attempts to stop all actively executing tasks
            Log.d("ContractDetailActivity", "ExecutorService shut down.");
        }
    }
}

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
