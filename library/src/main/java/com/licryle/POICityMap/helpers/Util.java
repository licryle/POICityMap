package com.licryle.POICityMap.helpers;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public abstract class Util {
  public static int intToUInt(int i, int iPow) {
    long iMax = (Double.valueOf(Math.pow(2, iPow))).longValue();

    return (int) ((i < 0) ? (iMax + i) : i);
  }

  public static LatLng getLastPosition(Context mContext) {
    LocationManager mLocMgr = (LocationManager) mContext.getSystemService(
        Context.LOCATION_SERVICE);

    Criteria mCriteria = new Criteria();
    mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
 
    String sProvider = mLocMgr.getBestProvider(mCriteria, true);
    if (sProvider == null) return null;

    Location mLastLocation = null;
    try {
      mLastLocation = mLocMgr.getLastKnownLocation(sProvider);
    } catch (SecurityException e) {

    }

    if (mLastLocation == null) return null;
    
    return new LatLng(mLastLocation.getLatitude(),
        mLastLocation.getLongitude());
  }
}
