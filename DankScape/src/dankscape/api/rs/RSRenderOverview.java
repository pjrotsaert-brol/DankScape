/*
 * RSRenderOverview.java
 * ---------------------------------------------------------------------------
 * Note: this file was automatically generated by the DankScape API Generator,
 * modifications to this file are useless as it will have to be regenerated often in the future.
 */

package dankscape.api.rs;

import dankscape.loader.AppletLoader;
import dankscape.api.internal.RSClassWrapper;

// Original name: kl => java.lang.Object
public class RSRenderOverview extends RSClassWrapper {

    public RSRenderOverview(Object ref) {
        super(ref);
    }

    public static RSFontName getFontNameVerdana13() {
        Object objects = (Object)AppletLoader.getSingleton().getFieldValue("RenderOverview", "fontNameVerdana13", null);
        RSFontName wrappers = null;
        if(objects != null)
            wrappers = (RSFontName)getWrapper(objects);
        return wrappers;
    }

    public static RSFontName getFontNameVerdana11() {
        Object objects = (Object)AppletLoader.getSingleton().getFieldValue("RenderOverview", "fontNameVerdana11", null);
        RSFontName wrappers = null;
        if(objects != null)
            wrappers = (RSFontName)getWrapper(objects);
        return wrappers;
    }

    public static RSFontName getFontNameVerdana15() {
        Object objects = (Object)AppletLoader.getSingleton().getFieldValue("RenderOverview", "fontNameVerdana15", null);
        RSFontName wrappers = null;
        if(objects != null)
            wrappers = (RSFontName)getWrapper(objects);
        return wrappers;
    }

}