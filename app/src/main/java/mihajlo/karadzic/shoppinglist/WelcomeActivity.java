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

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DB_NAME = "shared_list_app.db";
    private TextView user;
    private Button but_new_list, but_see_lists;
    private CustomRowAdapter adapter;
    private TextView emptyView;
    private DbHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Bundle bundle = getIntent().getExtras();
        user = findViewById(R.id.wel_user);

        username = bundle.getString("user", "Default");

        user.setText(username);

        but_new_list = findViewById(R.id.but_new_list);
        but_new_list.setOnClickListener(this);

        but_see_lists = findViewById(R.id.but_see_lists);
        but_see_lists.setOnClickListener(this);

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

                dbHelper.deleteList(rm.mTitle);
                adapter.removeModel(i);

                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListRowModel model = (ListRowModel) adapter.getItem(i);

                Intent intent = new Intent(WelcomeActivity.this, ShowListActivity.class);
                intent.putExtra("title",model.getmTitle());
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
            ListRowModel[] lists = dbHelper.readMyLists(user.getText().toString());
            adapter.update(lists);
        }

    }
}