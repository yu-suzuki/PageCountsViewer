import play.Project._

name := "PageCountsViewer"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.30",
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.jumpmind.symmetric.jdbc" % "mariadb-java-client" % "1.1.1"
)
