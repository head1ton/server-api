package ai.serverapi.order.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderProductEntity is a Querydsl query type for OrderProductEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderProductEntity extends EntityPathBase<OrderProductEntity> {

    private static final long serialVersionUID = 499329506L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderProductEntity orderProductEntity = new QOrderProductEntity("orderProductEntity");

    public final ai.serverapi.product.domain.entity.QCategoryEntity category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image1 = createString("image1");

    public final StringPath image2 = createString("image2");

    public final StringPath image3 = createString("image3");

    public final StringPath mainExplanation = createString("mainExplanation");

    public final StringPath mainImage = createString("mainImage");

    public final StringPath mainTitle = createString("mainTitle");

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath origin = createString("origin");

    public final NumberPath<Integer> originPrice = createNumber("originPrice", Integer.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath producer = createString("producer");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final StringPath productMainExplanation = createString("productMainExplanation");

    public final StringPath productSubExplanation = createString("productSubExplanation");

    public final StringPath purchaseInquiry = createString("purchaseInquiry");

    public final ai.serverapi.product.domain.entity.QSellerEntity seller;

    public final EnumPath<ai.serverapi.product.enums.ProductStatus> status = createEnum("status", ai.serverapi.product.enums.ProductStatus.class);

    public final EnumPath<ai.serverapi.product.enums.ProductType> type = createEnum("type", ai.serverapi.product.enums.ProductType.class);

    public final NumberPath<Long> viewCnt = createNumber("viewCnt", Long.class);

    public QOrderProductEntity(String variable) {
        this(OrderProductEntity.class, forVariable(variable), INITS);
    }

    public QOrderProductEntity(Path<? extends OrderProductEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderProductEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderProductEntity(PathMetadata metadata, PathInits inits) {
        this(OrderProductEntity.class, metadata, inits);
    }

    public QOrderProductEntity(Class<? extends OrderProductEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new ai.serverapi.product.domain.entity.QCategoryEntity(forProperty("category")) : null;
        this.seller = inits.isInitialized("seller") ? new ai.serverapi.product.domain.entity.QSellerEntity(forProperty("seller"), inits.get("seller")) : null;
    }

}

