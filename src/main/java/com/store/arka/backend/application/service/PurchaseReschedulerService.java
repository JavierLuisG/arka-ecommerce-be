package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.out.IPurchaseAdapterPort;
import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.model.Purchase;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markPurchaseAsRescheduled(Purchase purchase) {
    purchase.reschedule();
    purchaseAdapterPort.saveUpdatePurchase(purchase);
    log.info("[PURCHASE_RESCHEDULED][MARK_AS_RESCHEDULED] User(id={}) has marked as Purchase(id={}) as {} due to discrepancies",
        securityUtils.getCurrentUserId(), purchase.getId(), PurchaseStatus.RESCHEDULED);
  }
}
