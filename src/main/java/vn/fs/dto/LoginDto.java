package vn.fs.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String nameOrEmail;
    private String password;
}
