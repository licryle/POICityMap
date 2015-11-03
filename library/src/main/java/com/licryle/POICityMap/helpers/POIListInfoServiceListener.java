package com.licryle.POICityMap.helpers;

import com.licryle.POICityMap.datastructure.CityList;
import com.licryle.POICityMap.datastructure.POIList;

/**
 * Created by licryle on 11/3/15.
 */
public interface POIListInfoServiceListener {
  void onPOIListDownloadFailure(POIList mPOIList);
  void onPOIListDownloadSuccess(POIList mPOIList);

  void onCityListDownloadFailure(CityList mCityList);
  void onCityListDownloadSuccess(CityList mCityList);

  void onFinished();
}
