package ai.serverapi.global.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Api<T> {

    @NonNull
    private String code;
    @NonNull
    private String message;
    @NonNull
    private T data;
}
