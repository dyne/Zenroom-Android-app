package com.example.zencode;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dyne.zenroom.Zencode;

public class ContractDetailActivity extends AppCompatActivity {
    private static final String TAG = "ContractDetail";
    private EditText editTextContract, editTextKeys, editTextData;
    private Button buttonExecute, verify;
    private ProgressBar progressBar;
    private WebView webViewResult;

    private ZencodeContract contract;
    private String circuit;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);
        setupToolbar();
        initializeViews();
        populateContractData();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
    }

    private void initializeViews() {
        editTextContract = findViewById(R.id.editTextContract);
        editTextKeys = findViewById(R.id.editTextKeys);
        editTextData = findViewById(R.id.editTextData);
        buttonExecute = findViewById(R.id.buttonExecute);
        webViewResult = findViewById(R.id.webViewResult);
        progressBar = findViewById(R.id.progressBar);
        verify = findViewById(R.id.verify);
    }

    private void populateContractData() {
        contract = getIntent().getParcelableExtra("SELECTED_CONTRACT");
        if (contract == null) return;

        setTitle(contract.getTitle());
        if (getString(R.string.lf).equals(contract.getTitle())) {
            circuit = loadRawResourceAsString(R.raw.circuit);
        } else {
            editTextKeys.setText(contract.getKeys());
        }
        editTextContract.setText(contract.getContract());
        editTextData.setText(contract.getData());
    }

    private void setupListeners() {
        buttonExecute.setOnClickListener(v -> executeZencode());
    }

    private String loadRawResourceAsString(int resourceId) {
        try (InputStream inputStream = getResources().openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString().trim();

        } catch (IOException e) {
            Log.e(TAG, "Failed to read raw resource", e);
            return null;
        }
    }

    private void executeZencode() {
        showLoadingState(true);

        final String script = editTextContract.getText().toString();
        final String keys = editTextKeys.getText().toString();
        final String data = editTextData.getText().toString();
        final String actualKeys = getString(R.string.lf).equals(contract.getTitle()) ? circuit : keys;

        executorService.execute(() -> {
            String result = runZencode(script, actualKeys, data);
            mainThreadHandler.post(() -> handleExecutionResult(result));
        });
    }

    private String runZencode(String script, String keys, String data) {
        try {
            Zencode zencode = new Zencode();
            return zencode.zenroom(script, "logfmt=text, debug=3", keys, data, "", "");
        } catch (UnsatisfiedLinkError | Exception e) {
            Log.e(TAG, "Zenroom execution failed", e);
            return "ERROR: " + e.getMessage();
        }
    }

    private void handleExecutionResult(String result) {
        setupResultActions(result);
        animateWeightChange();
        showLoadingState(false);
        updateWebViewWithResult(result);
        Log.d(TAG, "Execution completed and UI updated.");
    }

    private void showLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonExecute.setEnabled(!isLoading);
    }

    private void setupResultActions(String result) {
        findViewById(R.id.saveLayout).setVisibility(View.VISIBLE);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button verifyButton = findViewById(R.id.verifyButton);

        buttonSave.setOnClickListener(v -> saveToFile(result));
        verifyButton.setOnClickListener(v -> {
            executorService.execute(() -> {
                String response = postProofToServer(result);

                mainThreadHandler.post(() -> {
                    injectResultIntoWebView(response, "https://zkp.api.forkbomb.eu/longfellow-zk-verify-proof");
                });
            });
        });


        if (getString(R.string.lf).equals(contract.getTitle())) {
            verifyButton.setVisibility(View.VISIBLE);
        }
    }



    private void updateWebViewWithResult(String result) {
        String html =
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset='UTF-8'>\n" +
            "  <title>Zenroom Result</title>\n" +
            "  <style>\n" +
            "    pre { white-space: pre-wrap; word-wrap: break-word; background: #f0f0f0 }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div id='content'>\n" +
            "    <pre id='result'>" + result + "</pre>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>";
        webViewResult.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private void animateWeightChange() {
        ScrollView inputScroll = findViewById(R.id.inputScroll);
        WebView webView = findViewById(R.id.webViewResult);

        LinearLayout parentLayout = (LinearLayout) inputScroll.getParent();
        int totalHeight = parentLayout.getHeight();
        int inputTargetHeight = (int) (totalHeight * 0.0);
        int webTargetHeight = (int) (totalHeight * 0.83);

        animateHeight(inputScroll, inputScroll.getHeight(), inputTargetHeight);
        animateHeight(webView, webView.getHeight(), webTargetHeight);
    }

    private void animateHeight(View view, int from, int to) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) valueAnimator.getAnimatedValue();
            view.setLayoutParams(params);
        });
        animator.setDuration(300);
        animator.start();
    }

    private void saveToFile(String content) {
        File file = new File(getExternalFilesDir(null), "zenroom_result.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes());
            showSnackbar("Saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to save file", e);
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    private String postProofToServer(String proofJsonString) {
        try {
            URL url = new URL("https://zkp.api.forkbomb.eu/longfellow-zk-verify-proof");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json"); // ← critical: no charset!
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            byte[] bytes = proofJsonString.getBytes();
            try (OutputStream os = conn.getOutputStream()) {
                os.write(bytes);
            }

            int code = conn.getResponseCode();
            Log.d("ZK_POST", "Response code: " + code + " / " + conn.getResponseMessage());

            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");

            return sb.toString().trim();

        } catch (Exception e) {
            Log.e("ZK_POST", "Exception: " + e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }

    private void injectResultIntoWebView(String response, String url) {
        String escaped = response
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");

        String html = "<html><head><meta charset='utf-8'><style>" +
                "body { font-family: sans-serif; padding: 1em; }" +
                "pre { background: #f0f0f0; padding: 1em; border-radius: 5px; white-space: pre-wrap; word-wrap: break-word; }" +
                ".url { font-size: 12px; color: #888; margin-bottom: 1em; }" +
                "</style></head><body>" +
                "<div class='url'>Verified via: <code>" + url + "</code></div>" +
                "<pre>" + escaped + "</pre>" +
                "</body></html>";

        webViewResult.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
            Log.d(TAG, "ExecutorService shut down.");
        }
    }

    public static class JsonUtils {
        public static String prettyPrintJson(String json) {
            try {
                JSONObject obj = new JSONObject(json);
                return obj.toString(2);
            } catch (JSONException e) {
                return json;
            }
        }
    }
}
