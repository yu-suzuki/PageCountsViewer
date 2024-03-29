package models

import models._
import scala.slick.lifted.TableQuery

private[models] trait DAO {
  val Titles = TableQuery[Titles]
  val Counts = TableQuery[Counts]
  val Diffs = TableQuery[Diffs]
  val CountDiffs = TableQuery[CountDiffs]
}