package dev.librecybernetics.ontology.social

trait Group[A <: Agent] extends Organization {
  lazy val members: Set[A]
}
