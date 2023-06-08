package mihajlo.karadzic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;


public class ShowListActivity extends AppCompatActivity implements View.OnClickListener{

    private final String DB_NAME = "shared_list_app.db";

    private TextView title;
    private Button but_add_task;
    private Button but_refresh;
    private Button but_home; //new
    private CustomTaskAdapter adapter;
    private ListView list;
    private Boolean cameFromShared;
    private DbHelper dbHelper;
    public static String ADD_TASK_URL = "http://192.168.56.1:3000/tasks";
    boolean flag;
    private String title_of_list;
    private String is_list_shared;
    private HttpHelper httpHelper;
    public static String R_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        Bundle bundle = getIntent().getExtras();
        title = findViewById(R.id.show_list_title);

        httpHelper = new HttpHelper();

        title_of_list = bundle.getString("title", "Default");

        title.setText(title_of_list);

        is_list_shared = bundle.getString("shared","Default");

        cameFromShared = bundle.getBoolean("cameFromShared",false);

        but_add_task = findViewById(R.id.but_add_task);
        but_add_task.setOnClickListener(this);




        but_refresh = findViewById(R.id.but_refresh);
        but_refresh.setOnClickListener(this);

        if(cameFromShared){
            but_refresh.setVisibility(View.VISIBLE);
        }
        else{
            but_refresh.setVisibility(View.INVISIBLE);
        }

        but_home = findViewById(R.id.but_home);
        but_home.setOnClickListener(this);

        list = findViewById(R.id.list_of_tasks);
        adapter = new CustomTaskAdapter(this);

        list.setAdapter(adapter);

        dbHelper = new DbHelper(this,DB_NAME,null,1);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ListTaskModel tm = (ListTaskModel) adapter.getItem(i);

                if(is_list_shared.equals("Yes")){
                    new Thread(new Runnable() {
                        public void run() {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonArray = httpHelper.getJSONArrayFromURL(ADD_TASK_URL + "/"+ title_of_list);

                                for(int j = 0; j < jsonArray.length(); j++)
                                {
                                    jsonObject = jsonArray.getJSONObject(j);

                                    String taskId = String.valueOf(jsonObject.getString("taskId"));


                                    if(taskId.equals(tm.getuID())){
                                        R_id = String.valueOf(jsonObject.getString("_id")).toString();

                                        Log.d("myTag", R_id);
                                    }
                                }

                                flag = httpHelper.httpDelete(ADD_TASK_URL +"/" + R_id);
                                if(!flag){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    });
                                }
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Successfully deleted!", Toast.LENGTH_LONG);
                                            toast.show();
                                            dbHelper.deleteItem(tm.getuID());
                                            adapter.removeModel(i);
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else{
                    dbHelper.deleteItem(tm.getuID());
                    adapter.removeModel(i);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        ListTaskModel[] items = dbHelper.readItems(title_of_list);
        adapter.update(items);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.but_add_task) {
            EditText et = (EditText) findViewById(R.id.show_list_add_task);
            String titleT = et.getText().toString();
            if(!titleT.equals("")) {

                ListTaskModel tm = new ListTaskModel(titleT, false, null);

                Toast.makeText(this, "Successful addition!", Toast.LENGTH_SHORT).show();

                String uID = UUID.randomUUID().toString();

                if(is_list_shared.equals("Yes")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", titleT);
                                jsonObject.put("list", title_of_list);
                                jsonObject.put("done", "false");
                                jsonObject.put("taskId", uID);
                                flag = httpHelper.postJSONObjectFromURL(ADD_TASK_URL, jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                dbHelper.insertItem(tm,uID, title_of_list);
                adapter.addModel(new ListTaskModel(titleT,false,uID));
            }
        }
        else if(view.getId() == R.id.but_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.but_refresh){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonArray = httpHelper.getJSONArrayFromURL(ADD_TASK_URL + "/"+ title_of_list);
                        if(jsonArray == null)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(), "No shared lists available.", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            });
                        }
                        ListTaskModel[] tasks = new ListTaskModel[jsonArray.length()];
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);
                            tasks[i] = new ListTaskModel(String.valueOf(jsonObject.getString("name")),Boolean.valueOf(jsonObject.getBoolean("done")),String.valueOf(jsonObject.getString("taskId")));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.update(tasks);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}