package dev.librecybernetics.foaf

trait Group[A <: Agent] extends Organization {
  lazy val members: Set[A]
}
