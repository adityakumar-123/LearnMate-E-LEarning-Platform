package com.learnify.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.learnify.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CodeFragment extends Fragment {

    private WebView codeWebView;
    private Spinner languageSpinner;
    private TextView outputTextView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code, container, false);

        codeWebView = view.findViewById(R.id.codeWebView);
        languageSpinner = view.findViewById(R.id.languageSpinner);
        Button runButton = view.findViewById(R.id.runButton);
        outputTextView = view.findViewById(R.id.outputTextView);

        // Load HTML editor
        codeWebView.getSettings().setJavaScriptEnabled(true);
        codeWebView.getSettings().setDomStorageEnabled(true);
        codeWebView.setWebChromeClient(new WebChromeClient());
        codeWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        codeWebView.loadUrl("file:///android_asset/editor.html");

        // Spinner setup
        String[] languages = {"python", "java", "cpp", "javascript"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Change language on spinner select
        languageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = languages[position];
                codeWebView.evaluateJavascript("setEditorLanguage('" + selectedLang + "');", null);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Run Button
        runButton.setOnClickListener(v -> {
            codeWebView.evaluateJavascript("getEditorCode();", value -> {
                String code = value.replaceAll("^\"|\"$", "").replace("\\n", "\n").replace("\\\"", "\"");
                String selectedLang = languageSpinner.getSelectedItem().toString();
                runCodeWithPiston(selectedLang, code);
            });
        });

        return view;
    }

    private void runCodeWithPiston(String lang, String code) {
        outputTextView.setText("⏳ Running " + lang + " code...");

        HashMap<String, String> langMap = new HashMap<>();
        langMap.put("python", "python3");
        langMap.put("java", "java");
        langMap.put("cpp", "cpp");
        langMap.put("javascript", "javascript");

        String pistonLang = langMap.getOrDefault(lang, "python3");

        new Thread(() -> {
            try {
                JSONObject bodyJson = new JSONObject();
                bodyJson.put("language", pistonLang);
                bodyJson.put("version", "*");

                JSONArray files = new JSONArray();
                JSONObject file = new JSONObject();
                file.put("name", "Main.txt");
                file.put("content", code);
                files.put(file);
                bodyJson.put("files", files);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(bodyJson.toString(), JSON);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://emkc.org/api/v2/piston/execute")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String res = response.body().string();

                JSONObject result = new JSONObject(res);
                String output = result.getJSONObject("run").optString("output", "⚠️ No output");

                requireActivity().runOnUiThread(() -> outputTextView.setText("✅ Output:\n\n" + output));

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> outputTextView.setText("❌ Error: " + e.getMessage()));
            }
        }).start();
    }
}
