package bound

import f0._
import Writers._
import Readers._

object BoundSerialization {

  def varW[B,F1,F,F2](wb: Writer[B,F1], wf: Writer[F,F2]): Writer[Var[B,F],S2[F1,F2]] =
    s2W(wb,wf)((a,b) => _.fold(a,b))

  def varR[B,F1,F,F2](rb: Reader[B,F1], rf: Reader[F,F2]): Reader[Var[B,F],S2[F1,F2]] =
    eitherR(rb, rf).map(scalaz.\/.fromEither)

  trait Writer1[F[+_]] { def apply[A](wa: Writer[A, DynamicF]): Writer[F[A], DynamicF] }
  trait Reader1[F[+_]] { def apply[A](wa: Reader[A, DynamicF]): Reader[F[A], DynamicF] }

  def scopeW[B, BF, F[+_], V, VF](
    pb: Writer[B, BF],
    pf: Writer1[F],
    pv: Writer[V, VF]
  ): Writer[Scope[B, F, V], DynamicF] =
    pf(varW(pb.erase, pf(pv.erase)).erase).cmap((s: Scope[B, F, V]) => s.unscope)

  def scopeR[B, BF, F[+_], V, VF](
    pb: Reader[B, BF],
    pf: Reader1[F],
    pv: Reader[V, VF]
  ): Reader[Scope[B, F, V], DynamicF] =
    pf(varR(pb.erase, pf(pv.erase)).erase).map(Scope(_))
}
