/*
 * RSClass18.java
 * ---------------------------------------------------------------------------
 * Note: this file was automatically generated by the DankScape API Generator,
 * modifications to this file are useless as it will have to be regenerated often in the future.
 */

package dankscape.api.rs;

import dankscape.loader.AppletLoader;
import dankscape.api.internal.RSClassWrapper;

// Original name: m => java.lang.Object
public class RSClass18 extends RSClassWrapper {

    public RSClass18(Object ref) {
        super(ref);
    }

    public static RSGrandExchangeEvents getGrandExchangeEvents() {
        Object objects = (Object)AppletLoader.getSingleton().getFieldValue("class18", "grandExchangeEvents", null);
        RSGrandExchangeEvents wrappers = null;
        if(objects != null)
            wrappers = (RSGrandExchangeEvents)getWrapper(objects);
        return wrappers;
    }

    public static RSSpritePixels[] getHeadIconsPrayer() {
        Object[] objects = (Object[])AppletLoader.getSingleton().getFieldValue("class18", "headIconsPrayer", null);
        RSSpritePixels[] wrappers = null;
        if(objects == null)
            return null;
        wrappers = new RSSpritePixels[objects.length];
        for(int i = 0;i < objects.length;i++) {
            if(objects[i] != null)
                wrappers[i] = (RSSpritePixels)getWrapper(objects[i]);
        }
        return wrappers;
    }

    public static Object[] getRSRef_HeadIconsPrayer() {
        return (Object[])AppletLoader.getSingleton().getFieldValue("class18", "headIconsPrayer", null);
    }

}
