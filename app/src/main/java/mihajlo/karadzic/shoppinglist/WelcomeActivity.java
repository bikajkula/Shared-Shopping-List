package mihajlo.karadzic.shoppinglist;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DB_NAME = "shared_list_app.db";
    private TextView user;
    private Button but_new_list, but_see_lists,but_home,but_see_shared_lists;
    private CustomRowAdapter adapter;
    private TextView emptyView;
    private DbHelper dbHelper;
    private String username;
    public static String GET_LIST_URL = "https://piars-server.cyclic.app/lists";
    boolean flag;
    boolean shared_clicked=false;
    private HttpHelper httpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        httpHelper = new HttpHelper();

        Bundle bundle = getIntent().getExtras();
        user = findViewById(R.id.wel_user);

        username = bundle.getString("user", "Default");

        user.setText(username);

        but_new_list = findViewById(R.id.but_new_list);
        but_new_list.setOnClickListener(this);

        but_see_lists = findViewById(R.id.but_see_lists);
        but_see_lists.setOnClickListener(this);

        but_see_shared_lists = findViewById(R.id.but_see_shared_lists);
        but_see_shared_lists.setOnClickListener(this);

        but_home= findViewById(R.id.but_home);
        but_home.setOnClickListener(this);

        adapter = new CustomRowAdapter(this);
        ListView list = findViewById(R.id.list_of_lists);
        emptyView = findViewById(R.id.empty_lol_view);   //list of lists
        list.setEmptyView(emptyView);

        list.setAdapter(adapter);

        dbHelper = new DbHelper(this,DB_NAME,null,1);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListRowModel rm = (ListRowModel) adapter.getItem(i);

                if(rm.mShared.equals("Yes")){
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                flag = httpHelper.httpDelete(GET_LIST_URL + "/" + username + "/" + rm.getmTitle());
                                if(!flag){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Error! You are not the creator of that list!", Toast.LENGTH_LONG);
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
                                            dbHelper.deleteList(rm.mTitle);
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
                else {
                    dbHelper.deleteList(rm.mTitle);
                    adapter.removeModel(i);
                }
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListRowModel model = (ListRowModel) adapter.getItem(i);

                Intent intent = new Intent(WelcomeActivity.this, ShowListActivity.class);
                intent.putExtra("title",model.getmTitle());
                intent.putExtra("shared",model.getmShared());
                intent.putExtra("cameFromShared",shared_clicked);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListRowModel[] lists = dbHelper.readLists(user.getText().toString());
        adapter.update(lists);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.but_new_list) {
            Intent intent = new Intent(this, NewListActivity.class);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New List Dialog");
            builder.setMessage("Are you sure you want to create a new list?");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Bundle bundle = new Bundle();
                    bundle.putString("user", user.getText().toString());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog ad = builder.create();
            ad.show();

        }
        else if(view.getId() == R.id.but_see_lists){
            shared_clicked=false;
            ListRowModel[] lists = dbHelper.readMyLists(user.getText().toString());
            adapter.update(lists);
        }
        else if(view.getId() == R.id.but_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.but_see_shared_lists){
            shared_clicked = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonArray = httpHelper.getJSONArrayFromURL(GET_LIST_URL);
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
                        ListRowModel[] liste = new ListRowModel[jsonArray.length()];
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);
                            String sh = String.valueOf(jsonObject.getBoolean("shared")).toString();
                            if(sh.equals("true")){
                                sh = "Yes";
                            }
                            else{
                                sh="No";
                            }
                            liste[i] = new ListRowModel(String.valueOf(jsonObject.getString("name")),sh) ;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.update(liste);
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