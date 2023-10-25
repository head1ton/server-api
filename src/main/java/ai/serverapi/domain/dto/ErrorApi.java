package ai.serverapi.domain.dto;

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
