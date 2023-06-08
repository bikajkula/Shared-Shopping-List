package mihajlo.karadzic.shoppinglist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyService extends Service {
    private final String DB_NAME = "shared_list_app.db";
    private boolean mRun = true;
    private HttpHelper httpHelper;
    private DbHelper dbHelper;
    private String getLists = "http://piars-server.cyclic.app/lists";
    private String getUsers = "http://piars-server.cyclic.app/users";
    private String getTasks = "http://piars-server.cyclic.app/tasks";
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;


    @Override
    public void onCreate() {
        super.onCreate();
        httpHelper = new HttpHelper();

        dbHelper = new DbHelper(this,DB_NAME,null,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRun) {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONArray jsonarrayL = httpHelper.getJSONArrayFromURL(getLists);

                        for(int i = 0; i < jsonarrayL.length(); i++){
                            JSONObject jsonobject = jsonarrayL.getJSONObject(i);

                            String name = jsonobject.getString("name");
                            Boolean shared = jsonobject.getBoolean("shared");
                            String creator = jsonobject.getString("creator");

                            ListRowModel rm = new ListRowModel(name, "No");

                            if(shared){
                                rm = new ListRowModel(name, "Yes");
                            }

                            if(!dbHelper.doesListExist(name)){
                                dbHelper.insertList(rm,creator);
                                Log.d("TAG","JAOJ");
                            }


                        }

                        JSONArray jsonarrayU = httpHelper.getJSONArrayFromURL(getUsers);

                        for(int i=0;i<jsonarrayU.length(); i++){
                            JSONObject jsonobject = jsonarrayU.getJSONObject(i);

                            String user = jsonobject.getString("username");
                            String email = jsonobject.getString("email");
                            String pass = jsonobject.getString("password");



                            if(!dbHelper.doesUserExist(user)){
                                dbHelper.insertUser(user,email,pass);
                            }
                        }

                        JSONArray jsonarrayT = httpHelper.getJSONArrayFromURL(getTasks);

                        for(int i=0;i<jsonarrayT.length(); i++){
                            JSONObject jsonobject = jsonarrayT.getJSONObject(i);

                            String title = jsonobject.getString("name");
                            String listID = jsonobject.getString("list");
                            Boolean checked = jsonobject.getBoolean("done");
                            String uid = jsonobject.getString("taskId");

                            String list = "";

                            for(int j = 0; j < jsonarrayL.length(); j++){
                                JSONObject jsonobject2 = jsonarrayL.getJSONObject(j);

                                String name = jsonobject2.getString("name");
                                String list_id = jsonobject2.getString("_id");

                                if(list_id.equals(listID)){
                                    list = name;
                                    break;
                                }
                            }

                            ListTaskModel tm = new ListTaskModel(title,checked,uid);
                            if(!dbHelper.doesTaskExist(title,uid)){
                                dbHelper.insertItem(tm,uid,list);
                            }
                        }

                        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                                0, notificationIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT);


                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            NotificationChannel channel =  new NotificationChannel("Channelld", "My_Channel", NotificationManager.IMPORTANCE_DEFAULT);

                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Channelld")
                                .setSmallIcon(R.drawable.ic_baseline_refresh_24)
                                .setContentIntent(contentIntent)
                                .setContentTitle("Data synced")
                                .setContentText("Data has been successfully synced!");

                        notification = builder.build();

                        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

                        notificationManagerCompat.notify(1, notification);




                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service is starting", Toast.LENGTH_SHORT).show();
        /*
        START_STICKY - ako sistem unisti servis kreiraj ga ponovo
        START_NOT_STICKY - ako sistem unisti servis nece ga ponovo kreirati
         */
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "service is done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
