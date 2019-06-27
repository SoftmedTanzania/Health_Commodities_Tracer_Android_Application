package com.softmed.stockapp.Dom;

import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.Unit;
import com.softmed.stockapp.Dom.entities.UsersInfo;
import com.softmed.stockapp.Dom.responces.CategoriesResponse;
import com.softmed.stockapp.Dom.responces.LoginResponse;
import com.softmed.stockapp.Dom.responces.ProductsResponse;
import com.softmed.stockapp.Dom.responces.SubCategoriesResponse;
import com.softmed.stockapp.Dom.responces.UnitsResponse;

import java.util.ArrayList;
import java.util.List;

public class DomConverter {

    public static UsersInfo getUserInfo(LoginResponse loginResponse){
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setFirstName(loginResponse.getUser().getFirstName());
        usersInfo.setMiddleName(loginResponse.getUser().getMiddleName());
        usersInfo.setSurname(loginResponse.getUser().getSurname());
        usersInfo.setId(loginResponse.getUser().getId());
        usersInfo.setHealth_facility(loginResponse.getUser().getProfile().getHealthFacility());
        usersInfo.setId(loginResponse.getUser().getId());
        usersInfo.setSurname(loginResponse.getUser().getUsername());

        return usersInfo;

    }

    public static Category getCategory(CategoriesResponse categoriesResponse){
        Category category = new Category();
        category.setId(categoriesResponse.getId());
        category.setName(categoriesResponse.getName());
        category.setDescription(categoriesResponse.getDescription());

        return category;

    }


    public static Product getProduct(ProductsResponse productsResponse){
        Product product = new Product();

        product.setId(productsResponse.getId());
        product.setName(productsResponse.getName());
        product.setDescription(productsResponse.getDescription());
        product.setCategory_id(productsResponse.getCategory_id());
        product.setUnit_id(productsResponse.getUnit());


        return product;
    }

    public static Unit getUnit(UnitsResponse unitsResponse){
        Unit unit = new Unit();

        unit.setId(unitsResponse.getId());
        unit.setDescription(unitsResponse.getDescription());
        unit.setName(unitsResponse.getName());


        return unit;
    }

}
