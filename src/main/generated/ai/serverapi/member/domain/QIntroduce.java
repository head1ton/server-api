package ai.serverapi.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIntroduce is a Querydsl query type for Introduce
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIntroduce extends EntityPathBase<Introduce> {

    private static final long serialVersionUID = -656612372L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIntroduce introduce = new QIntroduce("introduce");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final QSeller seller;

    public final EnumPath<ai.serverapi.member.enums.IntroduceStatus> status = createEnum("status", ai.serverapi.member.enums.IntroduceStatus.class);

    public final StringPath subject = createString("subject");

    public final StringPath url = createString("url");

    public QIntroduce(String variable) {
        this(Introduce.class, forVariable(variable), INITS);
    }

    public QIntroduce(Path<? extends Introduce> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIntroduce(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIntroduce(PathMetadata metadata, PathInits inits) {
        this(Introduce.class, metadata, inits);
    }

    public QIntroduce(Class<? extends Introduce> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.seller = inits.isInitialized("seller") ? new QSeller(forProperty("seller"), inits.get("seller")) : null;
    }

}

