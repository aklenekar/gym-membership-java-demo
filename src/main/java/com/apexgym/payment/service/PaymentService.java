package com.apexgym.payment.service;

import com.apexgym.payment.dto.PaymentRequest;
import com.apexgym.profile.persistence.MembershipPlan;
import com.apexgym.profile.persistence.PricingRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final PricingRepository pricingRepository;

    public Map<String, String> createPaymentIntent(PaymentRequest request) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        MembershipPlan plan;
        try {
            plan = MembershipPlan.valueOf(request.plan().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid plan: " + request.plan());
        }

        // server-side price source of truth — never trust client-sent amount
        long amountInCents = pricingRepository.findByName(plan.name())
                .map(pricing -> pricing.getPrice() * 100)
                .orElseThrow(() -> new IllegalArgumentException("Invalid plan: " + request.plan()));

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .putMetadata("plan", plan.name())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        return response;
    }

    public ResponseEntity<String> handleStripeWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (intent != null) {
                String plan = intent.getMetadata().get("plan");
                log.info("Payment succeeded for plan {} (intent {})", plan, intent.getId());
            }
        }

        return ResponseEntity.ok("Success");
    }
}