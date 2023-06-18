package coop.fugitiva

import scala.annotation.targetName
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import io.getquill.{PostgresJAsyncContext, Quoted, SnakeCase}

given PostgresContext: PostgresJAsyncContext[SnakeCase] =
  new PostgresJAsyncContext(SnakeCase, "quill")
