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

@Component
public class BookRestClientImpl implements BookRestClient {

    private RestClient restClient;

    public BookRestClientImpl(RestClient.Builder builder, ExternalServiceProperties properties) {
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
    }

    @Override
    public BookDto getBook(Long id) {
        try {
            BookApiResponse response = restClient.get()
                    .uri("/books/{id}", id)
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

    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] responses = restClient.get()
                    .uri("/books")
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
