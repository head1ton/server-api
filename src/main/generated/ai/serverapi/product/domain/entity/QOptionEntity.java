package ai.serverapi.product.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOptionEntity is a Querydsl query type for OptionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOptionEntity extends EntityPathBase<OptionEntity> {

    private static final long serialVersionUID = -1491191147L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOptionEntity optionEntity = new QOptionEntity("optionEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> ea = createNumber("ea", Integer.class);

    public final NumberPath<Integer> extraPrice = createNumber("extraPrice", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final QProductEntity product;

    public final EnumPath<ai.serverapi.product.enums.OptionStatus> status = createEnum("status", ai.serverapi.product.enums.OptionStatus.class);

    public QOptionEntity(String variable) {
        this(OptionEntity.class, forVariable(variable), INITS);
    }

    public QOptionEntity(Path<? extends OptionEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOptionEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOptionEntity(PathMetadata metadata, PathInits inits) {
        this(OptionEntity.class, metadata, inits);
    }

    public QOptionEntity(Class<? extends OptionEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProductEntity(forProperty("product"), inits.get("product")) : null;
    }

}

