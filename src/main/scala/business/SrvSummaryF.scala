package com.logicaalternativa.monadtransformerandmore
package business

import monad._
import bean._
import service._

import monad.syntax.Implicits._

import java.util.Optional

trait SrvSummaryF[E,P[_]] {
  
  implicit val E : Monad[E,P]
  
  import E._
  
  val srvBook : ServiceBookF[E,P]
  val srvSales : ServiceSalesF[E,P]
  val srvChapter : ServiceChapterF[E,P]
  val srvAuthor : ServiceAuthorF[E,P]
  
  def getSummary( idBook: Int) : P[Summary] = {
    
    for {
    
        book <- srvBook.getBook( idBook ) // book = srvBook.getBook( idBook ) 
      
      
    } yield( ??? )
    
    
  } 
  
  protected[SrvSummaryF] def getGenericError( s : String ) : E
  
}
