package mihajlo.karadzic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;


public class ShowListActivity extends AppCompatActivity implements View.OnClickListener{

    private final String DB_NAME = "shared_list_app.db";

    private TextView title;
    private Button but_add_task;
    private CustomTaskAdapter adapter;
    private ListView list;

    private DbHelper dbHelper;

    private String title_of_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        Bundle bundle = getIntent().getExtras();
        title = findViewById(R.id.show_list_title);

        title_of_list = bundle.getString("title", "Default");

        title.setText(title_of_list);

        but_add_task = findViewById(R.id.but_add_task);
        but_add_task.setOnClickListener(this);

        list = findViewById(R.id.list_of_tasks);
        adapter = new CustomTaskAdapter(this);

        list.setAdapter(adapter);

        dbHelper = new DbHelper(this,DB_NAME,null,1);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ListTaskModel tm = (ListTaskModel) adapter.getItem(i);
                dbHelper.deleteItem(tm.getuID());
                adapter.removeModel(i);
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
            String title = et.getText().toString();
            if(!title.equals("")) {

                ListTaskModel tm = new ListTaskModel(title, false, null);

                Toast.makeText(this, "Successful addition!", Toast.LENGTH_SHORT).show();

                String uID = UUID.randomUUID().toString();

                dbHelper.insertItem(tm,uID, title_of_list);
                adapter.addModel(new ListTaskModel(title,false,uID));
            }
        }
    }
}