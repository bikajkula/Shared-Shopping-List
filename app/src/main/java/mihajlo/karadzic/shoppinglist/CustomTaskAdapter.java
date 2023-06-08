package mihajlo.karadzic.shoppinglist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomTaskAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ListTaskModel> task_models;

    public CustomTaskAdapter(Context context){
        this.context = context;
        task_models = new ArrayList<ListTaskModel>();
    }

    @Override
    public int getCount() {
        return task_models.size();
    }

    @Override
    public Object getItem(int i) {
        return task_models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addModel(ListTaskModel model){
        task_models.add(model);
        notifyDataSetChanged();
    }

    public void removeModel(int i){
        task_models.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.show_list_row, null);

            ViewHolder vh = new ViewHolder();
            vh.title = (TextView) view.findViewById(R.id.text_row_task_title);
            vh.shared = (CheckBox) view.findViewById(R.id.check_row_task_shared);

            view.setTag(vh);
        }

        ViewHolder vh = (ViewHolder) view.getTag();

        ListTaskModel model = (ListTaskModel) getItem(i);


        vh.shared.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    vh.title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    model.setmChecked(true);
                }
                else{
                    vh.title.setPaintFlags(0);
                    model.setmChecked(false);
                }
            }
        });

        vh.title.setText(model.getmTitle());
        vh.shared.setChecked(model.ismChecked());



        return view;
    }

    private class ViewHolder {
        public TextView title;
        public CheckBox shared;
    }

}
