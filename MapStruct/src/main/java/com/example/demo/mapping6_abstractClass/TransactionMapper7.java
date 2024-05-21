package com.example.demo.mapping6_abstractClass;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper7 {

    @Mapping(source = "total", target = "totalInCents", qualifiedByName = "convertToCents")
    TransactionDTO transactionToTransactionDTO(Transaction transaction);

    // Custom mapping method
    default Long convertToCents(BigDecimal total) {
        return total != null ? total.multiply(BigDecimal.valueOf(100)).longValue() : null;
    }
}
