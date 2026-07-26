package com.crm.admin_app;
import java.util.List;

import org.springframework.data.domain.Page;      
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService{
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository costomerRepository){
        this.customerRepository = costomerRepository;
    }

    @Transactional
    public void saveCustomer(CustomerEntity customerEntity){//顧客用法の保存
        customerRepository.save(customerEntity);
    }
    @Transactional(readOnly = true)
    public  List<CustomerEntity> outputCustomer(){ //顧客情報の出力
        return customerRepository.findAll();
    }

    @Transactional
    public CustomerEntity getCustomerId(long id){
        CustomerEntity customerIn = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("指定された顧客IDが存在しません: " + id));
        return customerIn;
    }

    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<CustomerEntity> searchCustomers(String keyword, Boolean active, int page, int size, String sortField, String sortDir) {
        
        // 1. ソート方向の設定
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortField).ascending() 
                : Sort.by(sortField).descending();

        // 2. ページ情報（何ページ目か、1ページあたりの件数、ソート）をまとめる
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        String name = (keyword == null) ? "" : keyword;

        // 3. 検索実行（Pageオブジェクトが返ってくる）
        if (active != null) {
            return customerRepository.findByCustomerNameContainingAndActive(name, active, pageable);
        } else {
            return customerRepository.findByCustomerNameContaining(name, pageable);
        }
    }
}

