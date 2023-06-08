package mihajlo.karadzic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class NewListActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title_view;
    EditText title_edit;
    Button but_ok, but_save;
    String user;
    RadioButton but_yes, but_no;

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
            bundle.putString("user", user.toString());
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }
}