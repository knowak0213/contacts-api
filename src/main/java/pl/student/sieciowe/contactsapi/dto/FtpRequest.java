package pl.student.sieciowe.contactsapi.dto;

import lombok.Data;

@Data
public class FtpRequest {
    private String host;
    private int port = 21;
    private String username;
    private String password;
}
