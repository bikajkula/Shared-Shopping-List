package mihajlo.karadzic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    EditText user, pass;
    Button but_log,but_home;
    boolean flag;
    private final String DB_NAME = "shared_list_app.db";
    private HttpHelper httpHelper;
    public static String LOGIN_URL = "http://192.168.56.1:3000/login";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_login, container, false);

        httpHelper = new HttpHelper();

        user = v.findViewById(R.id.user_log);
        pass = v.findViewById(R.id.pass_log);

        but_log = v.findViewById(R.id.but_log_2);
        but_log.setOnClickListener(this);

        but_home = v.findViewById(R.id.but_home);
        but_home.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.but_log_2){
            DbHelper dbHelper = new DbHelper(getActivity(), DB_NAME, null, 1);
            if(dbHelper.checkUser(user.getText().toString(), pass.getText().toString())){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("username", user.getText().toString());
                            jsonObject.put("password", pass.getText().toString());
                            flag = httpHelper.postJSONObjectFromURL(LOGIN_URL, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(flag){
                            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();                                    startActivity(intent);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("user", user.getText().toString());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                        } else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Incorrect password / username.\n Login failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).start();
            }
            else{
                Toast.makeText(getContext(), "Incorrect password / username.\n Login failed.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId() == R.id.but_home){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

    }
}