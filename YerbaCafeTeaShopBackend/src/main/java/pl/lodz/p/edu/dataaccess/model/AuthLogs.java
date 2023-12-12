package pl.lodz.p.edu.dataaccess.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Data

@Embeddable
public class AuthLogs {

    @Column(name = "last_successful_auth_ip_addr")
    private String lastSuccessfulAuthIpAddr;

    @Column(name = "last_unsuccessful_auth_ip_addr")
    private String lastUnsuccessfulAuthIpAddr;

    @Column(name = "last_successful_auth_time")
    private LocalDateTime lastSuccessfulAuthTime;

    @Column(name = "last_unsuccessful_auth_time")
    private LocalDateTime lastUnsuccessfulAuthTime;

    @Column(name = "unsuccessful_auth_counter", nullable = false, columnDefinition = "integer default 0")
    private Integer unsuccessfulAuthCounter = 0;

    @Column(name = "blockade_end_time")
    private LocalDateTime blockadeEndTime;
}
