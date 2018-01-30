def sum( x: java.lang.Integer, y: java.lang.Integer ) : java.lang.Integer = {
  
   if ( x eq null ) {
    
      null
      
    } else if (  y eq null ) {
      
      null
      
    } else {
  
      x + y
    }
  
  } 
  
  
  def sumO( x: Option[Int], y: Option[Int] ) : Option[Int] = {
  
    x match {
      
        case Some( v ) => y match {
            case Some( vv ) => Some( v + vv )
            case None => None          
          }
        case None => None
      
    }
  
  } 
  
  
  def sumF( x: Option[Int], y: Option[Int] ) : Option[Int] = {
  
    x flatMap {
            
        v => y map {
            vv => v + vv          
        }
      
    }
  
  } 
  
  def sumF2( x: Option[Int], y: Option[Int] ) : Option[Int] = {
    
    for {
      
      v <- x
      
      vv <- y  
      
    } yield( v + vv )
    
  } 
  
   
  
  def sumFut( x: Future[Int], y: Future[Int] ) : Future[Int] = {
    
    for {
      
      v <- x
      
      vv <- y  
      
    } yield( v + vv )
    
  } 
  
  def sumFut[P[_]]( x: P[Int], y: P[Int] ) : P[Int] = {
  
    //~ for {
      //~ 
      //~ v <- x
      //~ 
      //~ vv <- y  
      //~ 
    //~ } yield( v + vv ) 
    
    ???  
    
  }
