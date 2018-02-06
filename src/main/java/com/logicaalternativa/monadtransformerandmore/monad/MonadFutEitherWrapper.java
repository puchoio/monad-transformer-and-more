package com.logicaalternativa.monadtransformerandmore.monad;

import java.util.function.Function;

import scala.concurrent.Future;
import scala.util.Either;

public class MonadFutEitherWrapper<E,T> {
	
	final Future<Either<E,T>> future;
	final MonadFutEither<E> m;
	
	private MonadFutEitherWrapper(Future<Either<E, T>> future,
			MonadFutEither<E> m) {
		super();
		this.future = future;
		this.m = m;
	}
	
	
	public <S> MonadFutEitherWrapper<E,S> map( Function<T, S> f ) {
				
		final Future<Either<E, S>> newFuture = m.map( future, f );
		
		return wrap( newFuture, m );
		
	}
	
	
	public MonadFutEitherWrapper<E,T> recover( Function<E, T> f ) {
		
		final Future<Either<E, T>> newFuture = m.recover( future, f );
		
		return wrap( newFuture, m );
	}
	
	public <S> MonadFutEitherWrapper<E,S> flatMap( Function<T, Future<Either<E,S>>> f ) {
		
		final Future<Either<E, S>> newFuture = m.flatMap( future, f );
		
		return wrap( newFuture, m );		
		
	}
	
	
	public MonadFutEitherWrapper<E,T> recoverWith( Function<E, Future<Either<E,T>>> f ) {
		
		final Future<Either<E, T>> newFuture = m.recoverWith( future, f );
		
		return wrap( newFuture, m );
	
	}	
	
	
	public static <E,T> MonadFutEitherWrapper<E,T> wrap(Future<Either<E, T>> future,
			MonadFutEither<E> m) {
		
		return new MonadFutEitherWrapper<E, T>(future, m);
		
	}
	
	public Future<Either<E,T>> value() {
		
		return future;
		
	}

}
