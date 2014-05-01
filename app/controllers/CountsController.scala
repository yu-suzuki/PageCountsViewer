package controllers

import play.api._
import play.api.mvc._
import models._
import scala.collection.immutable.Map
import java.util._
import play.api.db.slick._

object CountsController extends Controller {

  def index = Action {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.add(Calendar.HOUR_OF_DAY, -1)
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)+1
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    Redirect("/hour?year="+year+"&month="+month+"&day="+day+"&hour="+hour)
  }

  def hour(year:Int, month:Int, day:Int, hour:Int) = DBAction{implicit rs =>
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.set(year, month-1, day, hour, 0)
    val calendar = Calendar.getInstance()
    calendar.set(year,month-1,day)
    val date = new java.sql.Date(calendar.getTimeInMillis)
    val counts = models.Counts.findByDateLimit(date, hour)

    val calJfrom, calJto = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calJfrom.set(year, month-1, day, hour-1, 0)
    calJto.set(year,month-1,day,hour,0)
  calJto.add(Calendar.HOUR_OF_DAY, 9);
    calJfrom.add(Calendar.HOUR_OF_DAY, 9);
    Ok(views.html.lists(counts, calJfrom, calJto, calJfrom, calJto))
  }
}