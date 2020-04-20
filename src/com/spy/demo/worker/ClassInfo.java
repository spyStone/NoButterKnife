package com.spy.demo.worker;


/**
 * @author spy
 * @date 2020-04-19 17:25
 */
public class ClassInfo {

    public static enum ClassType {
        Unknown, Activity, Fragment, Adapter
    }

    private ClassType classType = ClassType.Unknown;
    private boolean hasOnCreateMethod;
    private boolean hasOnViewCreated;

    public boolean unknownClassType (){
        return classType == ClassType.Unknown;
    }

    public boolean isAdapter() {
        return classType == ClassType.Adapter;
    }

    public boolean isFragment() {
        return classType == ClassType.Fragment;
    }

    public boolean isActivity() {
        return classType == ClassType.Activity;
    }


    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public boolean hasOnCreateMethod() {
        return hasOnCreateMethod;
    }

    public void setHasOnCreateMethod(boolean hasOnCreateMethod) {
        this.hasOnCreateMethod = hasOnCreateMethod;
    }

    public boolean hasOnViewCreated() {
        return hasOnViewCreated;
    }

    public void setHasOnViewCreated(boolean hasOnViewCreated) {
        this.hasOnViewCreated = hasOnViewCreated;
    }
}
