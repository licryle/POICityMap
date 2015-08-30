package com.licryle.POICityMap.helpers;

import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.licryle.POICityMap.POICityMap;
import com.licryle.POICityMap.datastructure.POI;

public interface POICityMapListener {
  void onMapReady(POICityMap mPOICityMap);

  void onPOIListDownloadFailure(POICityMap mPOICityMap);
  void onPOIListDownloadSuccess(POICityMap mPOICityMap);

  void onCityListDownloadFailure(POICityMap mPOICityMap);
  void onCityListDownloadSuccess(POICityMap mPOICityMap);

  void onPOIClick(POICityMap mPOICityMap, POI mPOI);
  void onMapClick(POICityMap POICityMap, LatLng mLatLng);

  View onGetInfoContents(Marker mMarker, POI mPOI);

  void onDirectionsFailed(POICityMap POICityMap);
}
