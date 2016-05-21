package com.licryle.POICityMap;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.io.Serializable;

public class POICityMapSettings implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 7918647910557505183L;

  protected int _iStaticDeadline = 7;
  protected int _iDynamicDeadline = 3;
  protected int _iCityListDeadline = 10;

  protected String _sURLBase = "";
  protected String _sUrlCityList = "";
  protected String _sUrlPOIList = "?c=%d&f=%d";

  protected String _sAppName = "";


  protected float _fDefaultZoom = 13;

  protected boolean _bDisplayCompass = false;
  protected boolean _bDisplayLocation = false;
  protected boolean _bDisplayZoom = false;
  protected boolean _bDisplayToolBar = false;
  protected boolean _bDisplayIndoor = false;

  public POICityMapSettings() {
  }

  public int getStaticDeadLine() { return _iStaticDeadline; }
  public void setStaticDeadLine(int iStaticDeadline) {
    _iStaticDeadline = iStaticDeadline;
  }

  public int getDynamicDeadLine() { return _iDynamicDeadline; }
  public void setDynamicDeadLine(int iDynamicDeadline) {
    _iDynamicDeadline = iDynamicDeadline;
  }

  public int getCityListDeadLine() { return _iCityListDeadline; }
  public void setCityListDeadLine(int iCityListDeadline) {
    _iCityListDeadline = iCityListDeadline;
  }


  public void setURLBase(String sURL) { _sURLBase = sURL; }
  public void setAppName(String sAppName) { _sAppName = sAppName; }

  public float getDefaultZoom() { return _fDefaultZoom; }
  public void setDefaultZoom(float fZoom) { _fDefaultZoom = fZoom; }

  @SuppressLint("DefaultLocale")
  public String getURLPOIListDynamicData(int iCityId) {
    return String.format(_sURLBase + _sUrlPOIList, iCityId, 0);
  }

  @SuppressLint("DefaultLocale")
  public String getURLPOIListFullData(int iCityId) {
      return String.format(_sURLBase + _sUrlPOIList, iCityId, 1);
  }

  public String getURLCityList() {
    return _sURLBase + _sUrlCityList;
  }

  protected File getMapPath() {
    return new File(
        String.format("%s/%s/%s/",
            Environment.getExternalStorageDirectory().getPath(),
            _sAppName,
            "POICityMapSettings"
        )
    );
  }

  public File getPOIListFile() {
    return new File(getMapPath().getAbsolutePath() + "/POIlist");
  }

  public File getCityListFile() {
    return new File(getMapPath().getAbsolutePath() + "/CityList");
  }

  public boolean getDisplayCompass() { return _bDisplayCompass; }
  public void setDisplayCompass(boolean bDisplayCompass) {
    _bDisplayCompass = bDisplayCompass;
  }

  public boolean getDisplayLocation() { return _bDisplayLocation; }
  public void setDisplayLocation(boolean bDisplayLocation) {
    _bDisplayLocation = bDisplayLocation;
  }

  public boolean getDisplayZoom() { return _bDisplayZoom; }
  public void setDisplayZoom(boolean bDisplayZoom) {
    _bDisplayZoom = bDisplayZoom;
  }

  public boolean getDisplayToolBar() { return _bDisplayToolBar; }
  public void setDisplayToolBar(boolean mDisplayToolBar) {
    _bDisplayToolBar = mDisplayToolBar;
  }

  public boolean getDisplayIndoor() { return _bDisplayIndoor; }
  public void setDisplayIndoor(boolean mDisplayIndoor) {
    _bDisplayIndoor = mDisplayIndoor;
  }
}