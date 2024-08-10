package com.aquariux.CryptoTradingApplication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_price")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
