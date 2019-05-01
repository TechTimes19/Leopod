package com.example.leopod ;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Fragment1 extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_1, container, false);
    }


    public class fragment1 extends AppCompatActivity {

        ListView lvRss1;
        ArrayList<String> titles;
        ArrayList<String> links;
        ArrayList<String> pubDate;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            lvRss1 = (ListView) findViewById(R.id.lvRss1);

            titles = new ArrayList<String>();
            links = new ArrayList<String>();
            pubDate = new ArrayList<String>();

            lvRss1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Uri uri = Uri.parse(links.get(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }
            });

            new ProcessInBackground().execute();
        }

        public InputStream getInputStream(URL url) {
            try {
                return url.openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
        }

        public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {
            ProgressDialog progressDialog = new ProgressDialog(fragment1.this);

            Exception exception = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.setMessage("Hold Your Breath....");
                progressDialog.show();
            }

            @Override
            protected Exception doInBackground(Integer... integers) {
                try {
                    URL url = new URL("http://cyrussays.ivm.libsynpro.com/rss");

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    factory.setNamespaceAware(false);

                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(getInputStream(url), "UTF_8");

                    boolean insideitem = false;

                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideitem = true;
                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideitem) {
                                    titles.add(xpp.nextText());
                                }
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideitem) {
                                    links.add(xpp.nextText());
                                }
                            } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                if (insideitem) {
                                    pubDate.add(xpp.nextText());
                                }
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideitem = false;
                        }

                        eventType = xpp.next();
                    }
                } catch (MalformedURLException e) {
                    exception = e;
                } catch (XmlPullParserException e) {
                    exception = e;
                } catch (IOException e) {
                    exception = e;
                }

                return exception;
            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(fragment1.this, android.R.layout.simple_list_item_1, titles);

                lvRss1.setAdapter(adapter);


                progressDialog.dismiss();
            }
        }
    }
}


