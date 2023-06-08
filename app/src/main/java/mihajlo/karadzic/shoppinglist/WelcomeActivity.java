package mihajlo.karadzic.shoppinglist;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView user;
    Button but_new_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Bundle bundle = getIntent().getExtras();
        user = findViewById(R.id.wel_user);
        user.setText(bundle.getString("user", "Default"));

        but_new_list = findViewById(R.id.but_new_list);
        but_new_list.setOnClickListener(this);

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