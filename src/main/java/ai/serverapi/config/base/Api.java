package ai.serverapi.config.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Builder
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
