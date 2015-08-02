package domain.entity

trait Entity[IdentityType <: Identity[_], AttrType <: EntityAttr] {
  val id: IdentityType
  val attr: AttrType
}

trait Identity[T] {
  val value: T
}

trait EntityAttr
