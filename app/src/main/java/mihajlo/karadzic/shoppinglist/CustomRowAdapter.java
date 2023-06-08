package mihajlo.karadzic.shoppinglist;

import static mihajlo.karadzic.shoppinglist.R.layout.welcome_list_row;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomRowAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ListRowModel> row_models;

    public CustomRowAdapter(Context context){
        this.context = context;
        row_models = new ArrayList<ListRowModel>();
    }

    @Override
    public int getCount() {
        return row_models.size();
    }

    @Override
    public Object getItem(int i) {
        return row_models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addModel(ListRowModel model){
        row_models.add(model);
        notifyDataSetChanged();
    }

    public void removeModel(int i){
        row_models.remove(i);
        notifyDataSetChanged();
    }

    public void update (ListRowModel[] lists) {
        row_models.clear();
        if(lists != null) {
            for(ListRowModel list : lists) {
                row_models.add(list);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        ViewHolder vh;


        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.welcome_list_row, null);

            vh = new ViewHolder();
            vh.title = (TextView) view.findViewById(R.id.text_row_title);
            vh.shared = (TextView) view.findViewById(R.id.text_row_shared);

            view.setTag(vh);
        }
        else{
            vh = (ViewHolder) view.getTag();
        }
        ListRowModel model = (ListRowModel) getItem(i);

        vh.title.setText(model.getmTitle());
        vh.shared.setText(model.getmShared());



        return view;
    }

    private class ViewHolder {
        public TextView title;
        public TextView shared;
    }
}
