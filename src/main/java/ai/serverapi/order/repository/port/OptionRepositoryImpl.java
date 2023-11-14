package ai.serverapi.order.repository.port;

import ai.serverapi.product.domain.entity.OptionEntity;
import ai.serverapi.product.domain.model.Option;
import ai.serverapi.product.repository.OptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {

    private final OptionJpaRepository optionJpaRepository;

    @Override
    public Option save(final Option option) {
        return optionJpaRepository.save(OptionEntity.from(option)).toModel();
    }

    @Override
    public Option findById(final Long id) {
        OptionEntity optionEntity = optionJpaRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 옵션입니다."));
        return optionEntity.toModel();
    }
}
