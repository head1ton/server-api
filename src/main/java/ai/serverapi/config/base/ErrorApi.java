package ai.serverapi.config.base;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorApi<T> {

    private String code;
    private String message;
    private List<T> errors;
}
