package com.example.demo.materials;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.inventories.Inventory;
import com.example.demo.inventories.InventoryRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class MaterialsService {
	
	private  final InventoryRepository   InventoryRepo;
	private  final MaterialRepository   MaterialRepo;
	//查詢全部
	  public List<Material> getAllMaterials() {
	        
		  
		  
		  return MaterialRepo.findAll();
	    }
	//用ID查詢指定
	  public Material findById(Long id) {
		  Optional<Material> m = MaterialRepo.findById(id);
		  
	        return m.get();
	               
	    }
	  public Material create(Material material) {

	        // 1. 儲存原物料
		  Material savedMaterial = MaterialRepo.save(material);

	        // 2. 建立對應的庫存
	        Inventory inventory = new Inventory();

	        inventory.setMaterial(savedMaterial);

	        // 初始庫存 = 0
	        inventory.setQuantity(BigDecimal.ZERO);

	        InventoryRepo.save(inventory);

	        // 3. 回傳建立好的原物料
	        return savedMaterial;
	    }
	  public Material update(Long id, Material newMaterial) {
		  Optional<Material> m = MaterialRepo.findById(id);
		  
	        Material material =m.get();

	        material.setCode(newMaterial.getCode());
	        material.setName(newMaterial.getName());
	        material.setUnit(newMaterial.getUnit());
	        material.setCost(newMaterial.getCost());

	        return MaterialRepo.save(material);
	    }
	  public void delete(Long id) {

	        Material material = findById(id);

	        // 先刪除庫存
	        Inventory inventory = InventoryRepo
	                .findByMaterialId(material.getId()).get();
	               

	        if (inventory != null) {
	        	InventoryRepo.delete(inventory);
	        }

	        // 再刪除原物料
	        MaterialRepo.delete(material);
	    }

}
