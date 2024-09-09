import scala.compiletime.constValue
import scala.compiletime.ops.int.S

object TupleUtils:

  type IndexOf[Elems <: Tuple, T] <: Int =
    Elems match
      case T *: _    => 0
      case _ *: tail => S[IndexOf[tail, T]]

  trait Selector[Elems <: Tuple, T]:
    def select(elems: Elems): T

  given indexSelector[Elems <: NonEmptyTuple, T](using index: ValueOf[IndexOf[Elems, T]]): Selector[Elems, T] with
    def select(elems: Elems): T = elems(index.value).asInstanceOf[T]
