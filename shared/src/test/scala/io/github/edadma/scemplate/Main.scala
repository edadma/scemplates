//package io.github.edadma.scemplate
//
//import io.github.edadma.datetime.Datetime
//import pprint.pprintln
//
//import scala.language.postfixOps
//
//object Main extends App {
//
//  case class Person(name: String, age: Int)
//
//  val data = Map("date" -> Datetime.now())
////    val data = Map("jonny" -> Person("jonny", 45))
//  //  val data = List(BigDecimal(3), BigDecimal(4), List(BigDecimal(7), BigDecimal(5)), BigDecimal(6))
////  val data = Datetime.now()
//  //  val input = "zxcv {{ with .jonny -}} name: {{ .name }} age: {{ .age }} {{- end }} asdf "
//  val input = "{{ .date.unix }}"
//  //  val input = "{{ .asdf }}"
//  val parser = new TemplateParser(input, "{{", "}}", Builtin.functions, Builtin.namespaces)
//  val ast = parser.parse
//
//  //  pprintln(ast)
//  println(Renderer.defaultRenderer.render(data, ast))
//
//}
//
//// todo: https://pkg.go.dev/text/template#hdr-Arguments
