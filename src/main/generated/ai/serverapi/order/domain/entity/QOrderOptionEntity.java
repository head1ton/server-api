package ai.serverapi.order.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderOptionEntity is a Querydsl query type for OrderOptionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderOptionEntity extends EntityPathBase<OrderOptionEntity> {

    private static final long serialVersionUID = -505025912L;

    public static final QOrderOptionEntity orderOptionEntity = new QOrderOptionEntity("orderOptionEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> extraPrice = createNumber("extraPrice", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> optionId = createNumber("optionId", Long.class);

    public final EnumPath<ai.serverapi.product.enums.OptionStatus> status = createEnum("status", ai.serverapi.product.enums.OptionStatus.class);

    public QOrderOptionEntity(String variable) {
        super(OrderOptionEntity.class, forVariable(variable));
    }

    public QOrderOptionEntity(Path<? extends OrderOptionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderOptionEntity(PathMetadata metadata) {
        super(OrderOptionEntity.class, metadata);
    }

}

