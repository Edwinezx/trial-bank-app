package com.edwin.trial_bank_app.utils;

import com.edwin.trial_bank_app.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates structured, collision-resistant transaction references.
 * Format: {PREFIX}-{YYYYMMDD}-{HHmmss}-{SEQUENCE}
 * Example: TRF-20260615-143022-000001
 */
@Component
public class TransactionReferenceGenerator {

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FMT  = DateTimeFormatter.ofPattern("HHmmss");
    private final AtomicLong sequence = new AtomicLong(1);

    public String generate(TransactionType type) {
        String prefix = switch (type) {
            case TRANSFER   -> "TRF";
            case DEPOSIT    -> "DEP";
            case WITHDRAWAL -> "WDR";
            case REVERSAL   -> "REV";
        };

        LocalDateTime now = LocalDateTime.now();
        String seq = String.format("%06d", sequence.getAndIncrement() % 1_000_000);

        return prefix + "-" + now.format(DATE_FMT) + "-" + now.format(TIME_FMT) + "-" + seq;
    }
}
