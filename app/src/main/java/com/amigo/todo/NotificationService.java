package com.amigo.todo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.amigo.todo.contentprovider.ToDoContentProvider;
import com.amigo.todo.databases.ToDoTable;

import java.util.Calendar;
import java.util.Date;

public class NotificationService extends IntentService {

    private static final String TAG = NotificationService.class.getSimpleName();

    NotificationManager notificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null) {
            sendNotification();
        }
    }

    public void sendNotification() {
        String[] projection = { ToDoTable.COLUMN_ID, ToDoTable.COLUMN_TASK };
        String selection = "due_date=? and is_done=?";
        Log.i(TAG, "TO " + String.valueOf(today()));
        String [] selectArgs = new String[] {String.valueOf(today()), String.valueOf(0)};
        Cursor cursor = getContentResolver().query(ToDoContentProvider.CONTENT_URI, projection, selection, selectArgs, null);
        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int columnIndexTask = cursor.getColumnIndex(ToDoTable.COLUMN_TASK);
            int columnIndexId = cursor.getColumnIndex(ToDoTable.COLUMN_ID);
            do {
                String task = cursor.getString(columnIndexTask);
                int id = cursor.getInt(columnIndexId);
                Log.i(TAG, task);
                sendNotification(task, id);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
    }

    public void sendNotification(String task, int id) {
        //create a notification object using the notificationCompat builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("Amigo ToDo Manager");
        builder.setContentText(task);

        //Create a pending intent to attach with the notif
        Intent i = new Intent(getApplicationContext(), ToDoEditActivity.class);
        Uri todoUri = Uri.parse(ToDoContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(ToDoContentProvider.CONTENT_ITEM_TYPE, todoUri);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 101, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        Notification notif = builder.build();

        notificationManager.notify(101, notif);

    }

    private Date today() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day, 23, 59, 59);
        return cal.getTime();
    }
}
