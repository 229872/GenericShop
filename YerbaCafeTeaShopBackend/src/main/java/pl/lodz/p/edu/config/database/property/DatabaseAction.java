package pl.lodz.p.edu.config.database.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DatabaseAction {
    NONE("none"), CREATE("create"), DROP_AND_CREATE("drop-and-create"),
    DROP("drop");

    private final String actionName;
}
