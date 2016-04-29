package com.yanix.eleshopcart;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;

import java.io.IOException;
import java.io.InputStream;

/**
 * 系统工具类
 * 
 * @author yanix 2015/3/27
 */
public class Tools {
	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int dp2px(Context context, float dipValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
	}
}
