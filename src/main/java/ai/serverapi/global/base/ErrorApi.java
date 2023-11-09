package ai.serverapi.global.base;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorApi<T> {

    private String code;
    private String message;
    private List<T> errors;
}
