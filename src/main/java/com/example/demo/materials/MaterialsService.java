package com.example.demo.materials;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	
	
	//找全部書頁
	public Page<Material> getMaterials(Pageable pageable) {

	    return MaterialRepo.findAll(pageable);

	}
	
	
	
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

	   

	        // 3. 回傳建立好的原物料
	        return savedMaterial;
	    }
	  public Material update(Long id, Material newMaterial) {
		  Optional<Material> m = MaterialRepo.findById(id);
		  
	        Material material =m.get();


	        material.setName(newMaterial.getName());
	        material.setUnit(newMaterial.getUnit());
	        material.setCost(newMaterial.getCost());

	        return MaterialRepo.save(material);
	    }
	  public void delete(Long id) {

	        Material material = findById(id);

	        // 先刪除庫存
	      
	     

	        // 再刪除原物料
	        MaterialRepo.delete(material);
	    }

	  
	  public MaterialSummaryDTO getMaterialSummary() {

		    Long totalMaterials =
		    		 MaterialRepo.count();

		    BigDecimal averageCost =
		    		 MaterialRepo.findAverageCost();

		    Long safetyStockCount =
		    		 MaterialRepo.countSafetyStockMaterials();

		    Long unitCount =
		    		 MaterialRepo.countDistinctUnits();


		    if (averageCost == null) {
		        averageCost = BigDecimal.ZERO;
		    }


		    return new MaterialSummaryDTO(
		            totalMaterials,
		            averageCost,
		            safetyStockCount,
		            unitCount
		    );
		}
	  
	  
}
