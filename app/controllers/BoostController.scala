package controllers

import play.api.mvc.{Action, Controller}
import java.util.{TimeZone, Calendar}

/**
 * Created by suzuki on 2014/05/01.
 * 急上昇ランキング
 */
object BoostController extends Controller {


  def list(year: Int, month: Int, day: Int, hour: Int) = Action{
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.set(year, month-1, day, hour, 0)
    val calendar = Calendar.getInstance()
    calendar.set(year,month-1,day)
    val date = new java.sql.Date(calendar.getTimeInMillis)
    Ok(views.html.boost())
  }
}
