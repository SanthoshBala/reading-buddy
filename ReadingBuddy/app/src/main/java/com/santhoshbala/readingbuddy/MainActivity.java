package com.santhoshbala.readingbuddy;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int PERMISSIONS_REQUEST_ACCESS_CAMERA;
    int TTS_ENGINE_STATUS = 0;

    private TextToSpeech tts;

    private class TTSInitListener implements OnInitListener {
        public void onInit(int status) {
            TTS_ENGINE_STATUS = 1;
        }
    }

    private class ReadTextCallback implements ActionMode.Callback {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            menu.removeItem(android.R.id.selectAll);
            menu.removeItem(android.R.id.copy);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            EditText et = (EditText) findViewById(R.id.textView);
            CharacterStyle cs;

            int start = et.getSelectionStart();
            int end = et.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(et.getText());

            switch(item.getItemId()) {
                case R.id.read:
                    readSelectedEditText();
                    return true;

                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {

        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void readTextAsSpeech(CharSequence text) {
        this.tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, (String) text);
    }

    private void readSelectedEditText() {
        EditText et = (EditText) findViewById(R.id.textView);

        int startSelection = et.getSelectionStart();
        int endSelection = et.getSelectionEnd();

        String selectedText = et.getText().toString().substring(startSelection, endSelection);

        readTextAsSpeech(selectedText);
    }

    private void initializeFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            android.Manifest.permission.CAMERA)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.camera_permission_dialog_message)
                                .setTitle(R.string.camera_permission_dialog_title);

                        AlertDialog dialog = builder.create();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.CAMERA},
                                PERMISSIONS_REQUEST_ACCESS_CAMERA);
                    }
                }
            }
        });
    }

//    private void initializeWebView() {
//        WebView webView = (WebView) findViewById(R.id.webView);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl("https://80000hours.org/career-guide/basics/");
//    }

    private void initializeTTSEngine() {
        TTSInitListener initListener = new TTSInitListener();
        this.tts = new TextToSpeech(getApplicationContext(), initListener);

        this.tts.setLanguage(Locale.ENGLISH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initializeWebView();

        initializeFab();

        initializeTTSEngine();

        EditText et = (EditText) findViewById(R.id.textView);
        et.setCustomSelectionActionModeCallback(new ReadTextCallback());

        readTextAsSpeech("Hello, Reading Buddy!");
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
            item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.read:
                readSelectedEditText();
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
