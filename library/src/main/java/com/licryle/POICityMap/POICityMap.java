package com.licryle.POICityMap;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.licryle.POICityMap.datastructure.City;
import com.licryle.POICityMap.datastructure.CityList;
import com.licryle.POICityMap.datastructure.POI;
import com.licryle.POICityMap.datastructure.POIList;
import com.licryle.POICityMap.helpers.POICityMapListener;
import com.licryle.POICityMap.helpers.POIParser;
import com.licryle.POICityMap.helpers.POIQualifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class POICityMap implements OnMarkerClickListener, OnMapClickListener,
    InfoWindowAdapter, OnMapReadyCallback, ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private GoogleMap _mMap;
  protected GoogleApiClient _mGoogleApiClient = null;

	protected Activity _mContext = null;

  protected boolean	_bDownloading = false;

  protected ArrayList<POICityMapListener> _aListeners;
  protected POIList _mPOIList = null;
  protected CityList _mCityList = new CityList();
  protected POIQualifier _mPOIQualifier = null;
  protected POIParser _mPOIParser = null;
  protected POICityMapSettings _mSettings = null;
  protected int _iCityId = 0;

	public POICityMap(Activity mContext, POICityMapSettings mSettings,
                    POIQualifier mPOIQualifier, POIParser mPOIParser) {
    _aListeners = new ArrayList<POICityMapListener>();
    _mSettings = mSettings;
    _mPOIQualifier = mPOIQualifier;
    _mPOIParser = mPOIParser;
    _mContext = mContext;

    buildGoogleApiClient();
    _mGoogleApiClient.connect();
	}

  protected synchronized void buildGoogleApiClient() {
    _mGoogleApiClient = new GoogleApiClient.Builder(_mContext)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
  }

  /****** Google Play Callbacks ******/
  @Override
  public void onMapReady(GoogleMap mMap) {
    _mMap = mMap;
    _mMap.setInfoWindowAdapter(this);
    _setupMap();
    _dispatchOnMapReady();
  }


  @Override
  public void onConnected(Bundle bundle) {
    LatLng mLastLocation = getLastPosition();

    if (mLastLocation != null) {
      moveCameraTo(mLastLocation, 13);
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

  }

  public LatLng getLastPosition() {
    if (! _mGoogleApiClient.isConnected()) {
      return null;
    }

    Location mLocation = LocationServices.FusedLocationApi.getLastLocation(
        _mGoogleApiClient);

    return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
  }


  public boolean isMapLoaded() { return _mMap != null; }
  public boolean isDownloading() { return _bDownloading; }
  public POIList getPOIList() { return _mPOIList; }
  public CityList getCityList() { return _mCityList; }
  public City getCurrentCity() {
    if (getCityList().containsKey(_iCityId)) {
      return getCityList().get(_iCityId);
    } else {
      return null;
    }
  }

  public void invalidate() {
    _updateMarkers();
  }

  public void changeCityId(int iCityId) {
    _iCityId = iCityId;
  }

  public boolean downloadMarkers() {
    Log.i("POICityMap", "Entered downloadMarkers()");
    if (_bDownloading) return false;
    _bDownloading = true;

    _mSettings.getPOIListFile().mkdirs();

    Log.i("POICityMap", "Starting Intent in downloadMarkers()");
    _mContext.startService(POIListInfoService.buildIntent(
        _mContext,
        new _DownloadPOIListReceiver(new Handler()),
        this,
        _mPOIParser,
        _mSettings.getStaticDeadLine(),
        _mSettings.getDynamicDeadLine(),
        _mSettings.getCityListDeadLine(),
        _mSettings.getPOIListFile(),
        _mSettings.getCityListFile(),
        _mSettings.getURLPOIListFullData(_iCityId),
        _mSettings.getURLPOIListDynamicData(_iCityId),
        _mSettings.getURLCityList(),
        _iCityId
    ));

    return true;
  }

  public void moveCameraOnCity() {
    City mCity = _mCityList.findCityById(_iCityId);

    if (mCity != null) {
      moveCameraTo(mCity.getPosition(), mCity.getZoom());
    }
  }

  public void moveCameraTo(LatLng mPosition, int iZoom) {
    if (mPosition == null) return;

    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(
        new CameraPosition(mPosition, iZoom, 0, 0));

    updateCamera(cu);
  }

  public void updateCamera(CameraUpdate mCameraUpdate) {
    if (!isMapLoaded()) return;

    _mMap.animateCamera(mCameraUpdate, 700, null);
  }

  protected void _updateMarker(POI mPOI, boolean bFavorite) {
    if (!isMapLoaded() || mPOI == null) return;

    MarkerOptions mOpts = new MarkerOptions();
    mOpts.position(mPOI.getPosition());
    mOpts.title(mPOI.getName());

    int id = mPOI.getId();
    mOpts.title(String.valueOf(id));

    mOpts.icon(BitmapDescriptorFactory.fromResource(
        _mPOIQualifier.getIcon(mPOI)));

    _mMap.addMarker(mOpts);
  }

  protected void _updateMarkers() {
    if (!isMapLoaded() || _mPOIList == null) return;
    
    _mMap.clear();

    Iterator<Map.Entry<Integer, POI>> it = _mPOIList.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Integer, POI> entry = it.next();
      POI mPOI = entry.getValue();

      if (! _mPOIQualifier.isFiltered(mPOI))
        _updateMarker(mPOI, false);
    }
  }

  protected boolean _setupMap() {
    if (!isMapLoaded()) return false;

    _mMap.setMyLocationEnabled(true);
    _mMap.setOnMarkerClickListener(this);
    _mMap.setOnMapClickListener(this);
    _mMap.getUiSettings().setMapToolbarEnabled(false);

    return true;
  }

  @Override
  public boolean onMarkerClick(Marker mMarker) {
    if (_mPOIList == null) return false;

    String sPOIId = mMarker.getTitle();
    POI mPOI = _mPOIList.get(Integer.valueOf(sPOIId));

    _dispatchOnPOIClick(mPOI);
    return false;
  }

  @Override
  public void onMapClick(LatLng mLatLng) {
    _dispatchOnMapClick(mLatLng);
  }

  public void registerPOICityMapListener(POICityMapListener mListener) {
    _aListeners.add(mListener);
  }

  protected void _dispatchOnMapReady() {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onMapReady(this);
    }
  }

  protected void _dispatchOnPOIClick(POI mPOI) {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onPOIClick(this, mPOI);
    }
  }

  protected void _dispatchOnMapClick(LatLng mLatLng) {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onMapClick(this, mLatLng);
    }
  }

  protected void _dispatchOnPOIListDownloadFailure() {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onPOIListDownloadFailure(this);
    }
  }

  protected void _dispatchOnPOIListDownloadSuccess() {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onPOIListDownloadSuccess(this);
    }
  }

  protected void _dispatchOnCityDownloadFailure() {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onCityListDownloadFailure(this);
    }
  }

  protected void _dispatchOnCityDownloadSuccess() {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onCityListDownloadSuccess(this);
    }
  }

  protected void _dispatchOnDirectionsFailed() {
    for (POICityMapListener mListener: _aListeners) {
      mListener.onDirectionsFailed(this);
    }
  }

  protected View _dispatchOnGetInfoContents(Marker mMarker, POI mPOI) {
    View mResult = null;
    for (POICityMapListener mListener: _aListeners) {
      mResult = mListener.onGetInfoContents(mMarker, mPOI);
    }

    return mResult;
  }

  public boolean isAnyPOIVisible() {
    Iterator<Map.Entry<Integer, POI>> it = _mPOIList.entrySet().
        iterator();

    while (it.hasNext()) {
      Map.Entry<Integer, POI> entry = it.next();
      POI mPOI = entry.getValue();

      if (_isPOIVisible(mPOI)) return true;
    }

    return false;
  }

  private boolean _isPOIVisible(POI mPOI) {
    if (!isMapLoaded()) return false;

    return _mMap.getProjection().getVisibleRegion().latLngBounds.contains(
        mPOI.getPosition()
    );
  }


  private class _DownloadPOIListReceiver extends ResultReceiver
      implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 5529673172741409950L;

    public _DownloadPOIListReceiver(Handler handler) {
      super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
      super.onReceiveResult(resultCode, resultData);

      switch (resultCode) {
        case POIListInfoService.FAILURE_POILIST_CONNECTION:
        case POIListInfoService.FAILURE_POILIST_GENERIC:
        case POIListInfoService.FAILURE_POILIST_PARSE:
        case POIListInfoService.SUCCESS_POILIST:
          _mPOIList = (POIList) resultData.getSerializable("poilist");

          // we need this to display at least static data
          if (_mPOIList != null) {
            _updateMarkers();
          }

          if (resultCode == POIListInfoService.SUCCESS_POILIST) {
            Log.i("POICityMap", "onReceiveResult() SUCCESS_POILIST_xxxx");
            _dispatchOnPOIListDownloadSuccess();
          } else {
            Log.i("POICityMap", "onReceiveResult() FAILURE_POILIST_xxxx");
            _dispatchOnPOIListDownloadFailure();
          }
        break;

        case POIListInfoService.FAILURE_CITYLIST_CONNECTION:
        case POIListInfoService.FAILURE_CITYLIST_GENERIC:
        case POIListInfoService.FAILURE_CITYLIST_PARSE:
        case POIListInfoService.SUCCESS_CITYLIST:

          _mCityList = (CityList) resultData.getSerializable("citylist");

          if (resultCode == POIListInfoService.SUCCESS_CITYLIST) {
            Log.i("POICityMap", "onReceiveResult() SUCCESS_CITYLIST_xxxx");
            _dispatchOnCityDownloadSuccess();
          } else {
            Log.i("POICityMap", "onReceiveResult() FAILURE_CITYLIST_xxxx");
            _dispatchOnCityDownloadFailure();
          }
        break;

        case POIListInfoService.FINISHED:
          Log.i("POICityMap", "onReceiveResult() FINISHED");
          _bDownloading = false;
        break;
      }
    }
  }

  @Override
  public View getInfoContents(Marker mMarker) {
    if (_mPOIList == null) return null;

    String sPOIId = mMarker.getTitle();
    POI mPOI = _mPOIList.get(Integer.valueOf(sPOIId));

    return _dispatchOnGetInfoContents(mMarker, mPOI);
  }

  @Override
  public View getInfoWindow(Marker mMarker) {
    return null;
  }
}
