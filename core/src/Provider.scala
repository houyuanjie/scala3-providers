import TupleUtils.*

opaque type Provider[T] = T

object Provider:

  def provide[T](elem: T): Provider[T] = elem

  def provided[T](using provider: Provider[T]): T = provider

  given empty: Provider[EmptyTuple] = EmptyTuple

  given split[Elems <: Tuple, T](using elemsProvider: Provider[Elems], selector: Selector[Elems, T]): Provider[T] =
    selector.select(elemsProvider)

  given compose[T, Elems <: Tuple](using provider: Provider[T], elemsProvider: Provider[Elems]): Provider[T *: Elems] =
    provider *: elemsProvider
