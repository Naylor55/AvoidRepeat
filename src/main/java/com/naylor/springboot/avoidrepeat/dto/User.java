package com.naylor.springboot.avoidrepeat.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Data
@Accessors(chain = true)
public class User {
    

    private Integer id;

    private String name;

}
