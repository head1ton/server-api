package ai.serverapi.domain.entity.member;

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

    private static final long serialVersionUID = 1170247152L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final StringPath birth = createString("birth");

    public final QBuyer buyer;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<Recipient, QRecipient> recipientList = this.<Recipient, QRecipient>createList("recipientList", Recipient.class, QRecipient.class, PathInits.DIRECT2);

    public final EnumPath<ai.serverapi.domain.enums.Role> role = createEnum("role", ai.serverapi.domain.enums.Role.class);

    public final StringPath snsId = createString("snsId");

    public final EnumPath<ai.serverapi.domain.enums.member.SnsJoinType> snsType = createEnum("snsType", ai.serverapi.domain.enums.member.SnsJoinType.class);

    public final EnumPath<ai.serverapi.domain.enums.member.Status> status = createEnum("status", ai.serverapi.domain.enums.member.Status.class);

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.buyer = inits.isInitialized("buyer") ? new QBuyer(forProperty("buyer")) : null;
    }

}

