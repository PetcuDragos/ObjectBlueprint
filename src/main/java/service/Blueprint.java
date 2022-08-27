package service;

import format.JsonBlueprint;
import representation.Node;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Blueprint {
    private String filePath;
    private final Class<?> sourceClass;
    private List<Class<?>> excludedClasses = new ArrayList<>();

    public Blueprint(Class<?> aClass) {
        sourceClass = aClass;

    }

    public void setExcludedClasses(List<Class<?>> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    public void setClassesDefaultValuesFilePath(String filePath) {
        this.filePath = filePath;
    }

    private Node getObjectRepresentation() {

        return (new ObjectBlueprint(sourceClass, excludedClasses, getClassesDefaultValuesFromFile())).run();
    }

    public String getJsonBlueprint() {
        return JsonBlueprint.getJsonString(getObjectRepresentation());
    }

    private Map<String, String> getClassesDefaultValuesFromFile() {
        Map<String, String> result = loadDefaultClassesDefaultValues();
        if (filePath == null) return result;
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                result.put(objectObjectEntry.getKey().toString(), objectObjectEntry.getValue().toString());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Default values file path is unavailable!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private Map<String, String> loadDefaultClassesDefaultValues() {
        Map<String, String> result = new HashMap<>();
        result.put("String", "\"string\"");
        result.put("Integer", "0");
        result.put("Float", "0.0");
        result.put("Double", "0.00");
        result.put("Byte", "0");
        result.put("Long", "0");
        result.put("Short", "0");
        result.put("Boolean", "false");
        result.put("int", "0");
        result.put("float", "0.0");
        result.put("double", "0.00");
        result.put("byte", "0");
        result.put("long", "0");
        result.put("short", "0");
        result.put("char", "''");
        result.put("boolean", "false");

        return result;
    }

}
