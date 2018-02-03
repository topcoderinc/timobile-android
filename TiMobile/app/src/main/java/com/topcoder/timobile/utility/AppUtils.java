package com.topcoder.timobile.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.topcoder.timobile.baseclasses.BaseApplication;
import com.topcoder.timobile.model.Racetrack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
import retrofit2.HttpException;
import timber.log.Timber;

/**
 * Utility methods
 */
public class AppUtils {


  private static AmazonS3Client sS3Client;
  private static TransferUtility sTransferUtility;

  /**
   * the current location
   */
  @Getter @Setter private static Location currentLocation = null;

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


  /**
   * zip http request
   *
   * @param observable the request
   */
  public static <T> Observable<List<T>> scheduleList(Observable<List<T>> observable) {
    return observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).onErrorReturn(throwable -> new ArrayList<T>());
  }

  /**
   * convert json string to list
   *
   * @param jsonValue  the array json value
   * @param arrayClass the array type
   * @param <T>        the entity class
   * @return the list
   */
  public static <T> List<T> jsonStringToList(String jsonValue, Type arrayClass) {
    List<T> items = new ArrayList<>();
    T[] itemsArray = new Gson().fromJson(jsonValue, arrayClass);
    if (itemsArray != null) {
      items = Arrays.asList(itemsArray);
    }
    return items;
  }

  /**
   * get global shared Preferences
   *
   * @return the SharedPreferences
   */
  public static SharedPreferences getPreferences() {
    return BaseApplication.getInstance().getSharedPreferences("ti-mobile", Context.MODE_PRIVATE);
  }

  /**
   * get error message from backend error
   *
   * @param throwable the exception from backend.
   * @return the error msg
   */
  public static String getErrorMessage(Throwable throwable) {
    HttpException error;
    try {
      error = (HttpException) throwable;
    } catch (Exception e) {
      return throwable.getMessage();
    }
    if (error == null) {
      return null;
    }
    try {
      String jsonString = error.response().errorBody().string();
      JsonParser parser = new JsonParser();
      JsonObject o = parser.parse(jsonString).getAsJsonObject();
      try {
        JsonArray array = o.get("message").getAsJsonArray();
        return array.get(0).getAsJsonObject().get("message").getAsString();
      } catch (Exception e) {
        String message = o.get("message").toString().replace("\"", "").replace("\\", "");
        String[] messages = message.split(":");
        if (messages.length > 1) {
          return messages[1];
        }
        return message;
      }
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * show error msg
   *
   * @param throwable the exception
   * @param msg       the msg
   */
  public static void showError(Throwable throwable, String msg) {
    String backendMsg = getErrorMessage(throwable);
    String destMsg = backendMsg == null ? msg : backendMsg;
    ToastUtils.showShort(destMsg);
  }


  /**
   * hide keyboard when click outside keyboard
   *
   * @param ev       the MotionEvent
   * @param activity the activity
   */
  public static void hideKeyBoardWhenClickOther(MotionEvent ev, Activity activity) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      View v = activity.getCurrentFocus();
      if (isShouldHideKeyboard(v, ev)) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }
  }

  /**
   * hide keyboard
   *
   * @param v     the view
   * @param event the click event
   * @return is should hide keyboard
   */
  public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
    if (v != null && (v instanceof EditText)) {
      int[] l = {0, 0};
      v.getLocationInWindow(l);
      int left = l[0],
          top = l[1],
          bottom = top + v.getHeight(),
          right = left + v.getWidth();
      return !(event.getX() > left && event.getX() < right
          && event.getY() > top && event.getY() < bottom);
    }
    return false;
  }


  /**
   * Gets an instance of a S3 client which is constructed using the given
   * Context.
   *
   * @return A default S3 client.
   */
  public static AmazonS3Client getS3Client() {
    if (sS3Client == null) {
      sS3Client = new AmazonS3Client(new BasicAWSCredentials(AppConstants.AWS_KEY, AppConstants.AWS_SECRET));
      sS3Client.setRegion(Region.getRegion(AppConstants.AWS_S3_REGION));
    }
    return sS3Client;
  }

  /**
   * get distance between racetrack to current
   *
   * @param racetrack the racetrack
   * @return the format distance
   */
  public static String getDistance(Racetrack racetrack) {
    if (racetrack == null) {
      return "N/A mile";
    }
    Float lat = racetrack.getLocationLat();
    Float lng = racetrack.getLocationLng();
    if (lat == null || lng == null) {
      return "N/A mile";
    }
    if (AppUtils.getCurrentLocation() == null) {
      return "N/A mile";
    }
    Location location = new Location(LocationManager.GPS_PROVIDER);
    location.setLatitude(lat);
    location.setLongitude(lng);
    float distance = AppUtils.getCurrentLocation().distanceTo(location) * 0.000621371f; // to miles
    String suffix = "";
    if (distance > 1000) {
      distance = distance / 1000;
      suffix = "k";
    }
    return new DecimalFormat("#.#").format(distance) + suffix + " miles";
  }

  /**
   * Gets an instance of the TransferUtility which is constructed using the
   * given Context
   *
   * @param context
   * @return a TransferUtility instance
   */
  public static TransferUtility getTransferUtility(Context context) {
    if (sTransferUtility == null) {
      sTransferUtility = TransferUtility.builder()
          .s3Client(getS3Client()).context(context.getApplicationContext()).build();
    }
    return sTransferUtility;
  }

  /**
   * This method converts dp unit to equivalent pixels, depending on device density.
   *
   * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
   * @param context Context to get resources and device specific display metrics
   * @return A float value to represent px equivalent to dp depending on device density
   */
  public static float convertDpToPixel(float dp, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return px;
  }

  public static String getText(TextView textView) {
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
