package services

object JsonToAvroConverter extends App {

  case class Propertie(name: String, value: String)

  case class Event(name: String, properties: Seq[Propertie])

  val schema = AvroSchema[Event]
  //print(schema.toString(true))

  val converter = new JsonToAvroConverter("com.example.kafkaorch")
  val string =
    """{
      |  "AEvent": {
      |    "name": "",
      |      {
      |        "key": "" ,
      |        "name": "",
      |        "date": ""
      |      }
      |    ]
      |  },
      |}""".stripMargin

  print(converter.convert("test", string).toString(true))

}
