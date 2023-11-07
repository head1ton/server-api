package ai.serverapi.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1827203821L;

    public static final QMember member = new QMember("member1");

    public final StringPath birth = createString("birth");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<Recipient, QRecipient> recipientList = this.<Recipient, QRecipient>createList("recipientList", Recipient.class, QRecipient.class, PathInits.DIRECT2);

    public final EnumPath<ai.serverapi.member.enums.Role> role = createEnum("role", ai.serverapi.member.enums.Role.class);

    public final StringPath snsId = createString("snsId");

    public final EnumPath<ai.serverapi.member.enums.SnsJoinType> snsType = createEnum("snsType", ai.serverapi.member.enums.SnsJoinType.class);

    public final EnumPath<ai.serverapi.member.enums.Status> status = createEnum("status", ai.serverapi.member.enums.Status.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

