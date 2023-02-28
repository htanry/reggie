package com.example.reggie.dto;

import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 套餐及套餐下的菜品
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
