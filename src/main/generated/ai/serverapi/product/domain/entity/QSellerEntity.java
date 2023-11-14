package ai.serverapi.product.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSellerEntity is a Querydsl query type for SellerEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSellerEntity extends EntityPathBase<SellerEntity> {

    private static final long serialVersionUID = 1063260703L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSellerEntity sellerEntity = new QSellerEntity("sellerEntity");

    public final StringPath address = createString("address");

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath company = createString("company");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ai.serverapi.member.domain.entity.QMemberEntity member;

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath tel = createString("tel");

    public final StringPath zonecode = createString("zonecode");

    public QSellerEntity(String variable) {
        this(SellerEntity.class, forVariable(variable), INITS);
    }

    public QSellerEntity(Path<? extends SellerEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSellerEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSellerEntity(PathMetadata metadata, PathInits inits) {
        this(SellerEntity.class, metadata, inits);
    }

    public QSellerEntity(Class<? extends SellerEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new ai.serverapi.member.domain.entity.QMemberEntity(forProperty("member")) : null;
    }

}

