package pl.lodz.p.edu.shop.dataaccess.model.other;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Constraint {

    REQUIRED("NOT NULL", false),
    UNIQUE("UNIQUE", false),
    BIG_NUMBER("BIGINT", true),
    TEXT("VARCHAR(255)", true),
    FRACTIONAL_NUMBER("DOUBLE PRECISION", true),
    LOGICAL_VALUE("BOOLEAN", true);

    private final String constraintValue;
    private final boolean isType;
}
