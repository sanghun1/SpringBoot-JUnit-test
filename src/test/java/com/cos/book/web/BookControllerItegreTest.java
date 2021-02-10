package com.cos.book.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookControllerItegreTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	private void init() {
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();
	}

	@Test
	public void save_테스트() throws Exception{
		//given
		Book book = new Book(null, "1", 1.0, 10000.0);
		String content = new ObjectMapper().writeValueAsString(book);

		ResultActions resultActions = mockMvc.perform(post("/book").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content).accept(MediaType.APPLICATION_JSON_UTF8));

		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.title").value("제목1"))
					 .andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void findAll_테스트() throws Exception {
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "제목1", 1.0, 10000.0));
		books.add(new Book(null, "제목2", 2.2, 20000.0));
		books.add(new Book(null, "제목3", 3.3, 30000.0));
		bookRepository.saveAll(books);

		ResultActions resultActions = mockMvc.perform(get("/book").accept(MediaType.APPLICATION_JSON_UTF8));

		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.[0].title").value("제목1"))
					 .andDo(MockMvcResultHandlers.print());

	}



	@Test
	public void findById_테스트() throws Exception{
		int id = 1;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "제목1", 1.0, 10000.0));
		bookRepository.saveAll(books);

		ResultActions resultAction = mockMvc.perform(get("/book/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8));

		resultAction.andExpect(status().isOk())
					.andExpect(jsonPath("$.title").value("제목1"))
					.andDo(MockMvcResultHandlers.print());	
	}


	@Test
	public void update_테스트() throws Exception {
		int id = 1;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "제목1", 1.0, 10000.0));
		books.add(new Book(null, "제목2", 2.0, 20000.0));
		books.add(new Book(null, "제목3", 3.0, 30000.0));
		bookRepository.saveAll(books);

		Book book = new Book(null, "제목0", 0.0, 0.0);
		String content = new ObjectMapper().writeValueAsString(book);

		ResultActions resultAction = mockMvc.perform(put("/book/{id}", id).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content).accept(MediaType.APPLICATION_JSON_UTF8));

		resultAction.andExpect(status().isOk())
					.andExpect(jsonPath("$.title").value("제목0"))
					.andDo(MockMvcResultHandlers.print());	
	}


	@Test
	public void delete_테스트() throws Exception{
		int id = 3;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "제목1", 1.0, 10000.0));
		books.add(new Book(null, "제목2", 2.0, 20000.0));
		books.add(new Book(null, "제목3", 3.0, 30000.0));
		bookRepository.saveAll(books);

		ResultActions resultAction = mockMvc.perform(delete("/book/{id}", id).contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8));

		resultAction.andExpect(status().isOk())
					.andExpect(jsonPath("$.title").value("제목3"))
					.andDo(MockMvcResultHandlers.print());	
	}
}