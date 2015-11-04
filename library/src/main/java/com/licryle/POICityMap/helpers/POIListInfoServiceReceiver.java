package com.licryle.POICityMap.helpers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.licryle.POICityMap.POIListInfoService;
import com.licryle.POICityMap.datastructure.CityList;
import com.licryle.POICityMap.datastructure.POIList;

import java.io.Serializable;

/**
 * Created by licryle on 11/3/15.
 */
public class POIListInfoServiceReceiver extends ResultReceiver
    implements Serializable {
  protected POIListInfoServiceListener _mListener = null;
  /**
   *
   */
  private static final long serialVersionUID = 5529673172741409950L;

  public POIListInfoServiceReceiver(Handler handler,
                                    POIListInfoServiceListener mListener) {
    super(handler);
    _mListener = mListener;
  }

  @Override
  protected void onReceiveResult(int resultCode, Bundle resultData) {
    super.onReceiveResult(resultCode, resultData);

    switch (resultCode) {
      case POIListInfoService.FAILURE_POILIST_CONNECTION:
      case POIListInfoService.FAILURE_POILIST_GENERIC:
      case POIListInfoService.FAILURE_POILIST_PARSE:
      case POIListInfoService.SUCCESS_POILIST:
        POIList mPOIList = (POIList) resultData.getSerializable("poilist");

        if (resultCode == POIListInfoService.SUCCESS_POILIST) {
          Log.i("POIListInfoServiceRecei",
              "onReceiveResult() SUCCESS_POILIST_xxxx");
          _mListener.onPOIListDownloadSuccess(mPOIList);
        } else {
          Log.i("POIListInfoServiceRecei",
              "onReceiveResult() FAILURE_POILIST_xxxx");
          _mListener.onPOIListDownloadFailure(mPOIList, resultCode);
        }
        break;

      case POIListInfoService.FAILURE_CITYLIST_CONNECTION:
      case POIListInfoService.FAILURE_CITYLIST_GENERIC:
      case POIListInfoService.FAILURE_CITYLIST_PARSE:
      case POIListInfoService.SUCCESS_CITYLIST:

        CityList mCityList = (CityList) resultData.getSerializable("citylist");

        if (resultCode == POIListInfoService.SUCCESS_CITYLIST) {
          Log.i("POIListInfoServiceRecei",
              "onReceiveResult() SUCCESS_CITYLIST_xxxx");
          _mListener.onCityListDownloadSuccess(mCityList);
        } else {
          Log.i("POIListInfoServiceRecei",
              "onReceiveResult() FAILURE_CITYLIST_xxxx");
          _mListener.onCityListDownloadFailure(mCityList, resultCode);
        }
        break;

      case POIListInfoService.FINISHED:
        Log.i("POIListInfoServiceRecei", "onReceiveResult() FINISHED");
        _mListener.onFinished();
        break;
    }
  }
}
