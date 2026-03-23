package com.open.crm.admin.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.open.crm.admin.application.exceptions.TenantException;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.entities.tenant.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {

  @Mock private ITenantRepository tenantRepository;

  @InjectMocks private TenantService tenantService;

  @Test
  public void testGenerateTenant_Success() throws TenantException {
    when(tenantRepository.count()).thenReturn(0L);
    Tenant tenant = new Tenant();
    when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

    Tenant result = tenantService.generateTenant();

    assertEquals(true, result.getActive());
    assertFalse(result.isReady());
    assertEquals("tenant_00001", result.getSchemaName());
  }

  @Test
  public void testGenerateTenant_Exception() {
    when(tenantRepository.count()).thenThrow(new RuntimeException("DB error"));

    assertThrows(TenantException.class, () -> tenantService.generateTenant());
  }
}
