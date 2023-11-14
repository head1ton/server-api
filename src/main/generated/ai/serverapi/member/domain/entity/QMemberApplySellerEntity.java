package ai.serverapi.member.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberApplySellerEntity is a Querydsl query type for MemberApplySellerEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberApplySellerEntity extends EntityPathBase<MemberApplySellerEntity> {

    private static final long serialVersionUID = -508240546L;

    public static final QMemberApplySellerEntity memberApplySellerEntity = new QMemberApplySellerEntity("memberApplySellerEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final EnumPath<ai.serverapi.member.enums.MemberApplySellerStatus> status = createEnum("status", ai.serverapi.member.enums.MemberApplySellerStatus.class);

    public QMemberApplySellerEntity(String variable) {
        super(MemberApplySellerEntity.class, forVariable(variable));
    }

    public QMemberApplySellerEntity(Path<? extends MemberApplySellerEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberApplySellerEntity(PathMetadata metadata) {
        super(MemberApplySellerEntity.class, metadata);
    }

}

