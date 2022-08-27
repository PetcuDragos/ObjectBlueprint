import examples.ComposedClass;
import examples.ParameterizedClass;
import service.Blueprint;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Blueprint objectBlueprint = new Blueprint(ComposedClass.class);
        objectBlueprint.setExcludedClasses(List.of(ParameterizedClass.class));
        objectBlueprint.setClassesDefaultValuesFilePath("src/main/resources/classesDefaultValues.properties");
        System.out.println(objectBlueprint.getJsonBlueprint());
    }
}
