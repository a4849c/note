

package com.may6soft.notepad.TabIDs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.may6soft.notepad.CheckPasswordActivity;
import com.may6soft.notepad.DB.GlobalDataStructure;
import com.may6soft.notepad.ScrollView.MyHorizontalScrollView;
import com.may6soft.notepad.Views.ShowImageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.may6soft.notepad.R.drawable;
import static com.may6soft.notepad.R.id;
import static com.may6soft.notepad.R.layout;
import static com.may6soft.notepad.R.string;

public class IDsTabFragment extends Fragment implements ViewPager.OnPageChangeListener{

	static public SectionsPagerAdapter mSectionsPagerAdapter;
	static public ViewPager mViewPager;
	static private int selectedPageNumber;
	static public IDsPageData mIDsPageData;
	static public MyHorizontalScrollView topTabIndicator;
	//private Intent showImageIntent;
	static final int SHOW_IMAGE_REQUEST = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mIDsPageData = IDsPageData.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(layout.ids_tab, container, false);

		IDsPageData.getIDsData();
		if (mIDsPageData.mPagelist.size() > 0) {
			setSelectedPageNumber(mIDsPageData.mPagelist.get(0).position);
		}

		mSectionsPagerAdapter = new SectionsPagerAdapter(this.getChildFragmentManager());

		mViewPager = (ViewPager) rootView.findViewById(id.ids_tab_pager_container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		topTabIndicator = (MyHorizontalScrollView) rootView.findViewById(id.ids_tab_indicator);
		topTabIndicator.setOnPagerChangeListener(this);
		topTabIndicator.setViewPager(mViewPager);

		return rootView;
	}

	public static int getSelectedPageNumber() {
		return selectedPageNumber;
	}

	public static void setSelectedPageNumber(int position) {
		selectedPageNumber = position;
	}

	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		static private MyAdapter adapter;
		private static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		public PlaceholderFragment() {
		}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(layout.place_holder, container, false);
			Bundle bundle = this.getArguments();
			int pageNumber = bundle.getInt(ARG_SECTION_NUMBER);

			ListView listView = (ListView) rootView.findViewById(id.place_holder_listview);
			adapter = new MyAdapter(this.getContext(), pageNumber);
			listView.setAdapter(adapter);

			return rootView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == SHOW_IMAGE_REQUEST) {
				CheckPasswordActivity.set_password_verified(true);
			}
		}

		private final class ViewHolder{
			public TextView row_name;
			public TextView value_text;
			public ImageView image;
			public ImageView passwordVisibility;
		}

		public class MyAdapter extends BaseAdapter {
			private LayoutInflater mInflater;
			private int pageNumber;
			private Intent showImageIntent;
			private boolean showPassword = true;

			public MyAdapter(Context context, int pageNum){
				mInflater = LayoutInflater.from(context);
				pageNumber = pageNum;
				showImageIntent = new Intent(context, ShowImageActivity.class);
			}

			private void showImage(int pageNumber, int rowNumber) {
				showImageIntent.putExtra(ShowImageActivity.EXTRA_MESSAGE_IMAGE_NUMBER, mIDsPageData.mPagelist.get(pageNumber).rowList.get(rowNumber).dbPrimaryKey);
				startActivityForResult(showImageIntent, SHOW_IMAGE_REQUEST);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mIDsPageData.mPagelist.get(pageNumber).rowList.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					holder = new ViewHolder();

					convertView = mInflater.inflate(layout.row_txt_txt_img_img, null);
					holder.row_name = (TextView)convertView.findViewById(id.row_name);
					holder.row_name.setTag(position);
					holder.value_text = (TextView)convertView.findViewById(id.value_text);
					holder.value_text.setTag(position);
					holder.image = (ImageView)convertView.findViewById(id.image);
					holder.image.setTag(position);
					holder.image.setOnClickListener(new MyOnClickListener(holder) {
						@Override
						public void onClick(View v, ViewHolder holder) {
							int pos = (Integer) holder.image.getTag();
							showImage(pageNumber, pos);
						}
					});
					holder.passwordVisibility = (ImageView) convertView.findViewById(id.password_visibility);
					holder.passwordVisibility.setTag(position);
					holder.passwordVisibility.setOnClickListener(new MyOnClickListener(holder) {
						@Override
						public void onClick(View v, ViewHolder holder) {
							if (showPassword) {
								holder.value_text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
								holder.passwordVisibility.setBackgroundResource(drawable.icon_eye_open);
								holder.passwordVisibility.setSelected(true);
							} else {
								holder.value_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
								holder.passwordVisibility.setBackgroundResource(drawable.icon_eye_close);
								holder.passwordVisibility.setSelected(false);
							}
							showPassword = !showPassword;
						}
					});

					convertView.setTag(holder);
				}else {
					holder = (ViewHolder)convertView.getTag();
					holder.row_name.setTag(position);
					holder.value_text.setTag(position);
					holder.image.setTag(position);
					holder.passwordVisibility.setTag(position);
				}

				holder.row_name.setText(mIDsPageData.mPagelist.get(pageNumber).rowList.get(position).name.toString());
				holder.value_text.setText(mIDsPageData.mPagelist.get(pageNumber).rowList.get(position).text.toString());
				holder.image.setImageBitmap(mIDsPageData.mPagelist.get(pageNumber).rowList.get(position).image);
				if (mIDsPageData.mPagelist.get(pageNumber).rowList.get(position).rowType == GlobalDataStructure.ROW_IMAGE) {
					holder.value_text.setVisibility(View.GONE);
					holder.image.setVisibility(View.VISIBLE);
				}
				else {
					holder.value_text.setVisibility(View.VISIBLE);
					holder.image.setVisibility(View.GONE);
				}
				if (mIDsPageData.mPagelist.get(pageNumber).rowList.get(position).rowType == GlobalDataStructure.ROW_PASSWORD) {
					holder.value_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
					holder.passwordVisibility.setVisibility(View.VISIBLE);
					holder.passwordVisibility.setBackgroundResource(drawable.icon_eye_close);
					holder.passwordVisibility.setSelected(true);
				}
				else {
					holder.value_text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					holder.passwordVisibility.setVisibility(View.GONE);
				}

				return convertView;
			}

			private abstract class MyOnClickListener implements View.OnClickListener {
				private ViewHolder mHolder;

				public MyOnClickListener(ViewHolder holder){
					this.mHolder = holder;
				}

				@Override
				public void onClick(View v) {
					onClick(v, mHolder);
				}

				public abstract void onClick(View v, ViewHolder holder);
			}
		}
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return IDsPageData.getIDsTabNumber();
		}

		@Override
		public Fragment getItem(int position) {
			return PlaceholderFragment.newInstance(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return (mIDsPageData.mPagelist.get(position).title.toString());
		}

		@Override
		public int getItemPosition(Object object) {
			//return super.getItemPosition(object);
			return POSITION_NONE;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		selectedPageNumber = mIDsPageData.mPagelist.get(position).position;
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}


