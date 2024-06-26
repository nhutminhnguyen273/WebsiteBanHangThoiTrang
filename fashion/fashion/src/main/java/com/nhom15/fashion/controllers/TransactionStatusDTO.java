package com.nhom15.fashion.controllers;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TransactionStatusDTO implements Serializable {
    private String  status;
    private String message;
    private String data;
}
