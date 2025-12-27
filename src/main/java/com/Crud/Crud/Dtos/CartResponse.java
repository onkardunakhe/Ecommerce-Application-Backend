package com.Crud.Crud.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartResponse {
    private Integer productId;   // matches Product.id
    private String productName;
    private Long price;          // matches Product.price
    private int quantity;

}
