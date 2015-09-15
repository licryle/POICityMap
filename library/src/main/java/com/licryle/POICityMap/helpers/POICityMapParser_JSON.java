package com.licryle.POICityMap.helpers;

import android.util.Log;

import com.licryle.POICityMap.datastructure.City;
import com.licryle.POICityMap.datastructure.CityList;
import com.licryle.POICityMap.datastructure.POIList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by licryle on 9/15/15.
 */
public abstract class POICityMapParser_JSON implements POICityMapParser {
  @Override
  public void parsePOIListFullData(ByteArrayOutputStream mInput,
                                   POIList mOutput, City mCity)
      throws Exception {

  }

  @Override
  public void parsePOIListDynamicData(ByteArrayOutputStream mInput,
                                      POIList mOutput, City mCity)
      throws Exception {

  }

  @Override
  public void parseCityList(ByteArrayOutputStream mInput,
                            CityList mOutput)
      throws Exception {
    String sInput = new String(mInput.toByteArray());
    JSONArray mJSon = new JSONArray(sInput);

    for (int i=0; i < mJSon.length(); i++) {
      try {
        City mCity = parseCity(mJSon.getJSONObject(i));
        mOutput.put(mCity.getId(), mCity);
      } catch (JSONException e) {
        Log.i("POIListInfoService", "1 POI rejected, JSON invalid. " +
            e.getMessage());
      }
    }
  }

  public City parseCity(JSONObject mJSonCity) throws JSONException {
    return new City(mJSonCity);
  }
}
