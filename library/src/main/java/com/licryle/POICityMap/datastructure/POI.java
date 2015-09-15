package com.licryle.POICityMap.datastructure;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

@SuppressLint("DefaultLocale")
public class POI implements Serializable {
  private static final long serialVersionUID = -967068502674805726L;

	// Static data
	protected int _iId;
	protected City _mCity;

	protected String _sName;
	protected String _sAddress;
	protected Double _dLng;
	protected Double _dLat;

	// Shallow: only contains dynamic data
  protected boolean _bStaticOnly;

	protected HashMap<String, Object> _mDynamicData;

	public POI(City mCity, JSONObject mStation)
	    throws JSONException {
    _mCity = mCity;
		_iId = mStation.getInt("poiId");

		_sName = mStation.getString("name");
		_sAddress = mStation.getString("address");

		_dLat = mStation.getDouble("latitude");
		_dLng = mStation.getDouble("longitude");

		_bStaticOnly = false;
		_mDynamicData = new HashMap<>();
	}

	public POI(POI mOriginal) {
		_bStaticOnly = mOriginal.isStaticOnly();

		_mCity = mOriginal.getCity();
		_iId = mOriginal.getId();

		_sName = mOriginal.getName();
		_sAddress = mOriginal.getAddress();

		_dLat = mOriginal.getPosition().latitude;
		_dLng = mOriginal.getPosition().longitude;
	}

	/*public POI(int iId, int iAvBikes, int iAvBikeStands, boolean bOpened) {
		_iId = iId;
		_mCity = CityList.findCityById(iId / 1000000);
		_iNumber = iId - _mCity.getId() * 1000000;

		_bOpened = bOpened;
		_iAvBikes = iAvBikes;
		_iAvBikeStands = iAvBikeStands;

		_bShallow = true;
		_bStaticOnly = false;
	}*/

	public City getCity() { return _mCity; }
	public int getId() { return _iId; }
	public String getName() { return _sName; }
	public String getFriendlyName() {
	  String sTitle = this.getName();

	  sTitle = sTitle.replaceAll("[0-9]+ - (.*)", "$1").toLowerCase();
    sTitle = Character.toUpperCase(sTitle.charAt(0)) + sTitle.substring(1);

    return sTitle;
	}
	public String getAddress() { return _sAddress; }
	public LatLng getPosition() { return new LatLng(_dLat, _dLng); }

  public boolean isStaticOnly() { return _bStaticOnly; }

  public void removeDynamicData() {
		_mDynamicData.clear();
    _bStaticOnly = true;
  }

  protected void _updateDynamicData(HashMap<String, Object> mDynamicData) {
		_mDynamicData = new HashMap<String, Object>(mDynamicData);
    _bStaticOnly = mDynamicData == null;
  }

	protected HashMap<String, Object> getDynamicData() {
		return _mDynamicData;
	}
}
