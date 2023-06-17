package coop.fugitiva.domain

opaque type ProductSpecificationId = Int

case class ProductSpecification(
    id: ProductSpecificationId,
    owner: CooperativeId,
    name: String
)

case class ProductSpecificationInheritance(
    id: ProductSpecificationId,
    parent: ProductSpecificationId
)

case class ProductData(
    specification: ProductSpecification,
    parents: Set[ProductData]
)

// ProductSpecification
// Pan -> Pan de Ajo
