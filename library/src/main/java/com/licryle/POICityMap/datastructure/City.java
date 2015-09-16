package com.licryle.POICityMap.datastructure;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;

public class City implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -3698242294551745027L;

  protected final int _iId;
  protected final String _sInternalName;
  protected final String _sDisplayName;
  protected Double _dLng;
  protected Double _dLat;
  protected final int _iZoom;

  public class CityNameComparator implements Comparator<City> {
      @Override
      public int compare(City lhs, City rhs) {
        return lhs.getDisplayName().compareToIgnoreCase(rhs.getDisplayName());
      }
  }

  public City(JSONObject mContract)
      throws JSONException {
    _iId = mContract.getInt("cityid");
    _sInternalName = mContract.getString("internalname");
    _sDisplayName = mContract.getString("displayname");

    _dLat = mContract.getDouble("latitude");
    _dLng = mContract.getDouble("longitude");

    _iZoom = mContract.getInt("zoom");
  }

  public int getId() { return _iId; }
  public String getInternalName() { return _sInternalName; }
  public String getDisplayName() { return _sDisplayName; }
  public LatLng getPosition() { return new LatLng(_dLat, _dLng); }
  public int getZoom() { return _iZoom; }
}