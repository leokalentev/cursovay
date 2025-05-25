package hexlet.code.mapper;

import hexlet.code.dto.EventStatusCreateDTO;
import hexlet.code.dto.EventStatusDTO;
import hexlet.code.dto.EventStatusUpdateDTO;
import hexlet.code.model.EventStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EventStatusMapper {

    public abstract EventStatusDTO map(EventStatus eventStatus);

    public abstract EventStatus map(EventStatusCreateDTO dto);

    public abstract void update(EventStatusUpdateDTO dto, @MappingTarget EventStatus eventStatus);
}

