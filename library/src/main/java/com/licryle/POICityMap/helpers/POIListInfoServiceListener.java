package com.licryle.POICityMap.helpers;

import com.licryle.POICityMap.datastructure.CityList;
import com.licryle.POICityMap.datastructure.POIList;

/**
 * Created by licryle on 11/3/15.
 */
public interface POIListInfoServiceListener {
  void onPOIListDownloadFailure(POIList mPOIList, int iResultCode);
  void onPOIListDownloadSuccess(POIList mPOIList);

  void onCityListDownloadFailure(CityList mCityList, int iResultCode);
  void onCityListDownloadSuccess(CityList mCityList);

  void onFinished();
}
