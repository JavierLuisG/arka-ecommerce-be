package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.out.IPurchaseAdapterPort;
import com.store.arka.backend.domain.model.Purchase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseReschedulerService {
  private final IPurchaseAdapterPort purchaseAdapterPort;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markPurchaseAsRescheduled(Purchase purchase) {
    purchase.reschedule();
    purchaseAdapterPort.saveUpdatePurchase(purchase);
    log.info("[PURCHASE_RESCHEDULER][RESCHEDULED] Purchase {} marked as RESCHEDULED due to discrepancies", purchase.getId());
  }
}
