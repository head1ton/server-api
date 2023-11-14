package ai.serverapi.member.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecipientEntity is a Querydsl query type for RecipientEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipientEntity extends EntityPathBase<RecipientEntity> {

    private static final long serialVersionUID = 152276324L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipientEntity recipientEntity = new QRecipientEntity("recipientEntity");

    public final StringPath address = createString("address");

    public final StringPath addressDetails = createString("addressDetails");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMemberEntity member;

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final EnumPath<ai.serverapi.member.enums.RecipientInfoStatus> status = createEnum("status", ai.serverapi.member.enums.RecipientInfoStatus.class);

    public final StringPath tel = createString("tel");

    public final StringPath zonecode = createString("zonecode");

    public QRecipientEntity(String variable) {
        this(RecipientEntity.class, forVariable(variable), INITS);
    }

    public QRecipientEntity(Path<? extends RecipientEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecipientEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecipientEntity(PathMetadata metadata, PathInits inits) {
        this(RecipientEntity.class, metadata, inits);
    }

    public QRecipientEntity(Class<? extends RecipientEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMemberEntity(forProperty("member")) : null;
    }

}

