package dankscape.analysis.methods;

import dankscape.misc.Hook;

import java.util.Arrays;

/**
 * Created by Kyle on 11/16/2015.
 */
public class SceneTileMethodAnalyzer extends AbstractMethodAnalyzer {

    public SceneTileMethodAnalyzer() {
        setId("SceneTile");
        setNeededHooks(Arrays.asList("GameObject", "BoundaryObject", "WallDecoration", "GroundDecoration"));
    }

    
    @Override
    public void identify() {
        addHook(new Hook("GameObject", getFields(getClassNode(), "[L" + getOther("GameObject").getName() + ";").get(0)));
        addHook(new Hook("BoundaryObject", getFields(getClassNode(), "L" + getOther("BoundaryObject").getName() + ";").get(0)));
        addHook(new Hook("WallDecoration", getFields(getClassNode(), "L" + getOther("WallDecoration").getName() + ";").get(0)));
        addHook(new Hook("GroundDecoration", getFields(getClassNode(), "L" + getOther("FloorDecoration").getName() + ";").get(0)));

    }
}
