package com.xiuxiu.request;

import android.os.AsyncTask;

import com.xiuxiu.model.ThemeInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzdykj on 2017/7/3.
 */

public class RetrieveFeed extends AsyncTask {
    ArrayList<String> headlines = new ArrayList();
    ArrayList<String> links = new ArrayList();
    private List<ThemeInfo> themeInfos;
    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            String url = "http://service.niupaisp.com/restheme.xml";
            InputStream inputStream = HttpUtils.getXML(url);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(inputStream, "UTF-8");

            boolean insideItem = false;

            themeInfos = new ArrayList<ThemeInfo>();

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("themeid")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("themeName")) {
                        if (insideItem)
                            headlines.add(xpp.nextText()); //extract the headline
                    } else if (xpp.getName().equalsIgnoreCase("themeUrl")) {
                        if (insideItem)
                            links.add(xpp.nextText());
                    }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                }

                eventType = xpp.next(); //move to next element
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headlines;
    }

    public ArrayList<String> heads() {
        return headlines;
    }
}
