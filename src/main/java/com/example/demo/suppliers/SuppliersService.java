package com.example.demo.suppliers;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuppliersService {
    
    private final SuppliersRepository suppliersRepo;

    //新增
    //email不重複才能新增
    public Suppliers insertSupplier(String name, Integer phone, String address, String email) {
            if (suppliersRepo.existsByEmail(email)) {
                throw new IllegalArgumentException("Email 已存在");
                //OR return "此email已存在"
    }

        Suppliers suppliers = new Suppliers();
        suppliers.setName(name);
        suppliers.setPhone(phone);
        suppliers.setAddress(address);
        suppliers.setEmail(email);
        return suppliersRepo.save(suppliers);
    }
    //查詢全部
    public List<Suppliers> listAllSuppliers(){
		return suppliersRepo.findAll();
	}

    //查詢單筆
    public Optional<Suppliers> findSupplierById(Long id){
		return suppliersRepo.findById(id);
	}

    

}
