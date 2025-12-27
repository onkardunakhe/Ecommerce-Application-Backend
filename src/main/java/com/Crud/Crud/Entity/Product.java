package com.Crud.Crud.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Auto-generate primary key
    private Integer id;

    private String name;      // ✅ Matches JSON: "name"
    private String category;  // ✅ Matches JSON: "category"
    private Long price;       // ✅ Matches JSON: "price"
    private String imgtype;
    private String imgname;
    @Lob
    private byte[] imgbyt;
}
