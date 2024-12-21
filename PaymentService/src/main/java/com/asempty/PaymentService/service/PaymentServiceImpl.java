package com.asempty.PaymentService.service;

import com.asempty.PaymentService.dto.PaymentMode;
import com.asempty.PaymentService.dto.PaymentRequest;
import com.asempty.PaymentService.dto.PaymentResponse;
import com.asempty.PaymentService.entity.TransactionDetails;
import com.asempty.PaymentService.repository.TransactionDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final TransactionDetailRepository transactionDetailRepository;
    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        TransactionDetails transactionDetails =
                TransactionDetails.builder()
                        .paymentDate(Instant.now())
                        .paymentMode(paymentRequest.getPaymentMode().name())
                        .paymentStatus("SUCCESS")
                        .orderId(paymentRequest.getOrderId())
                        .referenceNumber(paymentRequest.getReferenceNumber())
                        .amount(paymentRequest.getAmount())
                        .build();
        transactionDetailRepository.save(transactionDetails);
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailByOrderId(long orderId) {
        log.info("PaymentDetail service called by orderId: {}", orderId);
        TransactionDetails transactionDetails = transactionDetailRepository.findByOrderId(orderId);
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentDate(transactionDetails.getPaymentDate())
                .paymentId(transactionDetails.getId())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .orderId(transactionDetails.getOrderId())
                .status(transactionDetails.getPaymentStatus())
                .amount(transactionDetails.getAmount())
                .build();
        return paymentResponse;
    }
}
