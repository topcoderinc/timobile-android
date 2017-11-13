package com.topcoder.timobile.utility;

import android.content.Context;
import android.widget.TextView;
import com.topcoder.timobile.baseclasses.BaseApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import timber.log.Timber;

/**
 * Utility methods
 */
public class AppUtils {

  public static String getJSONStringFromRawResource(int rawResId) {
    InputStream is = BaseApplication.getInstance().getResources().openRawResource(rawResId);
    Writer writer = new StringWriter();
    char[] buffer = new char[1024];
    try {
      Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      int n;
      while ((n = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, n);
      }
      is.close();
    } catch (IOException e) {
      Timber.e(e, "error reading json resource file");
    }

    return writer.toString();
  }

  public static String formatPaymentAmount(int amount) {
    if (amount < 1000) {
      return String.valueOf(amount);
    }

    if (amount < 1000000) {
      String format;
      if (amount % 1000 == 0) {
        format = "%.0f";
      } else {
        format = "%.1f";
      }
      return String.format(Locale.getDefault(), format, amount / 1000f) + "k";
    }

    if (amount < 1000000000) {
      String format;
      if (amount % 1000000 == 0) {
        format = "%.0f";
      } else {
        format = "%.1f";
      }
      return String.format(Locale.getDefault(), format, amount / 1000000f) + "M";
    }

    return String.format(Locale.getDefault(), "%.1f", amount / 1000000000f) + "B";
  }

  public static long getTimestampFromDate(String date) {
    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    Date startDate;
    try {
      startDate = formatter.parse(date);
      return startDate.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static int getYear(String date) {
    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    Date startDate;
    try {
      startDate = formatter.parse(date);
      Calendar c = Calendar.getInstance();
      c.setTime(startDate);
      return c.get(Calendar.YEAR);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static String getText(TextView textView){
    return textView.getText().toString().trim();
  }

  public static int dp2px(Context context, int dpVal) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpVal * scale + 0.5f);
  }

  public static int sp2px(Context context, int spVal) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spVal * fontScale + 0.5f);
  }
}
