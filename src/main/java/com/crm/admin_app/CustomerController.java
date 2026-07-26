package com.crm.admin_app;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;




@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private ArrayList<String> emptyList = new ArrayList<>();//落ちないように空のリストを作成

    public CustomerController(CustomerService customerService){
         this.customerService = customerService;
    } 

    @GetMapping("/create")
        public String create(Model model){
            if (!model.containsAttribute("customerEntity")) {  
            model.addAttribute("customerEntity", new CustomerEntity());
        }
            return "create";
    }

     @GetMapping("/table")//ここにあとで顧客一覧を表示っせるためのmodelを作る
     public String getMethodName(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "active", required = false) Boolean active,
        @RequestParam(name = "sortField", defaultValue = "customerId") String sortField,
        @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
        @RequestParam(name = "page", defaultValue = "1") int page,
        Model model
    ) { //

       
        try{
            int pageSize = 5;

        // 検索・ソート・ページネーションを実行
             Page<CustomerEntity> resultList = customerService.searchCustomers(keyword, active, page, pageSize, sortField, sortDir);

             String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        // 💡 画面側に渡すデータ
            model.addAttribute("resultList", resultList.getContent()); // 現在のページのリストデータ
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", resultList.getTotalPages()); // 総ページ数
            model.addAttribute("totalItems", resultList.getTotalElements()); // 総データ件数
        
            model.addAttribute("keyword", keyword);
            model.addAttribute("active", active);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", reverseSortDir);
          
            model.addAttribute("successMessage","javaとの通信に成功しました");
            model.addAttribute("resultList", resultList);
            
            return "index";
        }catch(Exception e){
            System.err.println("============ ❌ 保存失敗のエラー内容 ============");
            e.printStackTrace(); 
            System.err.println("==================================================");
            model.addAttribute("errorMessage","javaとの通信に失敗しました");
            //model.addAttribute("re", emptyList);
            return "index";
        }
     }
     
    
     @PostMapping("/create/All/Save")
         public String save(
            @Valid @ModelAttribute("customerEntity") CustomerEntity customerEntity, // ① @Valid でチェック実行
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
         ){

            if (bindingResult.hasErrors()) {
                System.out.println("⚠️ 入力チェックエラーが発生しました（件数: " + bindingResult.getErrorCount() + "）");
            
                // 💡 ポイント: "redirect:" を使わず、直接 "create" (HTML) を返す！
                // これにより入力途中の値とエラー情報が保持されたまま登録画面が再表示される
                 model.addAttribute("errorSave", "不正な入力が検知されました。*がついているところは必須です。");
                return "create";
        }


            try{
                customerService.saveCustomer(customerEntity);   
                redirectAttributes.addFlashAttribute("successSave", "保存に成功しました");
                return "redirect:/customers/table";
            }catch(Exception e){
                System.err.println("============ ❌ 保存失敗のエラー内容 ============");
                e.printStackTrace(); 
                System.err.println("==================================================");
                model.addAttribute("errorSave", "保存に失敗しました");
                return "create";
            }
    }

    @GetMapping("/edit/{id}")
    public String edit(
        @PathVariable("id") Long id,
        Model model
    ) {
         try{
            CustomerEntity customer=customerService.getCustomerId(id);
            model.addAttribute("customerEntity",customer);
            return "edit";
             }catch(Exception e){
                System.err.println("============ ❌ 失敗のエラー内容 ============");
                e.printStackTrace(); 
                System.err.println("==================================================");
                model.addAttribute("errorMessage","アクセスに失敗しました");
                //model.addAttribute("re", emptyList);
                return "index";
        }
        
    }

    @DeleteMapping("/delete/{id}")
    public String getMethodName(
        @PathVariable("id") Long id,
        Model model
    ) {
        try {
            customerService.deleteCustomer(id);
            return "redirect:/customers/table";
        } catch (Exception e) {
            return "redirect:/customers/table";
        }
    }
    
    


}


