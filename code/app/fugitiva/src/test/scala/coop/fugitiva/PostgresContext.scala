package coop.fugitiva

import io.getquill.{PostgresJAsyncContext, Quoted, SnakeCase}

given PostgresContext: PostgresJAsyncContext[SnakeCase] =
  new PostgresJAsyncContext(SnakeCase, "quill")
