package model;

public class Admin {
    private String ID;
    private String FirstName;
    private String LastName;
    private String Role;

    public Admin(String ID, String firstName, String lastName, String role) {
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        Role = role;
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

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
}
