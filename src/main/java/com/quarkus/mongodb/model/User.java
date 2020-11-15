package com.quarkus.mongodb.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class User {

//    @JsonIgnore
//    @BsonId
//    private String _id;
    private String id;
    private String name;
    private String surname;
    private String birthDate;
    private Address address;

    public User() {
    }

    public User(String name, String surname, String birthDate, Address address) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.address = address;
    }

   /* public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    
    public static User of(Document document){
        User user = new User();
        user.setId(document.getObjectId("_id").toHexString());
        user.setName(document.getString("name"));
        user.setSurname(document.getString("surname"));
        user.setBirthDate(document.getString("birthDate"));

        Document userAddressDocument = document.get("address", Document.class);
        Address address = new Address();
        address.setType(userAddressDocument.getString("name"));
        address.setStreetAddress(userAddressDocument.getString("streetAddress"));
        address.setCity(userAddressDocument.getString("city"));
        address.setState(userAddressDocument.getString("state"));
        address.setZipCode(userAddressDocument.getString("zipCode"));
        user.setAddress(address);
        
        return user;
    }
}
