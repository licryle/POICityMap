package com.licryle.POICityMap.datastructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class POIList extends Hashtable<Integer, POI> {
  /**
   * 
   */
  private static final long serialVersionUID = -2878489681537529799L;

  protected Date _mLastUpdate;

  public Date getLastUpdate() { return _mLastUpdate; }
  public void setLastUpdate(Date mLastUpdate) { _mLastUpdate = mLastUpdate; }

  public boolean isDynamicExpired(int iMinutes) {
    return isOverTime(Calendar.MINUTE, iMinutes);
  }

  public boolean isStaticExpired(int iDays) {
    return isOverTime(Calendar.DATE, iDays);
  }

  protected boolean isOverTime(int iType, int iLength) {
    Date mLastModified = getLastUpdate();
    if (mLastModified == null) return true;

    Calendar mDeadline = Calendar.getInstance();
    mDeadline.add(iType, -iLength);

    return mLastModified.before(mDeadline.getTime());    
  }

  public void removeDynamicData() {
    Iterator<Entry<Integer, POI>> it = this.entrySet().iterator();
    while (it.hasNext()) {
      Entry<Integer, POI> entry = it.next();
      POI mPOI = entry.getValue();
      mPOI.removeDynamicData();
    }
  }

  public static POIList loadPOIListInfo(File mFile, int iDeadLine) {
    POIList mPOIList;

    try {
      FileInputStream mInput = new FileInputStream(mFile);
      ObjectInputStream mObjectStream = new ObjectInputStream(mInput);
      mPOIList = (POIList) mObjectStream.readObject();

      if (mPOIList.isDynamicExpired(iDeadLine)) {
        mPOIList.removeDynamicData();
      }

      mObjectStream.close();
      return mPOIList;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (StreamCorruptedException e) {
      mFile.delete();
      e.printStackTrace();
    } catch (IOException e) {
      mFile.delete();
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // We changed version, let's delete that file
      mFile.delete();
      e.printStackTrace();
    } catch (Exception e) {
      // In doubt, call the shots
      mFile.delete();
      e.printStackTrace();
    }

    return new POIList();
  }

  public boolean saveToFile(File mFile) {
    mFile.delete();
    FileOutputStream mOutput;
    try {
      mOutput = new FileOutputStream(mFile);
      ObjectOutputStream mObjectStream = new ObjectOutputStream(mOutput);

      mObjectStream.writeObject(this);
      mObjectStream.close();
      return true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
      mFile.delete(); // TODO: this isn't reliable
    }
 
    return false;
  }

  public void copyInto(POIList mPOIList) {
    this.clear();
    Iterator<Map.Entry<Integer, POI>> it = mPOIList.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry<Integer, POI> entry = it.next();

      this.put(entry.getKey(), entry.getValue());
    }
  }
}
