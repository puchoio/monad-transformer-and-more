package com.logicaalternativa.monadtransformerandmore.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import scala.Tuple2;
import scala.collection.immutable.Stream.StreamBuilder;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;

import com.logicaalternativa.monadtransformerandmore.bean.Author;
import com.logicaalternativa.monadtransformerandmore.bean.Book;
import com.logicaalternativa.monadtransformerandmore.bean.Chapter;
import com.logicaalternativa.monadtransformerandmore.bean.Sales;
import com.logicaalternativa.monadtransformerandmore.bean.Summary;
import com.logicaalternativa.monadtransformerandmore.business.SrvSummaryFutureEither;
import com.logicaalternativa.monadtransformerandmore.errors.Error;
import com.logicaalternativa.monadtransformerandmore.errors.impl.MyError;
import com.logicaalternativa.monadtransformerandmore.monad.MonadFutEither;
import com.logicaalternativa.monadtransformerandmore.monad.MonadFutEitherWrapper;

import static com.logicaalternativa.monadtransformerandmore.monad.MonadFutEitherWrapper.wrap;

import com.logicaalternativa.monadtransformerandmore.service.future.ServiceAuthorFutEither;
import com.logicaalternativa.monadtransformerandmore.service.future.ServiceBookFutEither;
import com.logicaalternativa.monadtransformerandmore.service.future.ServiceChapterFutEither;
import com.logicaalternativa.monadtransformerandmore.service.future.ServiceSalesFutEither;
import com.logicaalternativa.monadtransformerandmore.util.Java8;

import static com.logicaalternativa.monadtransformerandmore.util.Java8.*;
import static com.logicaalternativa.monadtransformerandmore.util.TDD.$_notYetImpl;

public class SrvSummaryFutureEitherImpl implements SrvSummaryFutureEither<Error> {

	private static final Left<Error, Summary> LEFTERROR = new Left<>( new MyError("It is impossible to get book summary") );
	private final ServiceBookFutEither<Error> srvBook;
	private final ServiceSalesFutEither<Error> srvSales;
	private final ServiceChapterFutEither<Error> srvChapter;
	private final ServiceAuthorFutEither<Error> srvAuthor;
	
	private final MonadFutEither<Error> m;
	
	
	public SrvSummaryFutureEitherImpl(ServiceBookFutEither<Error> srvBook,
			ServiceSalesFutEither<Error> srvSales,
			ServiceChapterFutEither<Error> srvChapter,
			ServiceAuthorFutEither<Error> srvAuthor,
			MonadFutEither<Error> m) {
		super();
		this.srvBook = srvBook;
		this.srvSales = srvSales;
		this.srvChapter = srvChapter;
		this.srvAuthor = srvAuthor;
		this.m = m;
	}

	@Override
	public Future<Either<Error, Summary>> getSummary(Integer idBook) {
		
//		final Future<Either<Error, Optional<Sales>>> salesOF = m.recover(
//				m.map( 
//					srvSales.getSales(idBook), 
//					sales -> Optional.of(sales) 
//				), 
//				e  -> Optional.empty() );
		
//		final Future<Either<Error, Sales>> salesF = srvSales.getSales(idBook);		
//		final Future<Either<Error, Optional<Sales>>> salesOFF = m.map(salesF, sales -> Optional.of(sales));
//		final Future<Either<Error, Optional<Sales>>> salesOF = m.recover(salesOFF, e -> Optional.empty());
		
		
		
		final Future<Either<Error, Summary>> res = m.flatMap2(
				srvBook.getBook(idBook), 
				wrap( srvSales.getSales(idBook), m)
						.map( sales -> Optional.of(sales) )
						.recover( e -> Optional.empty() )
						.value(),				
				( book, salesO ) ->  m.map2(
								m.sequence( getListChapters(book) ), 
								srvAuthor.getAuthor( book.getIdAuthor() ), 
								(chapter, author) -> new Summary(book, chapter, salesO, author)
								) 
			);
		
		return m.recoverWith(res, error -> m.raiseError( new MyError("It is impossible to get book summary") ) );
			
	}

	private List<Future<Either<Error, Chapter>>> getListChapters(Book book) {
		final List<Future<Either<Error, Chapter>>> listFutCapter = book.getChapters()
			.stream()
			.map( chap -> srvChapter.getChapter(chap ) )
			.collect(Collectors.toList());
		return listFutCapter;
	}	

	
}
