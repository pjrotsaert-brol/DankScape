package dankscape.analysis.methods;

import dankscape.misc.Hook;
import java.util.Arrays;


/**
 * Created by Kyle on 11/9/2015.
 */
public class ObjectDefinitionMethodAnalyzer extends AbstractMethodAnalyzer {

    public ObjectDefinitionMethodAnalyzer() {
        setId("ObjectDefinition");
        setNeededHooks(Arrays.asList("Actions", "Name"));
    }

    
    @Override
    public void identify() {

        addHook(new Hook("Actions", getFields(getClassNode(), "[Ljava/lang/String;").get(0)));
        addHook(new Hook("Name", getFields(getClassNode(), "Ljava/lang/String;").get(0)));
    }
}
