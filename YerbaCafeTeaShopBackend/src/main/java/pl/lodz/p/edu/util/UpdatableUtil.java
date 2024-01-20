package pl.lodz.p.edu.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatableUtil {

    public static <T> void setNullableValue(T newValue, Consumer<T> setter) {
        Optional.ofNullable(newValue).ifPresent(setter);
    }
}
