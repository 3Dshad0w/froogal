package co.froogal.froogal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import co.froogal.froogal.R;
import co.froogal.froogal.model.DrawerItem;

import java.util.List;

public class DrawerAdapter extends BaseAdapter {
	
	private List<DrawerItem> mDrawerItems;
	private LayoutInflater mInflater;
	private final boolean mIsFirstType; //Choose between two types of list items
	
	public DrawerAdapter(Context context, List<DrawerItem> items, boolean isFirstType) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDrawerItems = items;
		mIsFirstType = isFirstType;
	}

	@Override
	public int getCount() {
		return mDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDrawerItems.get(position).getTag();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			if (mIsFirstType) {
				convertView = mInflater.inflate(R.layout.list_view_item_navigation_drawer, parent, false);
			}
			holder = new ViewHolder();
			holder.icon = (TextView) convertView.findViewById(R.id.icon); // holder.icon object is null if mIsFirstType is set to false
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		DrawerItem item = mDrawerItems.get(position);
		

		holder.title.setText(item.getTitle());
		holder.icon.setText(item.getIcon());
		return convertView;
	}
	
	private static class ViewHolder {
		public TextView icon;
		public /*Roboto*/TextView title;
	}
}
