package models


import scala.slick.driver.MySQLDriver.simple._
import java.sql.Date
import java.util.Calendar

/*case class Counts(
                 id: ObjectId = new ObjectId,
                 year: Integer,
                 month: Integer,
                 day: Integer,
                 hour: Integer,
                 title: String,
                 count: Integer
                 )*/

case class Title(id: Int, title: String)

case class Count(title_id: Int, count: Int, date: Date, hour: Int, rank: Int)

case class Diff(title_id: Int, date: Date, hour: Int, count: Int, rank: Option[Int], rCount: Float, rRank: Float, r_count: Int, r_rank: Option[Int], r_rCount: Int, r_rRank: Int)

case class CountDiff(title_id: Int, date: Date, hour: Int, count: Int, rank: Int, d_count: Int, d_rank: Option[Int], rCount: Float, rRank: Float, r_count: Int, r_rank: Option[Int], r_rCount: Int, r_rRank: Int)

class Titles(tag: Tag) extends Table[Title](tag, "title") {
  def id = column[Int]("id")

  def title = column[String]("title")

  def * = (id, title) <>(Title.tupled, Title.unapply)
}

class Counts(tag: Tag) extends Table[Count](tag, "stats") {
  def title_id = column[Int]("title_id")

  def count = column[Int]("count")

  def date = column[Date]("date")

  def hour = column[Int]("hour")

  def rank = column[Int]("rank")

  def * = (title_id, count, date, hour, rank) <>(Count.tupled, Count.unapply)
}

class CountDiffs(tag: Tag) extends Table[CountDiff](tag, "stats_diff") {
  def title_id = column[Int]("title_id")
  def date = column[Date]("date")
  def hour = column[Int]("hour")
  def count = column[Int]("count")
  def rank = column[Int]("rank")
  def d_count = column[Int]("d_count")
  def d_rank = column[Option[Int]]("d_rank")
  def rCount = column[Float]("rCount")
  def rRank = column[Float]("rRank")
  def r_count = column[Int]("r_count")
  def r_rank = column[Option[Int]]("r_rank")
  def r_rCount = column[Int]("r_rCount")
  def r_rRank = column[Int]("r_rRank")
  def * = (title_id, date, hour, count, rank, d_count, d_rank, rCount, rRank, r_count, r_rank, r_rCount, r_rRank) <>(CountDiff.tupled, CountDiff.unapply)
}

class Diffs(tag: Tag) extends Table[Diff](tag, "diff") {
  def title_id = column[Int]("title_id")

  def date = column[Date]("date")

  def hour = column[Int]("hour")

  def count = column[Int]("count")

  def rank = column[Option[Int]]("rank")

  def rCount = column[Float]("rCount")

  def rRank = column[Float]("rRank")

  def r_count = column[Int]("r_count")

  def r_rank = column[Option[Int]]("r_rank")

  def r_rCount = column[Int]("r_rCount")

  def r_rRank = column[Int]("r_rRank")

  def * = (title_id, date, hour, count, rank, rCount, rRank, r_count, r_rank, r_rCount, r_rRank) <>(Diff.tupled, Diff.unapply)
}

object Titles extends DAO {
  def findById(id: Int)(implicit s: Session): List[Title] = {
    Titles.filter {
      _.id === id
    }.list()
  }
}

object Counts extends DAO {

  def findByDate(date: Date, hour: Int)(implicit s: Session): List[(Int, String, Int, Int, Int, Option[Int], Int)] =
    datelist(date, hour).list()

  def findByDateLimit(date: Date, hour: Int)(implicit s: Session): List[(Int, Int, String, Int, Int, Option[Int], Int)] = {
    val list = datelist(date, hour).take(100).list()
    for {
      a <- list
    //} yield(a._4,a._1, a._2, a._3, diffCount(a._1, date, hour))
    } yield (a._4, a._1, a._2, a._3, a._5, a._6, a._7)
  }

  def diffCount(title_id: Int, date: Date, hour: Int)(implicit s: Session): Option[(Int, Int)] = {
    // 現在のカウント
    val count = getCount(title_id, date, hour)
    // 一時間前
    var preDate = new Date(0)
    var preHour = 0
    if (hour > 0) {
      preDate = date
      preHour = hour - 1
    } else {
      preHour = 23
      val cal = Calendar.getInstance
      cal.setTimeInMillis(date.getTime)
      cal.add(Calendar.DAY_OF_YEAR, -1)
      preDate = new Date(cal.getTimeInMillis)
    }
    val preCount = getCount(title_id, preDate, preHour)
    preCount match {
      case Some(value) => Some((count.get.count - value.count, value.rank - count.get.rank))
      case None => None
    }
  }

  def getCount(title_id: Int, date: Date, hour: Int)(implicit s: Session): Option[Count] = {
    Counts.filter(_.title_id === title_id).filter(_.date === date).filter(_.hour === hour).firstOption
  }

  def orderCount(count: Int, date: Date, hour: Int)(implicit s: Session): Int =
    Counts.filter(_.date === date).filter(_.hour === hour).filter(_.count > count).list.length

  def datelist(date: Date, hour: Int) = for {
  //Counts.filter{_.hour === hour}.sortBy(_.count.desc).list()
  //((c, d), t) <- Counts.filter(_.date === date).filter(_.hour === hour).sortBy(_.rank.asc).take(100) leftJoin Diffs.filter(_.date===date).filter(_.hour===hour) on (_.title_id === _.title_id) leftJoin Titles on(_._1.title_id === _.id)
    //((c, t), d) <- {Counts.filter(row => (row.date === date) && (row.hour === hour)).sortBy(_.rank.asc).take(100) leftJoin Titles on ((ri, ti) => ri.title_id === ti.id) leftJoin  Diffs.filter(row => (row.date === date) && (row.hour === hour)) on ((ai,di) => (ai._1.title_id === di.title_id) && (ai._1.date === di.date) && (ai._1.hour === di.hour) )}
    d <- CountDiffs.filter(row => (row.date === date) && (row.hour === hour)).sortBy(_.rank.asc).take(100)
    t <-  Titles if t.id === d.title_id
  } yield (t.id, t.title, d.count, d.rank, d.d_count, d.d_rank, d.r_rRank)
}

object Diffs extends DAO {

}