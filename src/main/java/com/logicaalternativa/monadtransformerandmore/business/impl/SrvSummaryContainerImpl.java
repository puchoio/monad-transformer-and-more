package com.logicaalternativa.monadtransformerandmore.business.impl;

import static com.logicaalternativa.monadtransformerandmore.util.TDD.$_notYetImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.logicaalternativa.monadtransformerandmore.bean.Author;
import com.logicaalternativa.monadtransformerandmore.bean.Book;
import com.logicaalternativa.monadtransformerandmore.bean.Chapter;
import com.logicaalternativa.monadtransformerandmore.bean.Sales;
import com.logicaalternativa.monadtransformerandmore.bean.Summary;
import com.logicaalternativa.monadtransformerandmore.business.SrvSummaryContainer;
import com.logicaalternativa.monadtransformerandmore.container.Container;
import com.logicaalternativa.monadtransformerandmore.monad.MonadContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceAuthorContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceBookContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceChapterContainer;
import com.logicaalternativa.monadtransformerandmore.service.container.ServiceSalesContainer;
import com.logicaalternativa.monadtransformerandmore.errors.Error;

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
		
		Book book = srvBook.getBook(idBook).getValue();
		
		Sales sales = srvSales.getSales(idBook).getValue();
		
	    Author author = srvAuthor.getAuthor(book.getIdAuthor()).getValue();
	    
	    List<Long> chapters2 = book.getChapters();
	    
		final List<Chapter> chapters = chapters2
	     .stream()
	     .parallel()
	     .map(  idChapter -> {
	    	 
	    	 Container<Error, Chapter> chapter = srvChapter.getChapter(idChapter);
	    	 return chapter.getValue();
	    	 
	     })
	     .collect(Collectors.toList())
	     ;	    
	    
	     Summary summary = new Summary(book, chapters, Optional.of(sales), author);
	     
	    
	    return Container.value(summary); 
		
	}

}
