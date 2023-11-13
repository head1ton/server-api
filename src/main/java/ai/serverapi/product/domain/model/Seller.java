package ai.serverapi.product.domain.model;

import ai.serverapi.member.domain.model.Member;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Seller {

    private Long id;
    private Member member;
    private String company;
    private String tel;
    private String zonecode;
    private String address;
    private String addressDetail;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}