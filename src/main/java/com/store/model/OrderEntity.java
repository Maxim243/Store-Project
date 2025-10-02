package com.store.model;

import com.store.model.embeddable.ProductOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ElementCollection
    @CollectionTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private List<ProductOrder> products = new ArrayList<>();

    private Double totalOrderPrice;

    private LocalDateTime orderDateTime;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String shippingAddress;
}
