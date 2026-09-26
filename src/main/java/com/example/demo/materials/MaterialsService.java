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
	
	
	
	
	//查詢還可以使用的
	public List<Material> getActiveMaterials() {

	    return MaterialRepo.findByStatus("ACTIVE");
	}
	//找全部書頁
	public Page<Material> getMaterials(Pageable pageable) {

	    return MaterialRepo.findAllOrderByStatus(pageable);
	}

	public Page<Material> searchMaterials(
			String keyword,
			String status,
			String unit,
			Pageable pageable) {

		return MaterialRepo.searchMaterials(
				normalizeFilter(keyword),
				normalizeFilter(status),
				normalizeFilter(unit),
				pageable);
	}

	private String normalizeFilter(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
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
		    applyPurchasingDefaultsAndValidate(material);

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
		    material.setStatus("ACTIVE");

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
	        // 舊版前端尚未傳送這兩個欄位時，保留資料庫原值，避免編輯其他欄位時被清空。
	        if (newMaterial.getLeadTimeDays() != null) {
	        	validateLeadTimeDays(newMaterial.getLeadTimeDays());
	        	material.setLeadTimeDays(newMaterial.getLeadTimeDays());
	        }
	        if (newMaterial.getPurchasePackQuantity() != null) {
	        	validatePurchasePackQuantity(newMaterial.getPurchasePackQuantity());
	        	material.setPurchasePackQuantity(newMaterial.getPurchasePackQuantity());
	        }
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

	  private void applyPurchasingDefaultsAndValidate(Material material) {
		  if (material.getLeadTimeDays() == null) {
			  material.setLeadTimeDays(7);
		  }
		  validateLeadTimeDays(material.getLeadTimeDays());

		  if (material.getPurchasePackQuantity() == null) {
			  BigDecimal conversionQuantity = material.getConversionQuantity();
			  material.setPurchasePackQuantity(
					  conversionQuantity != null && conversionQuantity.signum() > 0
							  ? conversionQuantity
							  : BigDecimal.ONE);
		  }
		  validatePurchasePackQuantity(material.getPurchasePackQuantity());
	  }

	  private void validateLeadTimeDays(Integer leadTimeDays) {
		  if (leadTimeDays < 1 || leadTimeDays > 365) {
			  throw new IllegalArgumentException("交期天數必須介於 1 到 365 天之間");
		  }
	  }

	  private void validatePurchasePackQuantity(BigDecimal purchasePackQuantity) {
		  if (purchasePackQuantity.compareTo(BigDecimal.ZERO) <= 0) {
			  throw new IllegalArgumentException("採購包裝量必須大於 0");
		  }
	  }
	  public Material updateStatus(Long id, String status) {

		    // 1. 找原物料
		    Material material = MaterialRepo
		            .findById(id)
		            .orElseThrow(() ->
		                new IllegalArgumentException(
		                    "找不到原物料 id=" + id
		                )
		            );

		    // 2. 檢查傳進來的狀態是否合法
		    if (!"ACTIVE".equals(status)
		            && !"INACTIVE".equals(status)) {

		        throw new IllegalArgumentException(
		            "原物料狀態只能是 ACTIVE 或 INACTIVE"
		        );
		    }

		    // 3. 修改狀態
		    material.setStatus(status);

		    // 4. 儲存
		    return MaterialRepo.save(material);
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
