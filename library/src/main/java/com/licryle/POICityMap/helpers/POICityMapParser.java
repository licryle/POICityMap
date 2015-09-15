package com.licryle.POICityMap.helpers;

import com.licryle.POICityMap.datastructure.City;
import com.licryle.POICityMap.datastructure.CityList;
import com.licryle.POICityMap.datastructure.POIList;

import java.io.ByteArrayOutputStream;

/**
 * Created by licryle on 8/23/15.
 */
public interface POICityMapParser {
  public void parsePOIListFullData(ByteArrayOutputStream mInput,
                                      POIList mOutput, City mCity)
      throws Exception;

  public void parsePOIListDynamicData(ByteArrayOutputStream mInput,
                                      POIList mOutput, City mCity)
      throws Exception;

  public void parseCityList(ByteArrayOutputStream mInput, CityList mOutput)
      throws Exception;
}