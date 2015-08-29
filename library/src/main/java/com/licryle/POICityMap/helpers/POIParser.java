package com.licryle.POICityMap.helpers;

import java.io.ByteArrayOutputStream;

import com.licryle.POICityMap.datastructure.City;
import com.licryle.POICityMap.datastructure.POI;
import com.licryle.POICityMap.datastructure.POIList;

/**
 * Created by licryle on 8/23/15.
 */
public interface POIParser<P extends POI> {
  public void parsePOIListFullData(ByteArrayOutputStream mInput,
                                      POIList mOutput, City mCity)
      throws Exception;

  public void parsePOIListDynamicData(ByteArrayOutputStream mInput,
                                      POIList mOutput, City mCity)
      throws Exception;
}