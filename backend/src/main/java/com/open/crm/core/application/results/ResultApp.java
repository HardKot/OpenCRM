package com.open.crm.core.application.results;

public sealed interface ResultApp<T>
    permits ResultApp.Ok, ResultApp.NotFound, ResultApp.InvalidData, ResultApp.IsDeleted {
  public record Ok<T>(T value) implements ResultApp<T> {}

  public record NotFound<T>() implements ResultApp<T> {}

  public record InvalidData<T>(String message) implements ResultApp<T> {}

  public record IsDeleted<T>(String message) implements ResultApp<T> {}
}
