
package com.startagb.startagb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapterTags  extends BaseAdapter {
    public String domain = MyGlobals.getInstance().getDomain();
    Context context;
    String[] tags;


    LayoutInflater inflater;

    public GridAdapterTags(Context context, String[] tags) {
        this.context = context;
        this.tags = tags;
    }

    @Override
    public int getCount() {
        return tags.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.tags_grid_item, null);

        }
        TextView tag = convertView.findViewById(R.id.tagsTV);
        tag.setText(tags[position]);


        return convertView;
    }

}