package nl.xs4all.pebbe.taalrader;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import go.taalrader.Taalrader;

public class TaalraderActivity extends AppCompatActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String webtext = bundle.getString("webtext");
            EditText v = (EditText) findViewById(R.id.myText);
            v.setText(webtext);
            doText();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taalrader);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        final String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        Runnable runnable = new Runnable() {
            public void run() {
                String webtext = "";
                try {
                    Document doc = Jsoup.connect(text).get();
                    webtext = doc.body().text();
                } catch (Exception e) {
                    webtext = "Error:\n" + e.toString();
                }
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("webtext", webtext);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        };

        EditText v = (EditText) findViewById(R.id.myText);
        if (text.startsWith("http://") || text.startsWith("https://")) {
            Thread myThread = new Thread(runnable);
            myThread.start();
            v.setText("Loading... " + text);
        } else {
            v.setText(text);
            doText();
        }
    }

    public void clear(View v) {
        EditText ev = (EditText) findViewById(R.id.myText);
        ev.setText("");
        TextView tv = (TextView) findViewById(R.id.taal);
        tv.setText("");
    }

    public void raadtaal(View v) {
        doText();
    }

    private void doText() {
        EditText ev = (EditText) findViewById(R.id.myText);
        String text = ev.getText().toString();
        String taal = Taalrader.raadtaal(text);
        taal = taal.replace("\n", " ").replace(".utf8", "");
        TextView tv = (TextView) findViewById(R.id.taal);
        tv.setText(taal);
        // TODO
    }
}
