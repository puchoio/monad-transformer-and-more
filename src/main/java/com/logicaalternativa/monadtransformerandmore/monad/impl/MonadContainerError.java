package com.logicaalternativa.monadtransformerandmore.monad.impl;

import java.util.function.Function;

import com.logicaalternativa.monadtransformerandmore.container.Container;
import com.logicaalternativa.monadtransformerandmore.errors.Error;
import com.logicaalternativa.monadtransformerandmore.errors.impl.MyError;
import com.logicaalternativa.monadtransformerandmore.monad.MonadContainer;

public class MonadContainerError implements MonadContainer<Error> {

	@Override
	public <T> Container<Error, T> pure(T value) {
		
		return Container.value( value );
			
	}

	@Override
	public <A, T> Container<Error, T> flatMap(Container<Error, A> from,
			Function<A, Container<Error, T>> f) {
		
		
		Container<Error, T> cont = null;
		
		if( from.isOk() ) {
			
			try {
			 
				cont = f.apply(  from.getValue() );
				
			} catch( Throwable t  ) {
				
				cont = raiseError( new MyError(t.getMessage())  );
				
			}
			
			
			
		} else {
			
			cont = raiseError( from.getError() );
		}
		
		return cont;
	}

	@Override
	public <T> Container<Error, T> raiseError(Error error) {
		
		return Container.error( error );
		
	}

	@Override
	public <T> Container<Error, T> recoverWith(Container<Error, T> from,
			Function<Error, Container<Error, T>> f) {
		
		Container<Error, T> cont = null;
		
		if( ! from.isOk() ) {
			
			try {
			 
				cont = f.apply(  from.getError() );
				
			} catch( Throwable t  ) {
				
				cont = raiseError( new MyError(t.getMessage())  );
				
			}
			
			
			
		} else {
			
			cont = pure( from.getValue() );
		}
		
		return cont;
		
	}

	

}
