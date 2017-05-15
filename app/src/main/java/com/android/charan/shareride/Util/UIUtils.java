package com.android.charan.shareride.Util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class UIUtils {

	public static SpannableStringBuilder buildSpannableStringWithAsterisk(String text) {
		
		SpannableStringBuilder builder = new SpannableStringBuilder();

		String colored = "*";
		builder.append(text);
		int start = builder.length();
		builder.append(colored);
		int end = builder.length();

		builder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		return builder;
	}
}
