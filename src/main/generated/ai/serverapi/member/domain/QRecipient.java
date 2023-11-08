package ai.serverapi.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecipient is a Querydsl query type for Recipient
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipient extends EntityPathBase<Recipient> {

    private static final long serialVersionUID = -708522778L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipient recipient = new QRecipient("recipient");

    public final StringPath address = createString("address");

    public final StringPath addressDetails = createString("addressDetails");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final EnumPath<ai.serverapi.member.enums.RecipientInfoStatus> status = createEnum("status", ai.serverapi.member.enums.RecipientInfoStatus.class);

    public final StringPath tel = createString("tel");

    public final StringPath zonecode = createString("zonecode");

    public QRecipient(String variable) {
        this(Recipient.class, forVariable(variable), INITS);
    }

    public QRecipient(Path<? extends Recipient> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecipient(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecipient(PathMetadata metadata, PathInits inits) {
        this(Recipient.class, metadata, inits);
    }

    public QRecipient(Class<? extends Recipient> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

