package dankscape.analysis.methods;

import dankscape.misc.Hook;

import java.util.Arrays;

/**
 * Created by Kyle on 11/16/2015.
 */
public class RegionMethodAnalyzer extends AbstractMethodAnalyzer {

    public RegionMethodAnalyzer() {
        setId("Region");
        setNeededHooks(Arrays.asList("SceneTiles"));
    }

    
    @Override
    public void identify() {
        addHook(new Hook("SceneTiles", getFields(getClassNode(), "[[[L" + getOther("SceneTile").getName() + ";").get(0)));

    }
}
