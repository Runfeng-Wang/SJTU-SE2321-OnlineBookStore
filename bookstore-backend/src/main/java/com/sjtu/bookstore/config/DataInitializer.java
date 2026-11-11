package com.sjtu.bookstore.config;

import com.sjtu.bookstore.entity.Book;
import com.sjtu.bookstore.entity.User;
import com.sjtu.bookstore.repository.BookRepository;
import com.sjtu.bookstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(BookRepository bookRepository, UserRepository userRepository) {
        return args -> {
            ensureAdmin(userRepository);

            if (bookRepository.count() > 0) {
                bookRepository.findAll().forEach(book -> {
                    if (book.getStock() == null) {
                        book.setStock(30);
                    }
                    if (book.getPublisher() == null) {
                        book.setPublisher("未知出版社");
                    }
                    if (book.getIsbn() == null) {
                        book.setIsbn("");
                    }
                    bookRepository.save(book);
                });
                return;
            }
            bookRepository.save(createBook(
                    "Java核心技术", "凯·S.霍斯特曼", "95.20", "编程",
                    "/9668da63666960defb0bf63339ca1116.jpg",
                    "9787111547426", 40, "机械工业出版社",
                    "本书是Java领域的经典之作，深入探讨了Java的高级特性，包括流、XML处理、网络、数据库编程等内容。适合有经验的开发者深入学习 Java SE。"
            ));
            bookRepository.save(createBook(
                    "三体", "刘慈欣", "45.00", "科幻", "/santi.jpg",
                    "9787536692930", 60, "重庆出版社",
                    "《三体》是刘慈欣创作的系列长篇科幻小说。作品讲述了地球人类文明和三体文明的信息交流、生死搏杀及两个文明在宇宙中的兴衰历程。"
            ));
            bookRepository.save(createBook(
                    "活着", "余华", "28.00", "文学", "/huozhe.jpg",
                    "9787506365437", 80, "作家出版社",
                    "《活着》讲述了主人公福贵在时代变迁与苦难命运中，顽强生存、直面人生的故事，展现了生命的韧性与平凡生活的力量。"
            ));
        };
    }

    private void ensureAdmin(UserRepository userRepository) {
        User admin = userRepository.findByUsername("admin").orElseGet(() -> {
            User created = new User();
            created.setUsername("admin");
            created.setPassword("admin123");
            return created;
        });
        admin.setEmail("admin@bookstore.local");
        admin.setRole("ADMIN");
        admin.setEnabled(true);
        userRepository.save(admin);
    }

    private Book createBook(String title, String author, String price,
                            String category, String image, String isbn, Integer stock,
                            String publisher, String description) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(new BigDecimal(price));
        book.setCategory(category);
        book.setImage(image);
        book.setIsbn(isbn);
        book.setStock(stock);
        book.setPublisher(publisher);
        book.setDescription(description);
        return book;
    }
}
