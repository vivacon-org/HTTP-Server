package org.vivacon.framework.serialization;

import org.vivacon.framework.serialization.common.ObjectMapper;
import org.vivacon.framework.serialization.json.JsonMapper;
import org.vivacon.framework.serialization.json.serializer.JsonSerializationFeatures;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        JsonSerializationFeatures features = new JsonSerializationFeatures();
        features.enableIncludeClazzNameWrapper(true);
        features.enablePrintPrettyJson(false);

        JsonMapper objectMapper = new JsonMapper();
        objectMapper.setFeatures(features);
        objectMapper.registerSerializer(Person.class, null);

        String serializedStr = objectMapper.serialize(new Person("Ngoc"));

        //Person deserialize = objectMapper.deserialize(serializedStr, Person.class);
        System.out.println(serializedStr);
    }

    private static class Person{
        private String name;

        private Map<School, Integer> map;

        public Person(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class School{

    }
}
