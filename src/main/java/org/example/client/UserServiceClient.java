package org.example.client;

import org.example.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${client.user-service.name}", url = "${client.user-service.host}:${client.user-service.port}",
        path = "${client.user-service.base-path}")
public interface UserServiceClient {

  @GetMapping("/{userId}")
  UserDto getUser(@PathVariable long userId);

  @GetMapping("/list")
  List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

  @PutMapping("/{userId}/diactivate")
  UserDto deactivatesUserProfile(@PathVariable Long userId);

}