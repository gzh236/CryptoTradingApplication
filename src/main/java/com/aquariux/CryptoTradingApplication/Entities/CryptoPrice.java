package com.aquariux.CryptoTradingApplication.Entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_price")
@Data
@Builder
public class CryptoPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_seq")
    @SequenceGenerator(name = "price_seq", sequenceName = "price_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String cryptoPair;

    @Column(nullable = false)
    private BigDecimal bidPrice;

    @Column(nullable = false)
    private BigDecimal askPrice;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

}
