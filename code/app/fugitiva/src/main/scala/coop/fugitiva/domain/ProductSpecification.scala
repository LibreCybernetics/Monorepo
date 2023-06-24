package coop.fugitiva.domain

case class ProductSpecification(
    id: ProductSpecificationId,
    owner: CooperativeId,
    name: String
)

case class ProductSpecificationInheritance(
    id: ProductSpecificationId,
    inheritsFrom: ProductSpecificationId
)

case class ProductSpecificationData(
    specification: ProductSpecification,
    parents: Set[ProductSpecification]
)

// ProductSpecification
// Pan -> Pan de Ajo   -> Pan de Ajo de 500gr
//  \--> Pan de 500gr /

