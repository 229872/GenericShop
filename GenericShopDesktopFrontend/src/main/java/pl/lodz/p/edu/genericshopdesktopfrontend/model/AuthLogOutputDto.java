package pl.lodz.p.edu.genericshopdesktopfrontend.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record AuthLogOutputDto(
    String lastSuccessfulAuthIpAddr,
    String lastUnsuccessfulAuthIpAddr,
    LocalDateTime lastSuccessfulAuthTime,
    LocalDateTime lastUnsuccessfulAuthTime,
    Integer unsuccessfulAuthCounter,
    LocalDateTime blockadeEndTime
) {

    public Optional<String> getBlockadeEndTime() {
        return Optional.ofNullable(blockadeEndTime)
            .map(localDateTime -> localDateTime.format(formatter()));
    }


    public Optional<String> getLastUnsuccessfulAuthTime() {
        return Optional.ofNullable(lastUnsuccessfulAuthTime())
            .map(localDateTime -> localDateTime.format(formatter()));
    }


    public Optional<String> getLastSuccessfulAuthTime() {
        return Optional.ofNullable(lastSuccessfulAuthTime)
            .map(localDateTime -> localDateTime.format(formatter()));
    }


    public Optional<String> getLastUnsuccessfulAuthIpAddr() {
        return Optional.ofNullable(lastUnsuccessfulAuthIpAddr);
    }


    private DateTimeFormatter formatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");
    }
}
