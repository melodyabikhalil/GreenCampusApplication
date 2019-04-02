package model;

public class Class {
    private String classID;
    private String isOn;

    public Class(String classID, String isOn) {
        this.classID = classID;
        this.isOn = isOn;
    }

    public Class(){

    }

    public String getClassName() {
        return classID;
    }

    public void setClassName(String className) {
        this.classID = className;
    }

    public String getIsOn() {
        return isOn;
    }

    public void setIsOn(String isOn) {
        this.isOn = isOn;
    }
}
