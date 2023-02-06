package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        // given
        Member member = createMember("홍길동");
        Book book = createBook("셜록 홈즈 전집", 30000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order order = orderRepository.findOne(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getTotalPrice()).isEqualTo(book.getPrice() * orderCount);
        assertThat(8).isEqualTo(book.getStockQuantity());
    }

    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = createMember("홍길동");
        Book book = createBook("셜록 홈즈 전집", 30000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order order = orderRepository.findOne(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(10);

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        // given
        Member member = createMember("홍길동");
        Book book = createBook("셜록 홈즈 전집", 30000, 10);

        int orderCount = 100;

        // when
        assertThatThrownBy(
                () -> orderService.order(member.getId(), book.getId(), orderCount)
        ).isInstanceOf(NotEnoughStockException.class);

        // then
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "소공로", "10375"));
        em.persist(member);

        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);

        return book;
    }
}