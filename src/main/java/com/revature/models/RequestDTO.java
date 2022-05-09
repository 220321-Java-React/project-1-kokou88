package com.revature.models;

import java.time.LocalDate;
import java.util.Date;

//only for the creation of new requests
public class RequestDTO {
    public double amount;
    public String description;
    public String type;
    public int authorId;

    public RequestDTO(){}

    public RequestDTO(double amount, String description, String type, int authorId) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.authorId = authorId;
    }


}