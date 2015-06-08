package domain.entity

trait Entity[IdentityType <: Identity[_], AttrType] {
  val id: IdentityType
  val attr: AttrType
}

trait Identity[T] {
  val value: T
}

trait AttrType
