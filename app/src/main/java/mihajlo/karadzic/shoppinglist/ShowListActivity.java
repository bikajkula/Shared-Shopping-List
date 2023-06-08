package mihajlo.karadzic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;



public class ShowListActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView title;
    private Button but_add_task;
    private CustomTaskAdapter adapter;
    private ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        Bundle bundle = getIntent().getExtras();
        title = findViewById(R.id.show_list_title);
        title.setText(bundle.getString("title", "Default"));

        but_add_task = findViewById(R.id.but_add_task);
        but_add_task.setOnClickListener(this);

        list = findViewById(R.id.list_of_tasks);
        adapter = new CustomTaskAdapter(this);

        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.removeModel(i);

                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.but_add_task) {
            EditText et = (EditText) findViewById(R.id.show_list_add_task);
            String title = et.getText().toString();
            if(!title.equals("")) {
                adapter.addModel(new ListTaskModel(title,false));
            }
        }
    }
}