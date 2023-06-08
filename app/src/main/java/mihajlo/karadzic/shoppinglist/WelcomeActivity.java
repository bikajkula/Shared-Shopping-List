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

    private TextView user;
    private Button but_new_list;
    private CustomRowAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Bundle bundle = getIntent().getExtras();
        user = findViewById(R.id.wel_user);
        user.setText(bundle.getString("user", "Default"));

        but_new_list = findViewById(R.id.but_new_list);
        but_new_list.setOnClickListener(this);

        adapter = new CustomRowAdapter(this);
        ListView list = findViewById(R.id.list_of_lists);

        adapter.addModel(new ListRowModel("naslov1","true"));
        adapter.addModel(new ListRowModel("naslov2","false"));
        adapter.addModel(new ListRowModel("naslov3","true"));
        adapter.addModel(new ListRowModel("naslov4","false"));
        adapter.addModel(new ListRowModel("naslov5","true"));
        adapter.addModel(new ListRowModel("naslov6","false"));
        adapter.addModel(new ListRowModel("naslov7","true"));
        adapter.addModel(new ListRowModel("naslov8","true"));
        adapter.addModel(new ListRowModel("naslov9","true"));
        adapter.addModel(new ListRowModel("naslov10","false"));

        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
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
    public void onClick(View view) {
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
}