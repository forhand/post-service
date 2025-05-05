package org.example.mapper;

import org.example.dto.PostDto;
import org.example.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

  PostDto toDto(Post entity);
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "numberViews", ignore = true)
  Post toEntity(PostDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "numberViews", ignore = true)
  void update(@MappingTarget Post post, PostDto postDto);

  List<PostDto> entitiesToDtos(List<Post> entities);
}