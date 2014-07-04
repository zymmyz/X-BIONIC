package com.imcore.xbionic.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class GridListView extends GridView {
	/*
	 * public GridListView(android.content.Context context,
	 * android.util.AttributeSet attrs) { super(context, attrs); }
	 */

	public GridListView(Context context) {
		super(context);

	}

	public GridListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	/*@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}*/

	/**
	 * 设置不滚动
	 */
	/*
	 * public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { //
	 * 根据view的实际大小设置相应的值 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	 * int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
	 * MeasureSpec.AT_MOST);
	 * 
	 * //setMeasuredDimension(widthMeasureSpec,expandSpec);
	 * 
	 * // setMeasuredDimension(600,300); // int specMode =
	 * MeasureSpec.getMode(measureSpec); // int specSize =
	 * MeasureSpec.getSize(measureSpec); /* MeasureSpec.UNSPECIFIED,
	 * MeasureSpec.EXACTLY, MeasureSpec.AT_MOST
	 */
	// int mode = MeasureSpec.getMode(widthMeasureSpec);
	// int size = MeasureSpec.getSize(widthMeasureSpec);
	// setMeasuredDimension(getMeasuredLength(widthMeasureSpec, true),
	// getMeasuredLength(heightMeasureSpec, false));

	// }

	// }
	
	
}