/*
 * RSPlayer.java
 * ---------------------------------------------------------------------------
 * Note: this file was automatically generated by the DankScape API Generator,
 * modifications to this file are useless as it will have to be regenerated often in the future.
 */

package dankscape.api.rs;

import dankscape.loader.AppletLoader;

// Original name: bf => bm (Actor) => eo (Renderable) => gd (CacheableNode) => gw (Node) => java.lang.Object
public class RSPlayer extends RSActor {

    public RSPlayer(Object ref) {
        super(ref);
    }

    public int getTotalLevel() {
        return (int)AppletLoader.getSingleton().getFieldValue("Player", "totalLevel", ref);
    }

    public int getSkullIcon() {
        return (int)AppletLoader.getSingleton().getFieldValue("Player", "skullIcon", ref);
    }

    public int getAnimationCycleEnd() {
        return (int)AppletLoader.getSingleton().getFieldValue("Player", "animationCycleEnd", ref);
    }

    public boolean getHidden() {
        return (boolean)AppletLoader.getSingleton().getFieldValue("Player", "hidden", ref);
    }

    public boolean getIsLowDetail() {
        return (boolean)AppletLoader.getSingleton().getFieldValue("Player", "isLowDetail", ref);
    }

    public int getTeam() {
        return (int)AppletLoader.getSingleton().getFieldValue("Player", "team", ref);
    }

    public int getOverheadIcon() {
        return (int)AppletLoader.getSingleton().getFieldValue("Player", "overheadIcon", ref);
    }

    public RSPlayerComposition getComposition() {
        Object objects = (Object)AppletLoader.getSingleton().getFieldValue("Player", "composition", ref);
        RSPlayerComposition wrappers = null;
        if(objects != null)
            wrappers = (RSPlayerComposition)getWrapper(objects);
        return wrappers;
    }

    public java.lang.String getName() {
        return (java.lang.String)AppletLoader.getSingleton().getFieldValue("Player", "name", ref);
    }

    public int getAnimationCycleStart() {
        return (int)AppletLoader.getSingleton().getFieldValue("Player", "animationCycleStart", ref);
    }

    public RSModel getModel() {
        Object objects = (Object)AppletLoader.getSingleton().getFieldValue("Player", "model", ref);
        RSModel wrappers = null;
        if(objects != null)
            wrappers = (RSModel)getWrapper(objects);
        return wrappers;
    }

    public int getCombatLevel() {
        return (int)AppletLoader.getSingleton().getFieldValue("Player", "combatLevel", ref);
    }

    public java.lang.String[] getActions() {
        return (java.lang.String[])AppletLoader.getSingleton().getFieldValue("Player", "actions", ref);
    }

}