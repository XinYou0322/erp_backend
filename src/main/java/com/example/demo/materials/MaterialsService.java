package com.example.demo.materials;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

		    if ("CONVERSION".equals(material.getCostMode())) {

		        if (material.getConversionQuantity() == null ||
		            material.getConversionQuantity().compareTo(BigDecimal.ZERO) <= 0) {

		            throw new IllegalArgumentException("換算數量必須大於0");
		        }

		        if (material.getPurchaseCost() == null ||
		            material.getPurchaseUnit() == null ||
		            material.getPurchaseUnit().isBlank()) {

		            throw new IllegalArgumentException("採購成本、採購單位不得為空");
		        }

		        material.setCost(
		            material.getPurchaseCost().divide(
		                material.getConversionQuantity(),
		                4,
		                RoundingMode.HALF_UP
		            )
		        );

		    } else if ("DIRECT".equals(material.getCostMode())) {

		        if (material.getCost() == null) {
		            throw new IllegalArgumentException("成本不得為空");
		        }

		        material.setPurchaseUnit(null);
		        material.setConversionQuantity(null);
		        material.setPurchaseCost(null);

		    } else {

		        throw new IllegalArgumentException(
		            "成本模式只能是 DIRECT 或 CONVERSION"
		        );
		    }

		    Material savedMaterial = MaterialRepo.save(material);

		    return savedMaterial;
		}
	  
	  public Material update(Long id, Material newMaterial) {
		  Optional<Material> m = MaterialRepo.findById(id);
		  
	        Material material =m.get();


	        material.setName(newMaterial.getName());
	        material.setUnit(newMaterial.getUnit());
	        material.setSafetyStock(newMaterial.getSafetyStock());
	        material.setCostMode(newMaterial.getCostMode());
	        if("CONVERSION".equals(newMaterial.getCostMode())) {
	        	
	        	 if (newMaterial.getConversionQuantity() == null ||
	        			 newMaterial.getConversionQuantity().compareTo(BigDecimal.ZERO) <= 0) {

	 		            throw new IllegalArgumentException("換算數量必須大於0");
	 		        }

	 		        if (newMaterial.getPurchaseCost() == null ||
	 		        		newMaterial.getPurchaseUnit() == null ||
	 		        				newMaterial.getPurchaseUnit().isBlank()) {

	 		            throw new IllegalArgumentException("採購成本、採購單位不得為空");
	 		        }

	 		       material.setPurchaseUnit(newMaterial.getPurchaseUnit());
	 		      material.setConversionQuantity(newMaterial.getConversionQuantity());
	 		      material.setPurchaseCost(newMaterial.getPurchaseCost());
	 		
	 		        material.setCost(
	 		        		newMaterial.getPurchaseCost().divide(
	 		        				newMaterial.getConversionQuantity(),
	 		                4,
	 		                RoundingMode.HALF_UP
	 		            )
	 		        		
	 		        		
	 		        );
	        	
	        
	        }else if ("DIRECT".equals(newMaterial.getCostMode())) {
	        	
		        
		        material.setCost(newMaterial.getCost());
		        material.setPurchaseUnit(null);
		        material.setConversionQuantity(null);
		        material.setPurchaseCost(null);

	        }else {
	        	
	        	 throw new IllegalArgumentException(
	 		            "成本模式只能是 DIRECT 或 CONVERSION");
	 		        
	        }
	        
	        

	        
	        
	        
	        
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
