package com.skycastle.auction.entities.forms.requests;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ModelRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8001795241473090377L;
    private String model;
    private String make;
    private Integer year;
}
