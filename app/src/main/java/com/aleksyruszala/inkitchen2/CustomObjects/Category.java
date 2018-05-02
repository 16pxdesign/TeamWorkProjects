package com.aleksyruszala.inkitchen2.CustomObjects;

public class Category {

    private String id, name;

    public Category(String id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        //return name for spinner
        return name;
    }

    public String getId() {
        return id;
    }
}
