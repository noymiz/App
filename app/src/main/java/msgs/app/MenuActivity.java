package msgs.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;



public class MenuActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "ChatActivity";
    private List<Msg> serverMessages;
    private ChatAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private GetMessagesTask mAuthTask;
    private PostMessageTask mAuthTask2;
    private UpdateMessagesTask mAuthTask3;
    private SwipeRefreshLayout mySwipe;
    private SensorManager sensorManager;
    private static final int SHAKE_THRESHOLD = 6000;
    float last_x,last_y,last_z;
    private long lastUpdate;
    private int lastSize;
    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        lastSize = 0;
        super.onCreate(savedInstanceState);
        serverMessages = new ArrayList<>();
        setContentView(R.layout.activity_menu);
        buttonSend = (Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        chatArrayAdapter = new ChatAdapter(getApplicationContext(), R.layout.left);
        listView.setAdapter(chatArrayAdapter);
        lastUpdate = System.currentTimeMillis();
        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        mAuthTask = new GetMessagesTask();
        mAuthTask.execute();
        mySwipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        chatArrayAdapter.setMaxMsgs(chatArrayAdapter.getMaxMsgs() + 10);
                        chatArrayAdapter.clearList();
                        serverMessages.clear();
                        mAuthTask = new GetMessagesTask();
                        mAuthTask.execute();
                        mySwipe.setRefreshing(false);
                    }
                }
        );
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        NotificationReceiver.activity = this;
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Long timeToAlert = new GregorianCalendar().getTimeInMillis() + 60000*5;
        Intent notify = new Intent(this, NotificationReceiver.class);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToAlert,
                PendingIntent.getBroadcast(this, 0, notify, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private boolean sendChatMessage() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        mAuthTask2 = new PostMessageTask(settings.getString("username",""),
                                        currentTime, chatText.getText().toString());
        mAuthTask2.execute();
        return true;
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        long curTime = System.currentTimeMillis();
        // only allow one update every 100ms.
        
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float x = values[0];
            float y = values[1];
            float z = values[2];

            float speed = Math.abs(x+y+z-last_x-last_y-last_z) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
                Toast.makeText(MenuActivity.this, "Loading...", Toast.LENGTH_LONG).show();
                chatArrayAdapter.clearList();
                serverMessages.clear();
                mAuthTask = new GetMessagesTask();
                mAuthTask.execute();
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void checkForUpdates() {
        mAuthTask3 = new UpdateMessagesTask(this);
        mAuthTask3.execute();
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Long timeToAlert = new GregorianCalendar().getTimeInMillis() + 60000*5;
        Intent notify = new Intent(this, NotificationReceiver.class);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToAlert,
                PendingIntent.getBroadcast(this, 0, notify, PendingIntent.FLAG_UPDATE_CURRENT));
    }


    public class GetMessagesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                StringBuilder req = new StringBuilder();
                req.append("http://192.168.1.11:8080/Server/GetMsgs");
                URL url = new URL(req.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        addMessageToList(inputStr);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String answer) {
                lastSize = serverMessages.size();
                for (Msg curr : serverMessages)
                    chatArrayAdapter.add(curr);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        protected void addMessageToList(String message){
            if (message!=null && !message.isEmpty() && message.contains("~")) {
                String[] data = message.split("~");
                serverMessages.add(new Msg(data[0], data[1], data[2]));
            }
        }
    }

    public class PostMessageTask extends AsyncTask<Void, Void, String> {
        private String text;
        private String username;
        private String time;
        public PostMessageTask(String user, String currentTime, String txt) {
            text =txt;
            username = user;
            time = currentTime;
        }
        @Override
        protected String doInBackground(Void... params) {
            String inputStr ="";
            try {
                StringBuilder req = new StringBuilder();
                String newText = text.replaceAll(" ", "%20");
                String newUser = username.replaceAll(" ", "%20");
                req.append("http://192.168.1.11:8080/Server/PostMsg?username=")
                .append(newUser).append("&time=").append(time).append("&text=").append(newText);
                URL url = new URL(req.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    while ((inputStr = streamReader.readLine()) != null);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return inputStr;
        }

        @Override
        protected void onPostExecute(final String answer) {
            chatArrayAdapter.add(new Msg(username, time, text));
            chatText.setText("");
            lastSize++;
        }

        @Override
        protected void onCancelled() {
            mAuthTask2 = null;
        }

    }

    public class UpdateMessagesTask extends AsyncTask<Void, Void, Integer> {
        private Context cont;
        public UpdateMessagesTask(Context contex){
            cont = contex;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            Integer count = 0;
            try {
                StringBuilder req = new StringBuilder();
                req.append("http://192.168.1.11:8080/Server/GetMsgs");
                URL url = new URL(req.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    while ((streamReader.readLine()) != null)
                        count++;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return count;
        }

        @Override
        protected void onPostExecute(final Integer count) {
            if (count-1 > lastSize){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(cont)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("You Have New Messages!")
                                .setContentText("Click here to see what's new");

                int mNotificationId = 001;
                Intent resultIntent = new Intent(MenuActivity.this, MenuActivity.class);

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                MenuActivity.this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                NotificationManager mNotifyMgr =
                        (NotificationManager) cont.getSystemService(cont.NOTIFICATION_SERVICE);
                mBuilder.setContentIntent(resultPendingIntent);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
                serverMessages.clear();
                mAuthTask = new GetMessagesTask();
                mAuthTask.execute();
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask3 = null;
        }
    }

}