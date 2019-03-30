package model;

/** Role: 0 not responsible, 1 delegue, 2 administrator**/

public class User {
    private String ID;
    private String firstname;
    private String lastname;
    private String phone;
    private String role;
    private String classID;

    public User(String ID, String firstname, String lastname, String phone, String role, String classID){
        this.ID = ID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone=phone;
        this.role=role;
        this.classID=classID;
    }

    public User(String ID, String firstname, String lastname, String phone, String role){
        this.ID = ID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone=phone;
        this.role=role;
    }

    /*private User(String firstname, String lastname, int phone, int role, String classID){
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone=phone;
        this.role=role;
        this.classID=classID;
    }

    private User(String firstname, String lastname, int phone, int role){
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone=phone;
        this.role=role;
    }

    public User getUser(){
        if(this.getRole()==0){
            return new User(this.firstname, this.lastname, this.phone, this.role);
        }
        else{
            return new User(this.firstname, this.lastname, this.phone, this.role, this.getClass().getName());

        }
    }
*/
    public void setID(String ID) {
        this.ID = ID;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getID() {
        return ID;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getClassID() {
        return classID;
    }

    public boolean isAuthenticated(){
        return true;
    }
}
