package models


import scala.slick.driver.MySQLDriver.simple._
import java.sql.Date
import java.util.Calendar
import models._
import scala.slick.lifted.TableQuery

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
case class Count(title_id: Int, count: Int, date: Date, hour:Int, rank: Int)

class Titles(tag: Tag) extends Table[Title](tag,"title"){
  def id = column[Int]("id")
  def title = column[String]("title")
  def * = (id, title) <> (Title.tupled, Title.unapply)
}

class Counts(tag: Tag) extends Table[Count](tag,"stats"){
  def title_id = column[Int]("title_id")
  def count = column[Int]("count")
  def date = column[Date]("date")
  def hour = column[Int]("hour")
  def rank = column[Int]("rank")
  def * = (title_id, count, date, hour, rank) <> (Count.tupled, Count.unapply)
}

object Titles extends DAO{
  def findById(id: Int)(implicit s: Session): List[Title] = {
    Titles.filter{_.id === id}.list()
  }
}

object Counts extends DAO {

  def findByDate(date: Date, hour: Int)(implicit s: Session):List[(Int,String, Int, Int)] =
    datelist(date,hour).list()

  def findByDateLimit(date: Date, hour: Int)(implicit s: Session):List[(Int, Int,String, Int, Option[(Int,Int)])] = {
    val list = datelist(date, hour).take(100).list()
    for{
      a <- list
    } yield(a._4,a._1, a._2, a._3, diffCount(a._1, date, hour))
  }

  def diffCount(title_id: Int, date:Date, hour: Int)(implicit s: Session): Option[(Int,Int)] = {
    // 現在のカウント
    val count = getCount(title_id, date, hour)
    // 一時間前
    var preDate = new Date(0)
    var preHour = 0
    if(hour > 0){
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
      case Some(value) => Some((count.get.count -value.count, value.rank - count.get.rank))
      case None => None
    }
  }

  def getCount(title_id:Int, date:Date, hour:Int)(implicit s: Session): Option[Count] = {
    Counts.filter(_.title_id === title_id).filter(_.date === date).filter(_.hour === hour).firstOption
  }

  def orderCount(count: Int, date: Date, hour: Int)(implicit s: Session): Int =
    Counts.filter(_.date === date).filter(_.hour === hour).filter(_.count > count).list.length

  def datelist(date: Date, hour: Int)= for{
  //Counts.filter{_.hour === hour}.sortBy(_.count.desc).list()
    c <- Counts.sortBy(_.rank.asc)
    t <- Titles if c.title_id === t.id && c.date === date && c.hour === hour
  } yield(t.id, t.title, c.count, c.rank)
}