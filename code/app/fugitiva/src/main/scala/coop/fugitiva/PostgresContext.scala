package coop.fugitiva

import io.getquill.{PostgresJAsyncContext, SnakeCase}

given PostgresContext: PostgresJAsyncContext[SnakeCase] = PostgresJAsyncContext(SnakeCase, "quill")
