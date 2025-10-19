package com.inditex.sisuprice.api.mapper;

import com.inditex.sisuprice.api.dto.PriceResponse;
import com.inditex.sisuprice.domain.PriceRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceRecordMapper {

    PriceResponse toResponse(PriceRecord record);
}
