package org.vivacon.framework.serialization;

import org.vivacon.framework.serialization.json.deserializer.StdJsonDeserializer;
import org.vivacon.framework.serialization.json.serializer.JsonGenerator;
import org.vivacon.framework.serialization.json.serializer.StdJsonSerializer;

public class Main {

    public static void main(String[] args) {
        Clazz clazz = new Clazz("ER");
        Person person = new Person("Hung", 15, clazz, "David");
        ObjectMapper objectMapper = new ObjectMapper(new StdJsonSerializer(), new StdJsonDeserializer());
        String serialize = objectMapper.serialize(person, new JsonGenerator());
        System.out.println(serialize);
    }


    private static class Person{
        private String name;

        private int age;

        private Clazz clazz;

        private String nickName;

        public Person(String name, int age, Clazz clazz, String nickName) {
            this.name = name;
            this.age = age;
            this.clazz = clazz;
            this.nickName = nickName;
        }
    }

    private static class Clazz{
        private String name;

        public Clazz(String name){
            this.name = name;
        }
    }
}
