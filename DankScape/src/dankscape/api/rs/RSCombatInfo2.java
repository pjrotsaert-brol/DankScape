/*
 * RSCombatInfo2.java
 * ---------------------------------------------------------------------------
 * Note: this file was automatically generated by the DankScape API Generator,
 * modifications to this file are useless as it will have to be regenerated often in the future.
 */

package dankscape.api.rs;

import dankscape.loader.AppletLoader;

// Original name: iu => gd (CacheableNode) => gw (Node) => java.lang.Object
public class RSCombatInfo2 extends RSCacheableNode {

    public RSCombatInfo2(Object ref) {
        super(ref);
    }

    public static RSNodeCache getSpriteCache() {
        Object objects = (Object)AppletLoader.getSingleton().getFieldValue("CombatInfo2", "spriteCache", null);
        RSNodeCache wrappers = null;
        if(objects != null)
            wrappers = (RSNodeCache)getWrapper(objects);
        return wrappers;
    }

    public int getHealthScale() {
        return (int)AppletLoader.getSingleton().getFieldValue("CombatInfo2", "healthScale", ref);
    }

}