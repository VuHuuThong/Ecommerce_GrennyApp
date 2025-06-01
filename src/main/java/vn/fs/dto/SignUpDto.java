package vn.fs.dto;

import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
public class SignUpDto {
    private String name;
    private String email;
    private String password;
    private String avatar;
    @Temporal(TemporalType.DATE)
    private Date registerDate;
    private Boolean status;
}
