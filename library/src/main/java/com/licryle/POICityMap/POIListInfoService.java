package com.licryle.POICityMap;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.licryle.POICityMap.datastructure.City;
import com.licryle.POICityMap.datastructure.CityList;
import com.licryle.POICityMap.datastructure.POIList;
import com.licryle.POICityMap.helpers.POICityMapParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Hashtable;

public class POIListInfoService extends IntentService {
  public static final int UPDATE_PROGRESS = 8344;
  public static final int SUCCESS_POILIST = 8345;
  public static final int FAILURE_POILIST_CONNECTION = 8346;
  public static final int FAILURE_POILIST_GENERIC = 8347;
  public static final int FAILURE_POILIST_PARSE = 8348;
  public static final int FAILURE_WRONG_CONTRACT = 8354;
  public static final int FINISHED = 8349;
  public static final int SUCCESS_CITYLIST = 8350;
  public static final int FAILURE_CITYLIST_CONNECTION = 8351;
  public static final int FAILURE_CITYLIST_GENERIC = 8352;
  public static final int FAILURE_CITYLIST_PARSE = 8353;

  protected Hashtable<String, ResultReceiver> _mRequesters;
  protected POIList _mPOIList;
  protected CityList _mCityList;
  protected POICityMapParser _mPOICityMapParser;

  protected POICityMapSettings _mSettings;
  protected int _iCity;

  public POIListInfoService() {
    super("POIListInfoService");
    Log.i("POIListInfoService", "Entering Constructor()");

    _mRequesters = new Hashtable<String, ResultReceiver>();
  }

  protected synchronized boolean _isConcurrent(ResultReceiver mReceiver,
                                               String sRequestor) {
    if (_mRequesters.containsKey(sRequestor)) return true;

    _mRequesters.put(sRequestor, mReceiver);

    // we do not queue the Intent, we just register the requester
    return (_mRequesters.size() > 1);
  }

  public int onStartCommand (Intent intent, int flags, int startId) {
    if (intent != null) {
      ResultReceiver mReceiver = (ResultReceiver)
          intent.getParcelableExtra("receiver");
      String sRequestor = intent.getStringExtra("requestor");

      if (_isConcurrent(mReceiver, sRequestor)) return 0;
    }

    return super.onStartCommand(intent, flags, startId);
  }

  protected void _loadPOIList(File mPOIListFile, int iDlDynamic) {
    if (_mPOIList == null) {
      _mPOIList = POIList.loadFromFile(mPOIListFile, iDlDynamic);
    }
  }

  protected void _loadCityList(File mCityListFile) {
    if (_mCityList == null) {
      _mCityList = CityList.loadFromFile(mCityListFile);
    }
  }


  private int _downloadCityList(String sUrlCityList) {
    Log.i("POIListInfoService", "Entering _downloadCityList()");
    try {
      ByteArrayOutputStream mOutput = _downloadData(sUrlCityList);

      try {
        _parseCityListData(mOutput);
      } catch (Exception e) {
        Log.i("POIListInfoService", "Error in parsing CityList");
      }

      mOutput.close();
    } catch (ConnectException e) {
      e.printStackTrace();
      return FAILURE_CITYLIST_CONNECTION;
    } catch (IOException e) {
      e.printStackTrace();
      return FAILURE_CITYLIST_GENERIC;
    } catch (Exception e) {
      e.printStackTrace();
      return FAILURE_CITYLIST_PARSE;
    }
    return SUCCESS_CITYLIST;
  }

  protected int _downloadPOIList(boolean bFullCycle, City mCity,
                                 String sURLPOIListFull,
                                 String sURLPOIListDynamic) {
    Log.i("POIListInfoService", "Entering _downloadPOIList()");
    try {
      String sUrl = bFullCycle ? sURLPOIListFull : sURLPOIListDynamic;

      ByteArrayOutputStream mOutput = _downloadData(sUrl);

      if (bFullCycle) {
        _parsePOIListFullData(mOutput, _mPOIList, mCity);
      } else {
        _parsePOIListDynamicData(mOutput, _mPOIList, mCity);
      }
      mOutput.close();

      return SUCCESS_POILIST;
    } catch (ConnectException e) {
      e.printStackTrace();
      return FAILURE_POILIST_CONNECTION;
    } catch (IOException e) {
      e.printStackTrace();
      return FAILURE_POILIST_GENERIC;
    } catch (Exception e) {
      Log.i("POIListInfoService", "Error in data parsing " + e.getMessage());
      e.printStackTrace();
      return FAILURE_POILIST_PARSE;
    }
  }

  protected ByteArrayOutputStream _downloadData(String sUrl)
      throws IOException {
    Log.i("POIListInfoService", "Entering _downloadData()");

    URL mUrl = new URL(sUrl);
    URLConnection mConnection = mUrl.openConnection();
    mConnection.connect();

    // For progress report
    int fileLength = mConnection.getContentLength();

    // download the file
    InputStream mInput = new BufferedInputStream(mUrl.openStream());
    ByteArrayOutputStream mOutput = new ByteArrayOutputStream();

    byte aData[] = new byte[1024];
    long lTotal = 0;
    int iCount;
    while ((iCount = mInput.read(aData)) != -1) {
      lTotal += iCount;
      // publishing the progress....
      Bundle mResultData = new Bundle();

      if (fileLength > 0) {
        mResultData.putInt("progress", (int) (lTotal * 100 / fileLength));
        _dispatchResults(UPDATE_PROGRESS, mResultData);
      }

      mOutput.write(aData, 0, iCount);
    }

    mOutput.flush();
    mInput.close();

    Log.i("POIListInfoService", "Leaving _downloadData()");
    return mOutput;
  }

  protected boolean _updateCityList(int iDlCityList, String sCityListUrl) {
    Log.i("POIListInfoService", "Entering _handleCityList()");
    boolean bExpiredCityList = _mCityList.isStaticExpired(iDlCityList);

    int iSignal = (_mCityList != null && _mCityList.size() > 0) ?
        SUCCESS_CITYLIST : FAILURE_CITYLIST_GENERIC;

    if (bExpiredCityList || _mCityList.size() == 0) {
      // Download citylist if they are expired
      // Send result to process asking, write after, all in background
      iSignal = _downloadCityList(sCityListUrl);
    }

    Bundle mBundle = new Bundle();
    mBundle.putSerializable("citylist", _mCityList);
    _dispatchResults(iSignal, mBundle);

    Log.i("POIListInfoService", "Leaving _handleCityList()");
    return bExpiredCityList && iSignal == SUCCESS_CITYLIST;
  }

  protected boolean _updatePOIList(int iDlStatic, int iDlDynamic,
                                   int iCity, String sURLPOIListFull,
                                   String sURLPOIListDynamic) {
    Log.i("POIListInfoService", "Entering _handlePOIList()");
    boolean bStaticExpired = _mPOIList.isStaticExpired(iDlStatic);
    boolean bDynamicExpired = _mPOIList.isDynamicExpired(iDlDynamic);

    // check if we changed city
    boolean bDiffCity = false;
    try {
      bDiffCity = (_mPOIList.size() == 0) ||
          (iCity != _mPOIList.entrySet().iterator().
              next().getValue().getCity().getId());
    } catch (Exception e) {
      bDiffCity = true;
    }

    // If POIs data is invalidated
    int iSignal = (_mPOIList != null && _mPOIList.size() > 0) ?
        SUCCESS_POILIST : FAILURE_POILIST_GENERIC;
    if (bStaticExpired || bDynamicExpired || bDiffCity) {
      if (_mCityList == null) {
        iSignal = FAILURE_WRONG_CONTRACT;
      } else {
        City mCity = _mCityList.findCityById(iCity);
        if (mCity == null) {
          iSignal = FAILURE_WRONG_CONTRACT;
        } else {
          iSignal = _downloadPOIList(bStaticExpired || bDiffCity, mCity,
              sURLPOIListFull, sURLPOIListDynamic);
        }
      }
    } else {
      iSignal = SUCCESS_POILIST;
    }

    if ( iSignal != SUCCESS_POILIST && _mPOIList != null) {
      // We send static info for reference
      _mPOIList.removeDynamicData();
    }

    // Send result to process asking, write after, all in background
    Bundle mBundle = new Bundle();
    mBundle.putSerializable("poilist", _mPOIList);
    _dispatchResults(iSignal, mBundle);

    Log.i("POIListInfoService", "Leaving _handlePOIList()");
    return (bStaticExpired || bDynamicExpired || bDiffCity) &&
        iSignal == SUCCESS_POILIST;
  }

  public static Intent buildIntent(Context mContext,
                                   ResultReceiver mReceiver,
                                   Object mRequestor,
                                   POICityMapSettings mSettings,
                                   POICityMapParser mParser,
                                   int iCityId) {
    Intent mIntent = new Intent(mContext, POIListInfoService.class);

    mIntent.putExtra("receiver", (Parcelable) mReceiver);
    mIntent.putExtra("requestor", mRequestor.toString());
    mIntent.putExtra("settings", mSettings);
    mIntent.putExtra("poi_parser", mParser.getClass().getName());

    mIntent.putExtra("city_id", iCityId);

    return mIntent;
  }

  protected void _parseIntent(Intent mIntent) {
    _mSettings = (POICityMapSettings) mIntent.getSerializableExtra("settings");
    String sParserClassName = mIntent.getStringExtra("poi_parser");
    _iCity = mIntent.getIntExtra("city_id", 0);

    try {
      Class mClass = Class.forName(sParserClassName);
      _mPOICityMapParser = (POICityMapParser) mClass.newInstance();
    } catch (Exception e) {
      // should not happen since the instanciation should come from an object.
    }
  }

  @Override
  protected void onHandleIntent(Intent mIntent) {
    Log.i("POIListInfoService", "Entering onHandleIntent()");

    if (_mRequesters.size() == 0) return;

    _parseIntent(mIntent);

    File mPOIListFile = _mSettings.getPOIListFile();
    File mCityListFile = _mSettings.getCityListFile();

    _loadCityList(mCityListFile);
    _loadPOIList(mPOIListFile, _mSettings.getDynamicDeadLine());

    if (_updateCityList(_mSettings.getCityListDeadLine(),
        _mSettings.getURLCityList()))
      _mCityList.saveToFile(mCityListFile);

    if (_iCity != 0 && _updatePOIList(_mSettings.getStaticDeadLine(),
        _mSettings.getDynamicDeadLine(), _iCity,
        _mSettings.getURLPOIListFullData(_iCity),
        _mSettings.getURLPOIListDynamicData(_iCity)))
      _mPOIList.saveToFile(mPOIListFile);

    Bundle mBundle = new Bundle();
    _dispatchResults(FINISHED, mBundle);

    _mRequesters.clear();
    Log.i("POIListInfoService", "Leaving onHandleIntent()");
  }

  private void _parseCityListData(ByteArrayOutputStream mInput)
      throws Exception {
    Log.i("POIListInfoService", "Entering _parseCityListData()");

    CityList mNewCityList = new CityList();
    _mPOICityMapParser.parseCityList(mInput, mNewCityList);

    mNewCityList.setLastUpdate(Calendar.getInstance().getTime());

    _mCityList = mNewCityList;

    Log.i("POIListInfoService", "Leaving _parseCityListData()");
  }

  protected void _parsePOIListFullData(ByteArrayOutputStream mInput,
                                       POIList mOutput,
                                       City mCity)
      throws Exception {
    Log.i("POIListInfoService", "Entering _parsePOIListFullData()");

    _mPOICityMapParser.parsePOIListFullData(mInput, mOutput, mCity);
    mOutput.setLastUpdate(Calendar.getInstance().getTime());

    Log.i("POIListInfoService", "Leaving _parsePOIListFullData()");
  }

  protected void _parsePOIListDynamicData(ByteArrayOutputStream mInput,
                                          POIList mOutput,
                                          City mCity)
      throws Exception {
    Log.i("POIListInfoService", "Entering _parsePOIListDynamicData()");

    _mPOICityMapParser.parsePOIListDynamicData(mInput, mOutput, mCity);
    mOutput.setLastUpdate(Calendar.getInstance().getTime());

    Log.i("POIListInfoService", "Leaving _parsePOIListDynamicData()");
  }

  protected void _dispatchResults(int iSignal, Bundle mBundle) {
    for(ResultReceiver mReceiver : _mRequesters.values()) {
      mReceiver.send(iSignal, mBundle);
    }
  }
}
