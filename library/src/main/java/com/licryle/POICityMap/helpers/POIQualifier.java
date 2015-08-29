package com.licryle.POICityMap.helpers;

import com.licryle.POICityMap.datastructure.POI;

/**
 * Created by licryle on 8/23/15.
 */
public interface POIQualifier<P extends POI> {
  public int getIcon(P mPOI);
  public boolean isFavorite(P mPOI);
  public boolean isFiltered(P mPOI);
}