package model;

/** Role: 0 not responsible, 1 delegue, 2 administrator**/

public class User {
    private String ID;
    private String FirstName;
    private String LastName;
    private String PhoneNumber;
    private String Role;
    private String Class;

    public User(String ID, String FirstName, String LastName, String PhoneNumber, String Role, String Class) {
        this.ID = ID;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.PhoneNumber = PhoneNumber;
        this.Role = Role;
        this.Class = Class;
    }

    public User(String ID, String FirstName, String LastName, String PhoneNumber, String Role) {
        this.ID = ID;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.PhoneNumber = PhoneNumber;
        this.Role = Role;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getClassID() {
        return Class;
    }

    public void setClass(String aClass) {
        Class = aClass;
    }
}
