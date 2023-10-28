package ai.serverapi.domain.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberApplySeller is a Querydsl query type for MemberApplySeller
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberApplySeller extends EntityPathBase<MemberApplySeller> {

    private static final long serialVersionUID = -1539770243L;

    public static final QMemberApplySeller memberApplySeller = new QMemberApplySeller("memberApplySeller");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final EnumPath<ai.serverapi.domain.enums.member.MemberApplySellerStatus> status = createEnum("status", ai.serverapi.domain.enums.member.MemberApplySellerStatus.class);

    public QMemberApplySeller(String variable) {
        super(MemberApplySeller.class, forVariable(variable));
    }

    public QMemberApplySeller(Path<? extends MemberApplySeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberApplySeller(PathMetadata metadata) {
        super(MemberApplySeller.class, metadata);
    }

}

