package android.rn.com.barcode.adapter;

import android.app.Activity;
import android.content.Context;
import android.rn.com.barcode.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by VGCSDELL on 10/7/2016.
 */
public class SelectCodeTypeAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private String[] typeName;
    private String[] typeDetail;

    public SelectCodeTypeAdapter(Activity activity, String[] typeName, String[] typeDetail) {
        this.activity = activity;
        this.typeName = typeName;
        this.typeDetail = typeDetail;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return typeName.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.select_code_type_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.detail = (TextView) convertView.findViewById(R.id.detail);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(typeName[position]);
        viewHolder.detail.setText(typeDetail[position]);

        return convertView;
    }

    public class ViewHolder {
        public TextView title = null, detail = null;

    }
}
