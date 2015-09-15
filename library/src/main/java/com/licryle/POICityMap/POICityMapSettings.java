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


  public POICityMapSettings() {
  }

  public int getStaticDeadLine() { return _iStaticDeadline; }
  public void setStaticDeadLine(int iStaticDeadline) { _iStaticDeadline = iStaticDeadline; }

  public int getDynamicDeadLine() { return _iDynamicDeadline; }
  public void setDynamicDeadLine(int iDynamicDeadline) { _iDynamicDeadline = iDynamicDeadline; }

  public int getCityListDeadLine() { return _iCityListDeadline; }
  public void setCityListDeadLine(int iCityListDeadline) { _iCityListDeadline = iCityListDeadline; }


  public void setURLBase(String sURL) { _sURLBase = sURL; }
  public void setAppName(String sAppName) { _sAppName = sAppName; }

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
}