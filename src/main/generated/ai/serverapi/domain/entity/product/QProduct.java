package ai.serverapi.domain.entity.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -253800526L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image1 = createString("image1");

    public final StringPath image2 = createString("image2");

    public final StringPath image3 = createString("image3");

    public final StringPath mainExplanation = createString("mainExplanation");

    public final StringPath mainImage = createString("mainImage");

    public final StringPath mainTitle = createString("mainTitle");

    public final ai.serverapi.domain.entity.member.QMember member;

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath origin = createString("origin");

    public final NumberPath<Integer> originPrice = createNumber("originPrice", Integer.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath producer = createString("producer");

    public final StringPath productMainExplanation = createString("productMainExplanation");

    public final StringPath productSubExplanation = createString("productSubExplanation");

    public final StringPath purchaseInquiry = createString("purchaseInquiry");

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.member = inits.isInitialized("member") ? new ai.serverapi.domain.entity.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

