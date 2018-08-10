package com.androidbash.androidbashfirebaseupdated.Utility;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.io.InputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Utils {
    private String TAG = "Utils";

    public int getTimeDifference(String fromMilliSecond, String toMilliSecond) {
        String localCreatedDate = new SimpleDateFormat("dd-M-yyyy HH:mm:ss aa").format(new Date(Long.parseLong(toMilliSecond)));
        String localExpireDate = new SimpleDateFormat("dd-M-yyyy HH:mm:ss aa").format(new Date(Long.parseLong(fromMilliSecond)));
        Log.i(TAG, "get_time_difference: localCreatedDate " + localCreatedDate);
        Log.i(TAG, "get_time_difference: localExpireDate " + localExpireDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy HH:mm:ss aa");

        Date Created_convertedDate = null, Expire_CovertedDate = null, todayWithZeroTime = null;
        try {
            Created_convertedDate = dateFormat.parse(localCreatedDate);
            Expire_CovertedDate = dateFormat.parse(localExpireDate);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeDiff = Expire_CovertedDate.getTime() - Created_convertedDate.getTime();
        return (int) TimeUnit.MINUTES.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    public Calendar parseStringDateToCalendar(String inputString, String formatPattern) {
        // EEE MMM dd HH:mm:ss z yyyy
        // Mon Mar 14 16:02:37 GMT 2011


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatPattern, Locale.US);
        try {
            calendar.setTime(simpleDateFormat.parse(inputString));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public String parseDateFromMilliseconds(long milliSecond, String formatPattern) {
        //dd-M-yyyy hh:mm:ss

        String localString = new SimpleDateFormat(formatPattern, Locale.getDefault()).format(new Date(milliSecond));
        return localString;
    }

    public long parseMillisFromString(String formatPattern) {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        try {
            Date mDate = sdf.parse(formatPattern);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public String parseJsonFile(Context mContext, String fileName) {
        try {
            InputStream inputStream = mContext.getResources().getAssets().open(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (Exception e) {
            Log.e(TAG, "parseJsonFile: e " + e);
            return null;
        }
    }

    public List parseMapKeysList(Map<String, Object> map) {
        return new ArrayList<>(map.keySet());
    }

    public boolean isMyServiceRunning(Context mContext, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    public long getCalendarDifference(Calendar mFirstCalendar, Calendar mSecondCalendar, int mTimeUnit) {
        //0 = Seconds
        //1 = Minutes
        //2 = Hours
        //3 = Days

        switch (mTimeUnit) {
            case 0: {
                return TimeUnit.MILLISECONDS.toSeconds(mFirstCalendar.getTimeInMillis() - mSecondCalendar.getTimeInMillis());
            }
            case 1: {
                return TimeUnit.MILLISECONDS.toMinutes(mFirstCalendar.getTimeInMillis() - mSecondCalendar.getTimeInMillis());
            }
            case 2: {
                return TimeUnit.MILLISECONDS.toHours(mFirstCalendar.getTimeInMillis() - mSecondCalendar.getTimeInMillis());
            }
            case 3: {
                return TimeUnit.MILLISECONDS.toDays(mFirstCalendar.getTimeInMillis() - mSecondCalendar.getTimeInMillis());
            }
        }
        return 0;
    }

    public void printHashKey(Context pContext) {
        try {
            Log.e(TAG, "printHashKey: pContext.getPackageName() " + pContext.getPackageName());

            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }
}


