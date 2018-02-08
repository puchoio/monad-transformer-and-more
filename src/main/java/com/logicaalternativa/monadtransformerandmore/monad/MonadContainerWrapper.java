package com.logicaalternativa.monadtransformerandmore.monad;

import java.util.function.Function;

import com.logicaalternativa.monadtransformerandmore.container.Container;

public class MonadContainerWrapper<E,T> {
	
	final Container<E,T> container;
	final MonadContainer<E> m;
	
	private MonadContainerWrapper(Container<E,T> future,
			MonadContainer<E> m) {
		super();
		this.container = future;
		this.m = m;
	}
	
	
	public <S> MonadContainerWrapper<E,S> map( Function<T, S> f ) {
				
		final Container<E, S> newContainer = m.map( container, f );
		
		return wrap( newContainer, m );
		
	}
	
	
	public MonadContainerWrapper<E,T> recover( Function<E, T> f ) {
		
		final Container<E, T> newContainer = m.recover( container, f );
		
		return wrap( newContainer, m );
	}
	
	public <S> MonadContainerWrapper<E,S> flatMap( Function<T, Container<E,S>> f ) {
		
		final Container<E, S> newContainer = m.flatMap( container, f );
		
		return wrap( newContainer, m );		
		
	}
	
	
	public MonadContainerWrapper<E,T> recoverWith( Function<E, Container<E,T>> f ) {
		
		final Container<E, T> newContainer = m.recoverWith( container, f );
		
		return wrap( newContainer, m );
	
	}	
	
	
	public static <E,T> MonadContainerWrapper<E,T> wrap(Container<E, T> future,
			MonadContainer<E> m) {
		
		return new MonadContainerWrapper<E, T>(future, m);
		
	}
	
	public Container<E,T> value() {
		
		return container;
		
	}

}
