package mihajlo.karadzic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button but_log, but_reg;
    LinearLayout lay_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentService = new Intent(this, MyService.class);
        startService(intentService);


        but_log=findViewById(R.id.but_log);

        but_reg=findViewById(R.id.but_reg);

        lay_main = findViewById(R.id.lay_main);

        but_log.setOnClickListener(this);
        but_reg.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        lay_main.setVisibility(View.INVISIBLE);

        if(view.getId() == R.id.but_log){
            LoginFragment fragment_login = LoginFragment.newInstance("login","login");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment_login).addToBackStack(null).commit();
        }
        else if(view.getId() == R.id.but_reg){
            RegisterFragment fragment_register = RegisterFragment.newInstance("register","register");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment_register).addToBackStack(null).commit();
        }

    }
}