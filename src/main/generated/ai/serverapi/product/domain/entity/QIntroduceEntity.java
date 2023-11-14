package ai.serverapi.product.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIntroduceEntity is a Querydsl query type for IntroduceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIntroduceEntity extends EntityPathBase<IntroduceEntity> {

    private static final long serialVersionUID = -1859498267L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIntroduceEntity introduceEntity = new QIntroduceEntity("introduceEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final QSellerEntity seller;

    public final EnumPath<ai.serverapi.member.enums.IntroduceStatus> status = createEnum("status", ai.serverapi.member.enums.IntroduceStatus.class);

    public final StringPath subject = createString("subject");

    public final StringPath url = createString("url");

    public QIntroduceEntity(String variable) {
        this(IntroduceEntity.class, forVariable(variable), INITS);
    }

    public QIntroduceEntity(Path<? extends IntroduceEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIntroduceEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIntroduceEntity(PathMetadata metadata, PathInits inits) {
        this(IntroduceEntity.class, metadata, inits);
    }

    public QIntroduceEntity(Class<? extends IntroduceEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.seller = inits.isInitialized("seller") ? new QSellerEntity(forProperty("seller"), inits.get("seller")) : null;
    }

}

