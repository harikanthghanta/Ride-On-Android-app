package com.android.charan.shareride.tabpanel;

import com.android.charan.shareride.CreateRide;
import com.android.charan.shareride.JoinRidesActivity;
import com.android.charan.shareride.MainActivity;
import com.android.charan.shareride.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class MyTabHostProvider extends TabHostProvider {
	
	private Tab joinRidesTab;
	private Tab myPastRidesTab;
	private Tab createRide;
	private Tab leaderBoard;

	private TabView tabView;
	
	private GradientDrawable transGradientDrawable, gradientDrawable; 
	
	public MyTabHostProvider(Activity context) {
		super(context);
		
		gradientDrawable = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM,
				new int[] {0xFFB2DA1D, 0xFF85A315});
		gradientDrawable.setCornerRadius(0f);
		gradientDrawable.setDither(true);

		transGradientDrawable = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM,
				new int[] {0x00000000, 0x00000000});
		transGradientDrawable.setCornerRadius(0f);
		transGradientDrawable.setDither(true);
	}

	@Override
	public TabView getTabHost(String category) {

		tabView = new TabView(context);
		tabView.setOrientation(TabView.Orientation.BOTTOM);
		tabView.setBackgroundID(R.layout.common_color_gradient);
		
		joinRidesTab = createTab(category, 
				MainActivity.class,
				R.drawable.menu_btn_join_rides, 
				R.drawable.menu_btn_join_rides_selected, 
				MenuConstants.JOIN_RIDES,
				category.equalsIgnoreCase(MenuConstants.JOIN_RIDES)
				);
//		myPastRidesTab = createTab(category,
//				MyPastRidesActivity.class,
//				R.drawable.menu_btn_past_rides,
//				R.drawable.menu_btn_past_rides_selected,
//				MenuConstants.PAST_RIDES,
//				category.equalsIgnoreCase(MenuConstants.PAST_RIDES)
//				);
		createRide = createTab(category, 
				CreateRide.class,
				R.drawable.menu_btn_create_ride, 
				R.drawable.menu_btn_create_ride_selected, 
				MenuConstants.CREATE_RIDE,
				category.equalsIgnoreCase(MenuConstants.CREATE_RIDE)
				);
//		leaderBoard = createTab(category,
//				LeaderBoardActivity.class,
//				R.drawable.menu_btn_leader_board,
//				R.drawable.menu_btn_leader_board_selected,
//				MenuConstants.LEADER_BOARD,
//				category.equalsIgnoreCase(MenuConstants.LEADER_BOARD)
//				);

		tabView.addTab(joinRidesTab);
		/*tabView.addTab(myPastRidesTab);*/
		tabView.addTab(createRide);
		/*tabView.addTab(leaderBoard);*/
		
		decideSelectedTabId(category);

		return tabView;
	}
	
	private void decideSelectedTabId(String category) {
		
		if(category.equalsIgnoreCase(MenuConstants.JOIN_RIDES)) {
			tabView.setSelectedTabId(0);
		} else if(category.equalsIgnoreCase(MenuConstants.PAST_RIDES)) {
			tabView.setSelectedTabId(1);
		} else if(category.equalsIgnoreCase(MenuConstants.CREATE_RIDE)) {
			tabView.setSelectedTabId(2);
		} else if(category.equalsIgnoreCase(MenuConstants.LEADER_BOARD)) {
			tabView.setSelectedTabId(3);
		}
	}
	
	private Tab createTab(String category, Class activity, int icon, int iconSelected, String btnText, boolean isSelected) {

		Tab tab = new Tab(context, category);
		tab.setIcon(icon);
		tab.setIconSelected(iconSelected);
		tab.setBtnText(btnText);
		tab.setBtnTextColor(Color.WHITE);
		tab.setSelectedBtnTextColor(Color.BLACK);
		tab.setBtnGradient(transGradientDrawable);
		tab.setSelectedBtnGradient(gradientDrawable);
		tab.setIntent(new Intent(context, activity));
		tab.setSelected(isSelected);


		return tab;
	}
}