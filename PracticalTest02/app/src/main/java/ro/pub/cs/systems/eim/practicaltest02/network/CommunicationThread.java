package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.WeatherForecastInformation;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!");
                    ArrayList<String> data = serverThread.getData();
                    long diff = 0;
                    String last_data = null;
                    long diffMinutes = 0;
                    if(data.size()!=0){
                    last_data = data.get(0);}
                    SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    Date d1;
                    if (last_data!=null) {
                        d1 = null;
                        try {
                            d1 = format.parse(last_data);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        d1 = null;
                        try {
                            d1 = format.parse("11/03/14 09:29:58");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                    Date d2 = null;
                    if (true) {

                        if (false) {
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                        } else {
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            String content = httpClient.execute(httpGet, responseHandler);
                            if (content != null) {
                                // do something with the response
                                try {
                                    d2 = format.parse(content);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.i(Constants.TAG, "Format data error");
                                }
                                diff = d2.getTime() - d1.getTime();
                                diffMinutes = diff /1000;

                            }

                        }

                        if (d2 != null) {
                            String result = null;
                            if (diffMinutes < 60) {
                                result = "Error! You have to wait";
                            } else {
                                result = d2.toString();
                            }
                            printWriter.println(result);
                            printWriter.flush();
                        } else {
                            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast information is null!");
                        }

                    } else {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
                    }
                } else {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }

            }

        }
        else{
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
        }

    }

}
