package com.inn.restaurant.serviceImpl;

import com.google.common.base.Strings;
import com.inn.restaurant.JWT.CustomerUserDetailsService;
import com.inn.restaurant.JWT.JwtFilter;
import com.inn.restaurant.POJO.Category;
import com.inn.restaurant.constents.RestaurantConstants;
import com.inn.restaurant.dao.CategoryDao;
import com.inn.restaurant.service.CategoryService;
import com.inn.restaurant.utils.RestaurantUtils;
import com.inn.restaurant.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    com.inn.restaurant.JWT.jwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    EmailUtil emailUtil;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        log.info("Inside addNewCategory{}", requestMap);
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap , false));
                    return RestaurantUtils.getResponeEntity("Category Added Successfully", HttpStatus.OK);
                }
            }else{
                return RestaurantUtils.getResponeEntity(RestaurantConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //System.out.println(CafeConstants.SOMETHING_WENT_WRONG);
        return RestaurantUtils.getResponeEntity(com.inn.restaurant.constents.RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
        }
        return false;
    }
    private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String Value) {
        try {
            if(!Strings.isNullOrEmpty(Value) && Value.equalsIgnoreCase("true")) {
                return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateCategoryMap(requestMap , true)) {

                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));

                    if (!optional.isEmpty()) {
                        categoryDao.save(getCategoryFromMap(requestMap,true));
                        return RestaurantUtils.getResponeEntity("Category is updated successfully", HttpStatus.OK);

                    } else {
                        return RestaurantUtils.getResponeEntity("Category id doesn't exist", HttpStatus.OK);
                    }

                }
                return RestaurantUtils.getResponeEntity(com.inn.restaurant.constents.RestaurantConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return RestaurantUtils.getResponeEntity(com.inn.restaurant.constents.RestaurantConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponeEntity(com.inn.restaurant.constents.RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
