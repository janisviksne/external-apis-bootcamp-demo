package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

// This is an interface - implement it in BookRestClientImpl using RestClient
// (synchronous calls) to talk to the external book service.
public interface BookRestClient {

    BookDto getBook(Long id);

    List<BookDto> getAllBooks();
}
