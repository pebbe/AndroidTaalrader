package nl.xs4all.pebbe.taalrader;

// TODO: styles
// TODO: launch icon
// TODO: publiceren op google play

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;

public class TaalraderActivity extends AppCompatActivity {

    private static Textcat textcat;
    private static String textcatError;

    private EditText editText;
    private TextView textView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String error = bundle.getString("error");
            String webtext = bundle.getString("webtext");
            editText.setText(webtext);
            if (error == null || error.equals("")) {
                doText();
            } else {
                textView.setText(error);
            }
        }
    };

    public void raadtaal(View v) {
        doText();
    }

    private void doText() {
        if (textcat == null) {
            textView.setText(textcatError);
            return;
        }
        String text = editText.getText().toString();
        String[] langs = textcat.classify(text);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < langs.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(codeToLang(langs[i]));
        }
        String taal = builder.toString();
        textView.setText(taal);
    }

    public void clear(View v) {
        editText.setText("");
        textView.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence input = editText.getText();
        CharSequence taal = textView.getText();
        outState.putCharSequence("input", input);
        outState.putCharSequence("taal", taal);
        outState.putInt("saved", 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taalrader);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.myText);
        textView = (TextView) findViewById(R.id.taal);

        if (textcat == null) {
            try {
                InputStream file = getResources().openRawResource(R.raw.data);
                textcat = new Textcat(file);
                file.close();
                textcat.setMinDocSize(10);
            } catch (Exception e) {
                textcatError = editText.toString();
                textcat = null;
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("saved", 0) != 0) {
                editText.setText(savedInstanceState.getCharSequence("input"));
                textView.setText(savedInstanceState.getCharSequence("taal"));
                return;
            }
        }

        String t = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (t == null) {
            return;
        }

        // text from intent, so make scrolling easier
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
            }
        });

        final String text = t;

        Runnable runnable = new Runnable() {
            public void run() {
                String webtext = "";
                String error = "";
                try {
                    Document doc = Jsoup.connect(text).get();
                    webtext = doc.body().text().replaceAll("\\bhttps?://\\S*", " ");
                } catch (Exception e) {
                    error = e.toString();
                }
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("error", error);
                bundle.putString("webtext", webtext);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        };

        if (text.startsWith("http://") || text.startsWith("https://")) {
            textView.setText(R.string.loading);
            editText.setText(" ");
            Thread myThread = new Thread(runnable);
            myThread.start();
        } else {
            editText.setText(text.replaceAll("\\bhttps?://\\S*", " "));
            doText();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_taalrader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            InfoDialogFragment dialog = new InfoDialogFragment();
            dialog.show(getSupportFragmentManager(), "InfoDiaFrag");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String codeToLang(String langcode) {
        String lang;
        switch (langcode) {
            case "SHORT": lang = getString(R.string.SHORT); break;
            case "UNKNOWN": lang = getString(R.string.UNKNOWN); break;
            case "af": lang = getString(R.string.af); break;
            case "ar": lang = getString(R.string.ar); break;
            case "bg": lang = getString(R.string.bg); break;
            case "ca": lang = getString(R.string.ca); break;
            case "cs": lang = getString(R.string.cs); break;
            case "cy": lang = getString(R.string.cy); break;
            case "da": lang = getString(R.string.da); break;
            case "de": lang = getString(R.string.de); break;
            case "el": lang = getString(R.string.el); break;
            case "en": lang = getString(R.string.en); break;
            case "eo": lang = getString(R.string.eo); break;
            case "es": lang = getString(R.string.es); break;
            case "et": lang = getString(R.string.et); break;
            case "eu": lang = getString(R.string.eu); break;
            case "fa": lang = getString(R.string.fa); break;
            case "fi": lang = getString(R.string.fi); break;
            case "fr": lang = getString(R.string.fr); break;
            case "fy": lang = getString(R.string.fy); break;
            case "ga": lang = getString(R.string.ga); break;
            case "gd": lang = getString(R.string.gd); break;
            case "he": lang = getString(R.string.he); break;
            case "hi": lang = getString(R.string.hi); break;
            case "hr": lang = getString(R.string.hr); break;
            case "hu": lang = getString(R.string.hu); break;
            case "ia": lang = getString(R.string.ia); break;
            case "id": lang = getString(R.string.id); break;
            case "is": lang = getString(R.string.is); break;
            case "it": lang = getString(R.string.it); break;
            case "ja": lang = getString(R.string.ja); break;
            case "kk": lang = getString(R.string.kk); break;
            case "ko": lang = getString(R.string.ko); break;
            case "la": lang = getString(R.string.la); break;
            case "lb": lang = getString(R.string.lb); break;
            case "lt": lang = getString(R.string.lt); break;
            case "lv": lang = getString(R.string.lv); break;
            case "mk": lang = getString(R.string.mk); break;
            case "ms": lang = getString(R.string.ms); break;
            case "nl": lang = getString(R.string.nl); break;
            case "no": lang = getString(R.string.no); break;
            case "pl": lang = getString(R.string.pl); break;
            case "pt": lang = getString(R.string.pt); break;
            case "ro": lang = getString(R.string.ro); break;
            case "ru": lang = getString(R.string.ru); break;
            case "sk": lang = getString(R.string.sk); break;
            case "sl": lang = getString(R.string.sl); break;
            case "sq": lang = getString(R.string.sq); break;
            case "sr-latin": lang = getString(R.string.sr); break;
            case "sr": lang = getString(R.string.sr); break;
            case "sv": lang = getString(R.string.sv); break;
            case "sw": lang = getString(R.string.sw); break;
            case "ta": lang = getString(R.string.ta); break;
            case "tl": lang = getString(R.string.tl); break;
            case "tr": lang = getString(R.string.tr); break;
            case "uk": lang = getString(R.string.uk); break;
            case "ur": lang = getString(R.string.ur); break;
            case "vi": lang = getString(R.string.vi); break;
            case "vo": lang = getString(R.string.vo); break;
            case "yi": lang = getString(R.string.yi); break;
            case "yo": lang = getString(R.string.yo); break;
            case "zh": lang = getString(R.string.zh); break;
            default:
                lang = langcode;
        }
        return lang;
    }

}
