package com.service.chataround.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.service.chataround.R;
import com.service.chataround.dto.chat.ChatMessageDto;

public class IconListViewAdapter extends ArrayAdapter<ChatMessageDto> {
	public static String TAG = IconListViewAdapter.class.getName();
	private ArrayList<ChatMessageDto> items;
	private Context ctx;

	public IconListViewAdapter(Context context, int textViewResourceId,
			ArrayList<ChatMessageDto> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.ctx = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_foro, null);
		}
		ChatMessageDto o = items.get(position);
		if (o != null) {

			TextView messageMine = (TextView) v.findViewById(R.id.messageright);
			TextView messageOthers = (TextView) v.findViewById(R.id.messageleft);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String time = sdf.format(o.getTime());
			if ( o.isMine() ) {
				messageMine.setText(o.getNickName()+"@"+time+"-"+o.getMessage());
				Typeface tf = Typeface.createFromAsset(ctx.getAssets(),
		        "fonts/Roboto-Medium.ttf");
				messageMine.setTypeface(tf);
				messageMine.setVisibility(View.VISIBLE);
				messageMine.setTextColor(Color.BLACK);
				messageOthers.setVisibility(View.GONE);
				if(o.isSent()){
					messageMine.setCompoundDrawablesWithIntrinsicBounds(R.drawable.navigation_accept, 0, 0, 0);
				}
				
			}else{
				messageOthers.setText(o.getNickName()+"@"+time+"-"+o.getMessage());
				Typeface tf = Typeface.createFromAsset(ctx.getAssets(),
		        "fonts/Roboto-Regular.ttf");
				messageOthers.setTypeface(tf);
				messageOthers.setTextColor(Color.BLACK);
				messageOthers.setVisibility(View.VISIBLE);
				
				messageMine.setVisibility(View.GONE);

			}
		}
		return v;
	}
}