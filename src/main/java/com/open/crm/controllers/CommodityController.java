package com.open.crm.controllers;

import com.open.crm.components.services.SessionService;
import com.open.crm.controllers.dto.ApplicationErrorDto;
import com.open.crm.core.application.errors.CommodityException;
import com.open.crm.core.application.services.CommodityService;
import com.open.crm.core.entities.commodity.Commodity;
import com.open.crm.core.entities.commodity.CommodityCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commodity")
@RequiredArgsConstructor
public class CommodityController {
  private final CommodityService commodityService;
  private final SessionService sessionEmployeeService;

  @PostMapping
  public Commodity actionCreateCommodity(@RequestBody Commodity entity) {
    return commodityService.createCommodity(entity, sessionEmployeeService.getAuthor());
  }

  @PutMapping("/{id}")
  public Commodity actionUpdateCommodity(
      @PathVariable("id") long id, @RequestBody Commodity entity) {
    entity.setId(id);
    return commodityService.updateCommodity(entity, sessionEmployeeService.getAuthor());
  }

  @DeleteMapping("/{id}")
  public Commodity actionHiddenCommodity(@PathVariable("id") long id) {
    Commodity commodity = new Commodity();
    commodity.setId(id);
    return commodityService.deleteCommodity(commodity, sessionEmployeeService.getAuthor());
  }

  @PostMapping("/{id}")
  public Commodity actionShowCommodity(@PathVariable("id") long id) {
    Commodity commodity = new Commodity();
    commodity.setId(id);
    return commodityService.restoreCommodity(commodity, sessionEmployeeService.getAuthor());
  }

  @PostMapping("/category")
  public CommodityCategory actionCreateCategory(@RequestBody CommodityCategory category) {
    return commodityService.createCategory(category, sessionEmployeeService.getAuthor());
  }

  @PutMapping("/category/{id}")
  public CommodityCategory actionUpdateCategory(
      @PathVariable("id") long id, @RequestBody CommodityCategory entity) {
    entity.setId(id);

    return commodityService.updateCategory(entity, sessionEmployeeService.getAuthor());
  }

  @PostMapping("/category/{id}")
  public CommodityCategory actionShowCategoryString(@PathVariable("id") long id) {
    CommodityCategory category = new CommodityCategory();
    category.setId(id);
    CommodityCategory entity =
        commodityService.restoreCategory(category, sessionEmployeeService.getAuthor());
    return entity;
  }

  @DeleteMapping("/category/{id}")
  public CommodityCategory actionDeleteCategory(@PathVariable("id") long id) {
    CommodityCategory category = new CommodityCategory();
    category.setId(id);
    CommodityCategory entity =
        commodityService.deleteCategory(category, sessionEmployeeService.getAuthor());
    return entity;
  }

  @ExceptionHandler({CommodityException.class})
  public ResponseEntity<ApplicationErrorDto> handleException(CommodityException e) {
    ApplicationErrorDto errorDto = new ApplicationErrorDto(e.getMessage());
    return ResponseEntity.badRequest().body(errorDto);
  }
}
