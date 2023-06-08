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

public class NewListActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DB_NAME = "shared_list_app.db";

    TextView title_view;
    EditText title_edit;
    Button but_ok, but_save;
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

        but_no=findViewById(R.id.rad_but_no);
        but_yes=findViewById(R.id.rad_but_yes);
        but_ok=findViewById(R.id.but_new_list_ok);
        but_save=findViewById(R.id.but_new_list_save);

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
                Toast.makeText(this, "Saving successful!", Toast.LENGTH_SHORT).show();
                dbHelper.insertList(rm,user);
                adapter.addModel(rm);
                startActivity(intent);
            }

        }
    }
}