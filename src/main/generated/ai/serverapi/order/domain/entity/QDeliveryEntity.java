package ai.serverapi.order.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDeliveryEntity is a Querydsl query type for DeliveryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryEntity extends EntityPathBase<DeliveryEntity> {

    private static final long serialVersionUID = -789641003L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDeliveryEntity deliveryEntity = new QDeliveryEntity("deliveryEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final QOrderEntity order;

    public final QOrderItemEntity orderItem;

    public final StringPath ownerAddress = createString("ownerAddress");

    public final StringPath ownerAddressDetail = createString("ownerAddressDetail");

    public final StringPath ownerName = createString("ownerName");

    public final StringPath ownerTel = createString("ownerTel");

    public final StringPath ownerZonecode = createString("ownerZonecode");

    public final StringPath recipientAddress = createString("recipientAddress");

    public final StringPath recipientAddressDetail = createString("recipientAddressDetail");

    public final StringPath recipientName = createString("recipientName");

    public final StringPath recipientTel = createString("recipientTel");

    public final StringPath recipientZonecode = createString("recipientZonecode");

    public final EnumPath<ai.serverapi.order.enums.DeliveryStatus> status = createEnum("status", ai.serverapi.order.enums.DeliveryStatus.class);

    public QDeliveryEntity(String variable) {
        this(DeliveryEntity.class, forVariable(variable), INITS);
    }

    public QDeliveryEntity(Path<? extends DeliveryEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDeliveryEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDeliveryEntity(PathMetadata metadata, PathInits inits) {
        this(DeliveryEntity.class, metadata, inits);
    }

    public QDeliveryEntity(Class<? extends DeliveryEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrderEntity(forProperty("order"), inits.get("order")) : null;
        this.orderItem = inits.isInitialized("orderItem") ? new QOrderItemEntity(forProperty("orderItem"), inits.get("orderItem")) : null;
    }

}

