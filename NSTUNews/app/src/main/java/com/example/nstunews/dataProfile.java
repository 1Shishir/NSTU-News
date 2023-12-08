package com.example.nstunews;

public class dataProfile {
    String firstName;
    String lastName;
    String email;
    String uid;
    String phone;
    String role;
    String profileImage;

    public dataProfile(){

    }

    public dataProfile(String firstName, String lastName, String email, String uid, String phone, String role, String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uid = uid;
        this.phone = phone;
        this.role = role;
        this.profileImage = profileImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
