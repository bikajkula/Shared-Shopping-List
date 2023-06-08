package mihajlo.karadzic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NewListActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DB_NAME = "shared_list_app.db";
    public static String NEW_LIST_URL = "https://piars-server.cyclic.app/lists";
    private HttpHelper httpHelper;
    boolean flag;
    TextView title_view;
    EditText title_edit;
    Button but_ok, but_save, but_home;
    String user;
    RadioButton but_yes, but_no;

    CustomRowAdapter adapter;
    DbHelper dbHelper;
    ListRowModel rm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        title_view=findViewById(R.id.list_title_text);
        title_edit=findViewById(R.id.list_title);

        httpHelper = new HttpHelper();

        but_no=findViewById(R.id.rad_but_no);
        but_yes=findViewById(R.id.rad_but_yes);
        but_ok=findViewById(R.id.but_new_list_ok);
        but_save=findViewById(R.id.but_new_list_save);

        but_home = findViewById(R.id.but_home);
        but_home.setOnClickListener(this);

        but_ok.setOnClickListener(this);
        but_save.setOnClickListener(this);

        adapter = new CustomRowAdapter(this);
        dbHelper = new DbHelper(this,DB_NAME,null,1);

        Bundle bundle = getIntent().getExtras();
        user =  bundle.getString("user", "Default");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.but_new_list_ok && !title_edit.getText().toString().isEmpty()){
            title_view.setText(title_edit.getText().toString());

        }
        else if(view.getId() == R.id.but_new_list_save){
            Intent intent = new Intent(this, WelcomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user", user);
            intent.putExtras(bundle);

            if(but_yes.isChecked()){
                rm = new ListRowModel(title_view.getText().toString(), "Yes");
            }
            else{
                rm = new ListRowModel(title_view.getText().toString(), "No");
            }

            if(dbHelper.doesListExist(title_view.getText().toString())){
                Toast.makeText(this, "Saving failed!", Toast.LENGTH_SHORT).show();
            }
            else{
                if(but_yes.isChecked()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", rm.getmTitle());
                                jsonObject.put("creator", user);
                                jsonObject.put("shared", but_yes.isChecked());
                                flag = httpHelper.postJSONObjectFromURL(NEW_LIST_URL, jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(flag){
                                dbHelper.insertList(rm,user);
                                adapter.addModel(rm);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Saving successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    }
                                });
                            } else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Saving failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    }).start();
                }
                else{
                    dbHelper.insertList(rm,user);
                    adapter.addModel(rm);
                    Toast.makeText(getApplicationContext(), "Saving successful!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

            }

        }
        else if(view.getId() == R.id.but_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}