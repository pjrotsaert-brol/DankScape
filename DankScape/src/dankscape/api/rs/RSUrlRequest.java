/*
 * RSUrlRequest.java
 * ---------------------------------------------------------------------------
 * Note: this file was automatically generated by the DankScape API Generator,
 * modifications to this file are useless as it will have to be regenerated often in the future.
 */

package dankscape.api.rs;

import dankscape.loader.AppletLoader;
import dankscape.api.internal.RSClassWrapper;

// Original name: en => java.lang.Object
public class RSUrlRequest extends RSClassWrapper {

    public RSUrlRequest(Object ref) {
        super(ref);
    }

    public byte[] getResponse0() {
        return (byte[])AppletLoader.getSingleton().getFieldValue("UrlRequest", "response0", ref);
    }

    public boolean getIsDone0() {
        return (boolean)AppletLoader.getSingleton().getFieldValue("UrlRequest", "isDone0", ref);
    }

    public java.net.URL getUrl() {
        return (java.net.URL)AppletLoader.getSingleton().getFieldValue("UrlRequest", "url", ref);
    }

}
