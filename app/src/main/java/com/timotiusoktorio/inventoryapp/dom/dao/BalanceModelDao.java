package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.timotiusoktorio.inventoryapp.dom.objects.Balances;
import com.timotiusoktorio.inventoryapp.dom.objects.ProductBalance;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface BalanceModelDao {

    @Query("select SubCategory.name as subCategoryName,Product.name as productName ,Balances.balance as balance, Unit.name as unit from Balances " +
            "INNER JOIN Product ON Balances.product_id=Product.id " +
            "INNER JOIN Unit ON Product.unitId=Unit.id " +
            "INNER JOIN SubCategory ON Product.subcategoryId = Subcategory.id  ")
    LiveData<List<ProductBalance>> getBalances();

    @Query("select * from Balances WHERE Balances.product_id=:product_id")
    Balances getBalance(int product_id);


    @Insert(onConflict = REPLACE)
    void addBalance(Balances balances);

}
