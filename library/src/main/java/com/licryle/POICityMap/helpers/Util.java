package com.licryle.POICityMap.helpers;

public abstract class Util {
  public static int intToUInt(int i, int iPow) {
    long iMax = (Double.valueOf(Math.pow(2, iPow))).longValue();

    return (int) ((i < 0) ? (iMax + i) : i);
  }
}
