package com.logicaalternativa.monadtransformerandmore.business.impl;

import static com.logicaalternativa.monadtransformerandmore.monad.MonadContainerWrapper.wrap;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.logicaalternativa.monadtransformerandmore.bean.Book;
import com.logicaalternativa.monadtransformerandmore.bean.Chapter;
import com.logicaalternativa.monadtransformerandmore.bean.Summary;
import com.logicaalternativa.monadtransformerandmore.business.SrvSummaryContainer;
import com.logicaalternativa.monadtransformerandmore.container.Container;
import com.logicaalternativa.monadtransformerandmore.errors.Error;
import com.logicaalternativa.monadtransformerandmore.errors.impl.MyError;
import com.logicaalternativa.monadtransformerandmore.monad.MonadContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceAuthorContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceBookContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceChapterContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceSalesContainer;

public class SrvSummaryContainerImpl implements SrvSummaryContainer<Error> {
	
	private final ServiceBookContainer<Error> srvBook;
	private final ServiceSalesContainer<Error> srvSales;
	private final ServiceChapterContainer<Error> srvChapter;
	private final ServiceAuthorContainer<Error> srvAuthor;
	
	private final MonadContainer<Error> m;
	

	public SrvSummaryContainerImpl(ServiceBookContainer<Error> srvBook,
			ServiceSalesContainer<Error> srvSales,
			ServiceChapterContainer<Error> srvChapter,
			ServiceAuthorContainer<Error> srvAuthor, MonadContainer<Error> m) {
		super();
		this.srvBook = srvBook;
		this.srvSales = srvSales;
		this.srvChapter = srvChapter;
		this.srvAuthor = srvAuthor;
		this.m = m;
	}



	@Override
	public Container<Error, Summary> getSummary(Integer idBook) {
		
		final Container<Error, Summary> res = m.flatMap2(
				srvBook.getBook(idBook), 
				wrap( srvSales.getSales(idBook), m)
					.map( sales -> Optional.of(sales) )
					.recover( e -> Optional.empty() )
					.value(),				
				( book, salesO ) -> m.map2(
										m.sequence( getListChapters(book) ), 
										srvAuthor.getAuthor( book.getIdAuthor() ), 
										(chapter, author) -> new Summary(book, chapter, salesO, author)
									)
			);
		
		return m.recoverWith(res, error -> m.raiseError( new MyError("It is impossible to get book summary") ) );
			
	}

	private List<Container<Error, Chapter>> getListChapters(Book book) {
		
		final List<Container<Error, Chapter>> listFutCapter = book.getChapters()
			.stream()
			.map( chap -> srvChapter.getChapter(chap ) )
			.collect(Collectors.toList());
		
		return listFutCapter;
	}

}
