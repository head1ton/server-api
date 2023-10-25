package ai.serverapi.service;

import ai.serverapi.domain.vo.MessageVo;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public MessageVo hello() {
        return MessageVo.builder().message("테스트").build();
    }

}
