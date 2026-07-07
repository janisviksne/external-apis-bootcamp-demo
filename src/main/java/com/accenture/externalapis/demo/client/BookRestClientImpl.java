package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.awt.print.Book;
import java.util.List;

// TODO: Make this class implement BookRestClient.
@Component
public class BookRestClientImpl implements BookRestClient {

    private RestClient restClient;

    public BookRestClientImpl(RestClient.Builder builder, ExternalServiceProperties properties) {
        // TODO: Build the RestClient using builder.baseUrl(properties.baseUrl()).build()
        // and assign it to this.restClient
        //
        // Optional/bonus: this service doesn't require auth, but in a real API you would
        // often also add builder.defaultHeader("Authorization", "Bearer " + token) here.
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
    }

    // TODO: Implement getBook(Long id) - fetch one book from GET /books/{id} as a
    // BookApiResponse, then map it onto a BookDto (only keep the fields BookDto needs).
    //
    // TODO: Handle the main RestClient error cases and rethrow them as ClientException:
    //  - HttpClientErrorException (4xx, e.g. book not found)
    //  - HttpServerErrorException (5xx, e.g. the faulty/teapot book)
    //  - ResourceAccessException (connection refused / timeout - the external service is unreachable)
    @Override
    public BookDto getBook(Long id) {
        try {
            BookApiResponse response = restClient.get()
                    .uri("/api/books/{id}", id)
                    .retrieve()
                    .body(BookApiResponse.class);

            return toDto(response);
        } catch (HttpClientErrorException e){
            throw new ClientException(
                    "Client error fetching book " + id +": " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e){
            throw new ClientException(
                    "EXternal service error fetching book " + id + ": " + e.getStatusCode(), e
            );
        }catch (ResourceAccessException e){
            throw new ClientException(
                    "Could not reach external service while fetching book " + id, e);
        } catch (Exception e){
            throw new ClientException("Unexpected error fetching book " + id, e);
        }
    }

    // TODO: Implement getAllBooks() - fetch all books from GET /books as
    // BookApiResponse[], then map each one onto a BookDto. Handle the same error
    // cases as getBook() above.
    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] responses = restClient.get()
                    .uri("/api/books")
                    .retrieve()
                    .body(BookApiResponse[].class);
            if(responses == null){
                return List.of();
            }
            return List.of(responses).stream().map(this::toDto).toList();
        } catch (HttpClientErrorException e){
            throw new ClientException(
                    "Client error fetching all books: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e){
            throw new ClientException(
                    "External service error fetching all books: " + e.getStatusCode(), e
            );
        }catch (ResourceAccessException e){
            throw new ClientException(
                    "Could not reach external service while fetching all books", e);
        } catch (Exception e){
            throw new ClientException("Unexpected error fetching all book", e);
        }
    }


    private BookDto toDto(BookApiResponse response) {
        if (response == null) {
            return null;
        }
        return new BookDto(
                response.title(),
                response.author(),
                response.genre(),
                response.price()
        );
    }
}
