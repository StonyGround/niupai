package com.xiuxiu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class TagsViewGroup extends ViewGroup
{
	private final static int VIEW_WIDTH = 20;
	private final static int VIEW_HEIGHT = 30;
	
	public TagsViewGroup(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
        final int count = getChildCount();
    	for (int index = 0; index < count; index++) 
    	{
			final View child = getChildAt(index);
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		}
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) 
	{
		final int count = getChildCount();
		int row = 0;// which row lay you view relative to parent
		int lengthX = arg1; // right position of child relative to parent
		int lengthY = arg2; // bottom position of child relative to parent

		for (int i = 0; i < count; i++) 
		{
 			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			lengthX += width + VIEW_WIDTH;
			lengthY = row*(height+VIEW_HEIGHT) + VIEW_HEIGHT + height + arg2;
			
			if (row == 0)
			{
				lengthY =  VIEW_HEIGHT + arg2 ;
			}
			else
			{
				lengthY = row * (height + VIEW_HEIGHT) + VIEW_HEIGHT + height;
			}
			
			if (lengthX > arg3) 
			{
				lengthX = width + VIEW_WIDTH + arg1;
				row++;
				
				if (row > 0)
				{
					lengthY = row * (height + VIEW_HEIGHT) + VIEW_HEIGHT + height;
				}
				else
				{
					lengthY = row * (height + VIEW_HEIGHT) + VIEW_HEIGHT + height + arg2;
				}
			}
			
			child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
			
		/*	if (lengthX > arg3) 
			{
				lengthX=width+VIEW_MARGIN+arg1;
				row++;
				lengthY=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height+arg2;
			}*/
			
		//	child.layout(lengthX-width, lengthY-height, lengthX, lengthY);
		}
	}
}
