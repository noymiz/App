package msgs.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.GregorianCalendar;

public class NotificationReceiver extends BroadcastReceiver {
    public static MenuActivity activity;
    @Override
    public void onReceive(Context context, Intent intent) {
        activity.checkForUpdates();

    }
}