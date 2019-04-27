package model;

public class Class {
    private String classID;
    private String isOn;
    private String photoURL;

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Class(String classID, String isOn) {
        this.classID = classID;
        this.isOn = isOn;
    }

    public Class() {
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
