package hexlet.code.mapper;

import hexlet.code.dto.CategoryCreateDTO;
import hexlet.code.dto.CategoryDTO;
import hexlet.code.dto.CategoryUpdateDTO;
import hexlet.code.model.Category;
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
public abstract class CategoryMapper {

    public abstract CategoryDTO map(Category category);

    public abstract Category map(CategoryCreateDTO dto);

    public abstract void update(CategoryUpdateDTO dto, @MappingTarget Category category);
}

