package co.froogal.froogal.model;

import android.graphics.drawable.Drawable;

public class DrawerItem {

	public static final int DRAWER_ITEM_TAG_ORDERS = 26;
	public static final int DRAWER_ITEM_TAG_REWARDPOINTS = 27;
	public static final int DRAWER_ITEM_TAG_REDEEM = 28;
	public static final int DRAWER_ITEM_TAG_EDITPROFILE = 29;
	public static final int DRAWER_ITEM_TAG_ABOUTUS = 30;
	public static final int DRAWER_ITEM_TAG_LOGOUT = 31;
	public static final int DRAWER_ITEM_TAG_INVITE = 32;


	public DrawerItem(Drawable icon, int title, int tag) {
		this.icon = icon;
		this.title = title;
		this.tag = tag;
	}

	private Drawable icon;
	private int title;
	private int tag;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
}
