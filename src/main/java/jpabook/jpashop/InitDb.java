package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 총 2개의 주문
 * * userA
 *   * JPA1 BOOK
 *   * JPA2 BOOK
 *
 * * userB
 *   * SPRING1 BOOK
 *   * SPRING2 BOOK
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {
            Member member1 = createMember("userA", new Address("Seoul", "Yeonheero", "03644"));
            em.persist(member1);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member1.getAddress());
            Order order = Order.createOrder(member1, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member2 = createMember("userB", new Address("Seongnam", "Pangyoro", "03495"));
            em.persist(member2);

            Book book1 = createBook("SPRING1 BOOK", 30000, 300);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 400);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 30000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = new Delivery();
            delivery.setAddress(member2.getAddress());
            Order order = Order.createOrder(member2, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public Member createMember(String name, Address address) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(address);
            return member;
        }

        public Book createBook(String name, int price, int stockQuantiry) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantiry);
            return book;
        }

    }

}

