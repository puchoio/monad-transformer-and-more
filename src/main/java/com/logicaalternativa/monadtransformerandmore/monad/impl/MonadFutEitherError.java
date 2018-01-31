package com.logicaalternativa.monadtransformerandmore.monad.impl;

import static com.logicaalternativa.monadtransformerandmore.util.TDD.$_notYetImpl;

import java.util.function.Function;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;
import akka.dispatch.Futures;

import com.logicaalternativa.monadtransformerandmore.errors.Error;
import com.logicaalternativa.monadtransformerandmore.errors.impl.MyError;
import com.logicaalternativa.monadtransformerandmore.monad.MonadFutEither;
import static com.logicaalternativa.monadtransformerandmore.util.Java8.recoverF;

public class MonadFutEitherError implements MonadFutEither<Error> {
	
	final ExecutionContext ec;
	
	
	public MonadFutEitherError(ExecutionContext ec) {
		super();
		this.ec = ec;
	}

	@Override
	public <T> Future<Either<Error, T>> pure(T value) {
		
		return Futures.successful(new Right<>(value));
	}

	@Override
	public <A, T> Future<Either<Error, T>> flatMap(
			Future<Either<Error, A>> from,
			Function<A, Future<Either<Error, T>>> f) {

		// FutureEither<Error, A> from
		// A => FutureEither<Error,  B> 
		// =>
		// FutureEither<Error,  B>
		
		final Future<Either<Error, A>> recoverWith = recover( from );
		
		return recoverWith.flatMap(
				
				 ( aEither ) -> {
					
					if ( aEither.isLeft() ) {
						
						final Error error = aEither.left().get();
						return raiseError(  error );
						
					} else {
						
						final A  value = aEither.right().get();
						return f.apply( value );
						
					}
					
				},				
				ec);
		
	}
	@Override
	public <T> Future<Either<Error, T>> raiseError(Error error) {
		
		return Futures.successful(new Left<>( error ));
	}

	@Override
	public <T> Future<Either<Error, T>> recoverWith(
			Future<Either<Error, T>> from,
			Function<Error, Future<Either<Error, T>>> f) {
		
		
		Future<Either<Error, T>> recover = recover( from );
		
		return recover.flatMap(
				 
				 ( aEither ) -> {
					
					if ( aEither.isRight()) {
						
						final T value = aEither.right().get();
						return pure( value );
						
					} else {
						
						final Error  error = aEither.left().get();
						return f.apply( error );
						
					}
					
				},				
				ec);
		
	}
	


	private <A> Future<Either<Error, A>> recover(Future<Either<Error, A>> from) {
		
		final Future<Either<Error, A>> recoverWith = from.recoverWith(
				recoverF(
						t -> raiseError( new MyError( t.getMessage()))				
			    ),ec);
		
		return recoverWith;
	}

}
