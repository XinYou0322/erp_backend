package com.example.demo.salesOrder;

import org.springframework.stereotype.Service;

import com.example.demo.suppliers.SuppliersRepository;
import com.example.demo.suppliersNotes.SuppliersNotesRepository;
import com.example.demo.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrderItemService {

	
	//查詢某張訂單的所有明細
	//修改某筆訂單商品
	//刪除某筆訂單商品
	//單獨新增商品到尚未完成的訂單
}
