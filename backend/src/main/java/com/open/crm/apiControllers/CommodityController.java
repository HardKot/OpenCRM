package com.open.crm.apiControllers;

import com.open.crm.apiControllers.dto.ApplicationErrorDto;
import com.open.crm.components.services.SessionService;
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
  public ResponseEntity<?> actionCreateCommodity(@RequestBody Commodity entity) {
    var result = commodityService.createCommodity(entity, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> actionUpdateCommodity(
      @PathVariable("id") long id, @RequestBody Commodity entity) {
    entity.setId(id);
    var result = commodityService.updateCommodity(entity, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> actionHiddenCommodity(@PathVariable("id") long id) {
    Commodity commodity = new Commodity();
    commodity.setId(id);
    var result = commodityService.deleteCommodity(commodity, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PostMapping("/{id}")
  public ResponseEntity<?> actionShowCommodity(@PathVariable("id") long id) {
    Commodity commodity = new Commodity();
    commodity.setId(id);
    var result = commodityService.restoreCommodity(commodity, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PostMapping("/category")
  public ResponseEntity<?> actionCreateCategory(@RequestBody CommodityCategory category) {
    var result = commodityService.createCategory(category, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PutMapping("/category/{id}")
  public ResponseEntity<?> actionUpdateCategory(
      @PathVariable("id") long id, @RequestBody CommodityCategory entity) {
    entity.setId(id);
    var result = commodityService.updateCategory(entity, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PostMapping("/category/{id}")
  public ResponseEntity<?> actionShowCategoryString(@PathVariable("id") long id) {
    CommodityCategory category = new CommodityCategory();
    category.setId(id);
    var result = commodityService.restoreCategory(category, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @DeleteMapping("/category/{id}")
  public ResponseEntity<?> actionDeleteCategory(@PathVariable("id") long id) {
    CommodityCategory category = new CommodityCategory();
    category.setId(id);
    var result = commodityService.deleteCategory(category, sessionEmployeeService.getAuthor());
    if (result instanceof com.open.crm.core.application.results.ResultApp.Ok ok) {
      return ResponseEntity.ok(ok.value());
    } else if (result
        instanceof com.open.crm.core.application.results.ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof com.open.crm.core.application.results.ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @ExceptionHandler({CommodityException.class})
  public ResponseEntity<ApplicationErrorDto> handleException(CommodityException e) {
    ApplicationErrorDto errorDto = new ApplicationErrorDto(e.getMessage());
    return ResponseEntity.badRequest().body(errorDto);
  }
}
