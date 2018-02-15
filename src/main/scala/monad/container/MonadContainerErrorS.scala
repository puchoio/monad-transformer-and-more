package com.logicaalternativa.monadtransformerandmore
package monad
package container

import com.logicaalternativa.monadtransformerandmore.container._
import errors._
import errors.impl._

import MonadContainerErrorS.ContainerError

object MonadContainerErrorS {
  
    type ContainerError[T] =  Container[Error,T]
    
    def apply() = new MonadContainerErrorS
  
}

class MonadContainerErrorS extends Monad[Error, ContainerError] {
  
  def pure[T]( value : T ) : ContainerError[T] = Container.value( value )
  
  def flatMap[A,T]( from : ContainerError[A], f : (A) => ContainerError[T] ) : ContainerError[T] = {
    
      if ( from.isOk ){
        
        execute( f( from.getValue ) )
        
      } else {
          
         raiseError( new MyError( from.getError.getDescription ) )
        
      }
    
    
  }
  
  def raiseError[T] ( error: Error ) : ContainerError[T] = Container.error( error )

  def recoverWith[T]( from : ContainerError[T], f : (Error) => ContainerError[T] ) : ContainerError[T] = {
    
    if( ! from.isOk ) {
      
      execute( f( from.getError ) )      
      
    } else {
      
      pure( from.getValue )
      
    }
    
  }
  
  def execute[T]( cont : => ContainerError[T] ) : ContainerError[T] = {
    
    try {
        
      cont
      
    } catch {
      
        case t : Throwable => raiseError( new MyError ( t.getMessage ) )
      
    }
    
  }
  
}
