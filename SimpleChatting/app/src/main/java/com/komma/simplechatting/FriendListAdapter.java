package com.komma.simplechatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tommy on 2016. 12. 22..
 */

public class FriendListAdapter extends BaseAdapter implements View.OnClickListener {


    public interface ListBtnClickListener {
        void onListBtnClick(String friendId);
    }

    static class ViewHolder {
        TextView tvFriendId;
        Button   btnFriendId;
    }

    int resoruceId;
    private ListBtnClickListener listBtnClickListener;
    private ArrayList<String> listId = new ArrayList<>();

    public FriendListAdapter(Context context, int resoruceId, ArrayList<String> list, ListBtnClickListener listBtnClickListener) {
        this.resoruceId = resoruceId;
        this.listBtnClickListener = listBtnClickListener;
        this.listId = list;
    }

    public void setListId(ArrayList<String>list) {
        listId = list;
    }

    @Override
    public int getCount() {
        return  listId.size();
    }

    @Override
    public Object getItem(int i) {
        return listId.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        ViewHolder holder = new ViewHolder();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resoruceId, parent, false);


            holder.tvFriendId = (TextView) convertView.findViewById(R.id.txFriendid);
            holder.btnFriendId = (Button) convertView.findViewById(R.id.btFriendid);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvFriendId.setText(listId.get(position));
        holder.btnFriendId.setText(listId.get(position));
        holder.btnFriendId.setTag(listId.get(position));
        holder.btnFriendId.setOnClickListener(this);


        return convertView;
    }

    @Override
    public void onClick(View view) {

        if(this.listBtnClickListener != null) {
            this.listBtnClickListener.onListBtnClick((String)view.getTag());
        }
    }
}
