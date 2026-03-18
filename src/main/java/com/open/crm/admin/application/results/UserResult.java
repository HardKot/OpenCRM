package com.open.crm.admin.application.results;

import com.open.crm.admin.entities.user.User;

public sealed interface UserResult
    permits UserResult.Ok,
        UserResult.NotFound,
        UserResult.InvalidData,
        UserResult.IsDeleted,
        UserResult.NoUniqueEmail {
  public record Ok(User value) implements UserResult {}

  public record NotFound() implements UserResult {}

  public record InvalidData(String message) implements UserResult {}

  public record IsDeleted() implements UserResult {}

  public record NoUniqueEmail() implements UserResult {}
}
