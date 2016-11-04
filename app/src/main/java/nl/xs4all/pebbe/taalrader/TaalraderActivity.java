package nl.xs4all.pebbe.taalrader;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
