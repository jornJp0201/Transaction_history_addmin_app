package com.crm.admin_app;

import org.springframework.data.domain.Page;      
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    // 名前（あいまい） AND アクティブ状態（完全一致）で検索
    Page<CustomerEntity> findByCustomerNameContainingAndActive(String customerName, Boolean active, Pageable pageable);

    Page<CustomerEntity> findByCustomerNameContaining(String customerName, Pageable pageable);
}

    
