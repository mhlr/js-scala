package scala.js

import scala.virtualization.lms.common._
import java.io.PrintWriter
import scala.xml.Document
import scala.js.language.Debug
import scala.js.exp.DebugExp
import scala.js.gen.js.GenEqual
import scala.js.gen.js.GenDebug
import scala.js.gen.js.GenEffect
import scala.js.language.dom.Dom
import scala.js.exp.dom.DomExp
import scala.js.gen.js.dom.GenDom

class TestNodeList  extends FileDiffSuite {
  val prefix = "test-out/"
  
  trait DSL extends Base with Dom with Debug with Equal
  trait DSLExp extends DSL with DomExp with DebugExp with EqualExp
  trait DSLJSGen extends GenEffect with GenDom with GenDebug with GenEqual { val IR: DSLExp }

  def testOn() {

    trait Prog extends DSL {

      def testSize(ns: Rep[NodeList[Element]]) = ns.size
      def testFilter(ns: Rep[NodeList[Element]]) = ns.filter(n => n == 2)
      def testForeach(ns: Rep[NodeList[Element]]) = ns.foreach(n => log(n))
  
      def nodelist(ns: Rep[NodeList[Element]]) = {
        log(ns.size)
        log(testFilter(ns))
        testForeach(ns)
        ns.foreachWithIndex { (n, i) =>
          log(i)
        }
      }
    }
    withOutFile(prefix+"nodeList") {
      val prog = new Prog with DSLExp
      val codegen = new DSLJSGen { val IR: prog.type = prog }
      codegen.emitSource(prog.nodelist _, "nodeList", new PrintWriter(System.out))
    }
    assertFileEqualsCheck(prefix+"nodeList")
  }
}