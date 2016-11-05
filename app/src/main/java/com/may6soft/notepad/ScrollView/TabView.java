package com.may6soft.notepad.ScrollView;

import android.content.Context;
import android.widget.TextView;

/** 

 */
public class TabView extends TextView{

	public int index;
	public TabView(Context context)
	{
		super(context);
	}
	public TabView(Context context, int index)
	{
		this(context);
		this.index = index;
	}
	

}
