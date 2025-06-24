package dyne.zenroom;

import android.util.Log;

public class Zencode {
  static {
    try {
      // Ensure this name ("zenroom") matches your .so file name (e.g., libzenroom.so)
      System.loadLibrary("zenroom");
      Log.d("ZencodeJNI", "Successfully loaded native library.");
    } catch (UnsatisfiedLinkError e) {
      Log.e("ZencodeJNI", "Failed to load native library: " + e.getMessage());
      throw e;
    }
  }

  // UPDATE THE NATIVE METHOD SIGNATURE TO INCLUDE THE TWO NEW STRING PARAMETERS
  public native String zenroom(String script, String conf, String keys, String data, String extra, String context);

}
