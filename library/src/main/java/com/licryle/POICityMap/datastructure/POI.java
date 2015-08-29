package com.licryle.POICityMap.datastructure;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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

	protected Bundle _mDynamicData;

	public POI(City mCity, JSONObject mStation)
	    throws JSONException {
    _mCity = mCity;
		// TODO restore this list _iId = mStation.getInt("id");
		_iId = mStation.getInt("number");

		_sName = mStation.getString("name");
		_sAddress = mStation.getString("address");

		_dLat = mStation.getJSONObject("position").getDouble("lat");
		_dLng = mStation.getJSONObject("position").getDouble("lng");

		_bStaticOnly = false;
		_mDynamicData = new Bundle();
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

  protected void _updateDynamicData(Bundle mDynamicData) {
		_mDynamicData = mDynamicData;
    _bStaticOnly = mDynamicData == null;
  }

	protected Bundle getDynamicData() {
		return _mDynamicData;
	}
}
