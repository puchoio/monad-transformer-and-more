package com.logicaalternativa.monadtransformerandmore
package business

import monad._
import bean._
import service._

import monad.syntax.Implicits._

import collection.JavaConverters._

import java.util.Optional

case class AuthorChapter( author: Author, listChapter : List[Chapter] )

trait SrvSummaryF[E,P[_]] {
  
  implicit val E : Monad[E,P]
  
  import E._
  
  val srvBook : ServiceBookF[E,P]
  val srvSales : ServiceSalesF[E,P]
  val srvChapter : ServiceChapterF[E,P]
  val srvAuthor : ServiceAuthorF[E,P]
  
  def getSummary( idBook: Int) : P[Summary] = {
    
    for {
        
        book <- srvBook getBook idBook        
        sales <- srvSales getSales( idBook )
        authorChapter <- getAuthorChapter( book )
        
    } yield( new Summary( book, authorChapter.listChapter.asJava, Optional.of( sales ), authorChapter.author) )
    
    
  } 
  
  def getAuthorChapter( book: Book ) : P[AuthorChapter] = {
  
      for {
        
        author <- srvAuthor.getAuthor( book.getIdAuthor )
        listChapter <- getListChapter( book.getChapters.asScala.toList )
        
      } yield( AuthorChapter(author, listChapter ) ) 
    
    
  }
  
  def getListChapter( chapters : List[java.lang.Long] ) : P[List[Chapter]] = ???
  
  
  protected[SrvSummaryF] def getGenericError( s : String ) : E
  
}
