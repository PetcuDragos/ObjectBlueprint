package service;

import representation.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class ObjectBlueprint {

    private final Class<?> sourceClass;
    private final List<Class<?>> excludedClasses;

    private final Map<String, String> defaultValues;

    private final Map<String, String> parameterizedValuesMap;

    private final Set<String> processingClasses = new HashSet<>();

    public ObjectBlueprint(Class<?> sourceClass, List<Class<?>> excludedClasses, Map<String, String> stringStringMap) {
        this.sourceClass = sourceClass;
        this.excludedClasses = excludedClasses;
        defaultValues = stringStringMap;
        parameterizedValuesMap = new HashMap<>();
    }

    public Node run() {
        return createNode(sourceClass);
    }

    private Node createNode(Class<?> sourceClass) {

        if (sourceClass == null) return null;

        if (isClassExcluded(sourceClass)) return null;

        if (isClassPrimitiveType(sourceClass)) return createNodeForDefaultValueClass(sourceClass);

        if (hasDefaultValue(sourceClass)) return createNodeForDefaultValueClass(sourceClass);

        if (isClassCollection(sourceClass)) return createNodeForCollectionClass(sourceClass);

        return createNodeForClassWithFields(sourceClass);

    }

    private Node createNodeForCollectionClass(Class<?> sourceClass) {
        TypeVariable<? extends Class<?>>[] typeParameters = sourceClass.getTypeParameters();
        String actualTypeParameter = parameterizedValuesMap.get(typeParameters[0].getName());
        Class<?> actualClassParameter;
        try {
            actualClassParameter = Class.forName(actualTypeParameter);
        } catch (ClassNotFoundException e) {
            actualClassParameter = null;
        }
        List<Node> collectionObject = new ArrayList<>();
        collectionObject.add(createNode(actualClassParameter));
        return new CollectionNode(collectionObject);
    }

    private boolean isClassCollection(Class<?> sourceClass) {
        return Collection.class.isAssignableFrom(sourceClass);
    }

    private Node createNodeForClassWithFields(Class<?> sourceClass) {
        processingClasses.add(sourceClass.getSimpleName());
        Field[] declaredFields = sourceClass.getDeclaredFields();
        List<Node> nodes = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            Class<?> fieldClass;
            if (isParameterizedField(declaredField)) {
                processParameterizedField(declaredField);
            }
            if (isTemplateField(declaredField)) {
                try {
                    fieldClass = Class.forName(parameterizedValuesMap.get(declaredField.getGenericType().getTypeName()));
                } catch (ClassNotFoundException e) {
                    System.out.println("Template for " + declaredField.getGenericType().getTypeName() + " had an invalid class");
                    fieldClass = null;
                }
            } else {
                fieldClass = declaredField.getType();
                if (processingClasses.contains(fieldClass.getSimpleName())) {
                    System.out.println("Attention, circular dependency in class " + fieldClass.getSimpleName() + ", returned null");
                    fieldClass = null;
                }
            }
            Node nodeForValue = createNode(fieldClass);
            if (nodeForValue == null) continue;
            Node nodeForName = new NameNode(declaredField.getName(), nodeForValue);
            nodes.add(nodeForName);
        }
        processingClasses.remove(sourceClass.getSimpleName());
        return new EmptyNode(nodes);
    }

    private boolean isTemplateField(Field declaredField) {
        String typeName = declaredField.getGenericType().getTypeName();
        String actualType = parameterizedValuesMap.get(typeName);
        return actualType != null;
    }

    private boolean isParameterizedField(Field declaredField) {
        return declaredField.getGenericType() instanceof ParameterizedType;
    }

    private void processParameterizedField(Field declaredField) {
        ParameterizedType genericType = (ParameterizedType) declaredField.getGenericType();
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        TypeVariable<? extends Class<?>>[] typeParameters;
        try {
            typeParameters = Class.forName(genericType.getRawType().getTypeName()).getTypeParameters();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int typeArgumentIndex = 0; typeArgumentIndex < actualTypeArguments.length; typeArgumentIndex++) {
            parameterizedValuesMap.put(typeParameters[typeArgumentIndex].getName(), actualTypeArguments[typeArgumentIndex].getTypeName());
        }
    }

    private Node createNodeForDefaultValueClass(Class<?> sourceClass) {
        String className = sourceClass.getSimpleName();
        String defaultValue = defaultValues.get(className);
        if (defaultValue == null) {
            defaultValue = "ERR! No default value found!";
        }
        return new ValueNode(defaultValue);
    }

    private boolean hasDefaultValue(Class<?> sourceClass) {
        return defaultValues.get(sourceClass.getSimpleName()) != null;
    }

    private boolean isClassExcluded(Class<?> sourceClass) {
        return excludedClasses.contains(sourceClass);
    }

    private boolean isClassPrimitiveType(Class<?> sourceClass) {
        return sourceClass.isPrimitive();
    }


}
