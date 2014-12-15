package com.fn.reunion.app.ui.base;


import com.fn.reunion.app.ui.pages.BuddiesListPage;
import com.fn.reunion.app.ui.pages.NewsPage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			return new NewsPage();
		case 1:
			return new BuddiesListPage();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
