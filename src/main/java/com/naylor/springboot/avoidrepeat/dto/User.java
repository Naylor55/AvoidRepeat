package com.naylor.springboot.avoidrepeat.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    

    private Integer id;

    private String name;

}
