package com.inditex.sisuprice.infrastructure.mapper;

import com.inditex.sisuprice.domain.PriceRecord;
import com.inditex.sisuprice.infrastructure.persistence.jpa.PriceEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper to convert JPA PriceEntity to domain PriceRecord.
 */
@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

    PriceRecord toDomain(PriceEntity entity);

}
