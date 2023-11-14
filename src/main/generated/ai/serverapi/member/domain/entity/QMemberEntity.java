package ai.serverapi.member.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberEntity is a Querydsl query type for MemberEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberEntity extends EntityPathBase<MemberEntity> {

    private static final long serialVersionUID = 1483405845L;

    public static final QMemberEntity memberEntity = new QMemberEntity("memberEntity");

    public final StringPath birth = createString("birth");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<RecipientEntity, QRecipientEntity> recipientList = this.<RecipientEntity, QRecipientEntity>createList("recipientList", RecipientEntity.class, QRecipientEntity.class, PathInits.DIRECT2);

    public final EnumPath<ai.serverapi.member.enums.MemberRole> role = createEnum("role", ai.serverapi.member.enums.MemberRole.class);

    public final StringPath snsId = createString("snsId");

    public final EnumPath<ai.serverapi.member.enums.SnsJoinType> snsType = createEnum("snsType", ai.serverapi.member.enums.SnsJoinType.class);

    public final EnumPath<ai.serverapi.member.enums.MemberStatus> status = createEnum("status", ai.serverapi.member.enums.MemberStatus.class);

    public QMemberEntity(String variable) {
        super(MemberEntity.class, forVariable(variable));
    }

    public QMemberEntity(Path<? extends MemberEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberEntity(PathMetadata metadata) {
        super(MemberEntity.class, metadata);
    }

}

