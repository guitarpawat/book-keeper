package me.guitarpawat.bookkeeper.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorModel {

    private int errorStatus;
    private String errorMessage;
}
