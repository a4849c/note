package com.may6soft.notepad.ScrollView;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.may6soft.notepad.R;

/** 

 */
public class MyHorizontalScrollView extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
	private ViewPager mViewPager;
	private MyLinearLayout mLinearLayout;
	ViewPager.OnPageChangeListener mListener;
	public MyHorizontalScrollView(Context context)
	{
		super(context);
		init(context);
	}
	public MyHorizontalScrollView(Context context,AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}
	public MyHorizontalScrollView(Context context,AttributeSet attrs,int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		setHorizontalScrollBarEnabled(false);
		mLinearLayout = new MyLinearLayout(context);
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		
		addView(mLinearLayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}

	public void setViewPager(ViewPager viewPager) {
		setViewPager(viewPager, 0);
	}

	private void setViewPager(ViewPager viewPager,int initPos)
	{
		if(mViewPager == viewPager)
		{
			return;
		}
		if(mViewPager != null)
		{
			mViewPager.addOnPageChangeListener(null);
		}
		final PagerAdapter adapter = viewPager.getAdapter();
		if(adapter == null)
		{
			throw new IllegalStateException("viewpager does not have adapter instance");
		}
		mViewPager = viewPager;
		viewPager.addOnPageChangeListener(this);
		notifyDataSetChanged();
		setCurrentItem(initPos);
				
	}

	public void notifyDataSetChanged(){
	      mLinearLayout.removeAllViews();
	      PagerAdapter mAdapter = mViewPager.getAdapter();
	      int count = mAdapter.getCount();
	      for(int i=0;i<count;i++){
	            addTab(i,mAdapter.getPageTitle(i));
	      }
	      requestLayout();
	}
	 
	private void addTab(int index,CharSequence text) {
	      TabView tabView = new TabView(getContext());
	      tabView.index = index;
	      tabView.setOnClickListener(mTabClickListener);
	      tabView.setFocusable(true);
	      tabView.setText(text);
	      tabView.setTextSize(15);
	      tabView.setPadding(8,8,8,5);
	      mLinearLayout.addView(tabView);
	}
	public void setCurrentItem(int item)
	{
		if(mViewPager == null)
		{
			throw new IllegalStateException("ViewPager has not been");
		}
		int mSelectTabIndex = item;
		mViewPager.setCurrentItem(item);
		final int tabCount = mLinearLayout.getChildCount();
		for(int i = 0;i<tabCount;i++)
		{
			final TabView child = (TabView) mLinearLayout.getChildAt(i);
			final boolean isSelected = (i == item);
			child.setSelected(isSelected);
			if(isSelected)
			{
				//child.setBackgroundColor(Color.GRAY);
				child.setBackgroundResource(R.drawable.tab_indicator_mtrl_alpha);
				child.setTextColor(getResources().getColor(R.color.seagreen));
				child.setPadding(8,8,8,5);
				animateToTab(item);
				//animateToTab(i);
			}
			else
			{
				child.setBackgroundColor(Color.TRANSPARENT);
				child.setTextColor(getResources().getColor(R.color.black));
				child.setPadding(8,8,8,5);
			}
		}
	}
	private Runnable mTabSelector;
	private void animateToTab(int item)
	{
		final View tabView = mLinearLayout.getChildAt(item);
		if(mTabSelector != null)
		{
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable() {
			@Override
			public void run() 
			{
				final int scrollPos = tabView.getLeft()-(getWidth()-tabView.getWidth())/2;
				smoothScrollTo(scrollPos, 0);
				mTabSelector = null;
			}
		};
		post(mTabSelector);
	}
	private OnClickListener mTabClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v)
		{
			TabView tabView = (TabView) v;
			int newSelected = tabView.index;
			setCurrentItem(newSelected);
		}
	};
	public void setOnPagerChangeListener(ViewPager.OnPageChangeListener listener)
	{
		this.mListener = listener;
	}
	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		if(mListener != null)
		{
			mListener.onPageScrollStateChanged(arg0);
		}
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		if(mListener != null)
		{
			mListener.onPageScrolled(arg0,arg1,arg2);
		}
		
	}
	@Override
	public void onPageSelected(int arg0)
	{
		if(mListener != null)
		{
			mListener.onPageSelected(arg0);
			setCurrentItem(arg0);
		}
		
	}

}
