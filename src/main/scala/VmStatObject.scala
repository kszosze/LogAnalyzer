
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by kszos on 15/06/2016.
  */
class VmStatObject(nodeName: String, date: LocalDateTime) {

  var timeStamp = new ListBuffer[LocalDateTime]()
  var procs = mutable.Map("r" -> new ListBuffer[Number](),
    "b" -> new ListBuffer[Number]())
  var memory = mutable.Map(
    "swpd" -> new ListBuffer[Number](),
    "free" -> new ListBuffer[Number](),
    "buff" -> new ListBuffer[Number]())

  var swap = mutable.Map(
    "si" -> new ListBuffer[Number](),
    "so" -> new ListBuffer[Number](),
    "bi" -> new ListBuffer[Number]()
  )
  var io = mutable.Map(
    "bi" -> new ListBuffer[Number](),
    "bo" -> new ListBuffer[Number]()
  )
  var system = mutable.Map(
    "in" -> new ListBuffer[Number](),
    "cs" -> new ListBuffer[Number]()
  )
  var cpu = mutable.Map(
    "us" -> new ListBuffer[Number](),
    "sy" -> new ListBuffer[Number](),
    "id" -> new ListBuffer[Number](),
    "wa" -> new ListBuffer[Number](),
    "st" -> new ListBuffer[Number]()
  )

  def getNodeName(): String = nodeName

  def getDate(): LocalDateTime = date

  def addProcs(key: String, value: Number): Unit = {
    procs put(key, procs.get(key).get :+ value)
  }

  def addMemory(key: String, value: Number): Unit = {
    memory put(key, memory.get(key).get :+ value)
  }

  def addSwap(key: String, value: Number): Unit = {
    swap put(key, swap.get(key).get :+ value)
  }

  def addIo(key: String, value: Number): Unit = {
    io put(key, io.get(key).get :+ value)
  }

  def addSystem(key: String, value: Number): Unit = {
    system put(key, system.get(key).get :+ value)
  }

  def addCpu(key: String, value: Number): Unit = {
    cpu put(key, cpu.get(key).get :+ value)
  }

  def addTimeStamp(time: String): Unit = {
    this.timeStamp = this.timeStamp :+ LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  }
}
