# Multi-Tenancy Architecture

## Архитектура

Проект использует **multi-tenancy на уровне схем PostgreSQL** с разделением:

### Public схема (без multi-tenancy)
- **Tenant** - информация о тенантах
- **User** - пользователи системы
- Эти таблицы хранятся в схеме `public` и доступны глобально

### Tenant-специфичные схемы (с multi-tenancy)
- **Client** - клиенты (и другие бизнес-сущности)
- Каждый тенант имеет свою схему: `tenant_{UUID}`
- Данные изолированы между тенантами

## Структура JPA

### 1. EntityManagerFactory для Public схемы

**Конфигурация**: [`PublicEntityManagerConfig`](src/main/java/com/open/crm/config/PublicEntityManagerConfig.java)
- **Пакет сущностей**: `com.open.crm.domain.common`
- **Репозитории**: `com.open.crm.application.repositories.common`
- **PersistenceUnit**: `publicPU`
- **TransactionManager**: `publicTransactionManager` (Primary)

### 2. EntityManagerFactory для Tenant схем

**Конфигурация**: [`MultiTenantConfig`](src/main/java/com/open/crm/tenancy/MultiTenantConfig.java)
- **Пакет сущностей**: `com.open.crm.domain.client`  
- **Репозитории**: `com.open.crm.application.repositories.tenant`
- **PersistenceUnit**: `tenantPU`
- **TransactionManager**: `tenantTransactionManager`
- **Multi-tenancy mode**: SCHEMA
- **Connection Provider**: [`MultiTenantConnectionProviderImpl`](src/main/java/com/open/crm/tenancy/MultiTenantConnectionProviderImpl.java)

## Компоненты Multi-Tenancy

### TenantContext
Хранит текущий tenant ID в ThreadLocal для каждого запроса.

```java
TenantContext.setCurrentTenant(tenantId);
UUID currentTenant = TenantContext.getCurrentTenant();
TenantContext.clear();
```

### MultiTenantConnectionProviderImpl
Устанавливает `search_path` для каждого соединения:
```sql
SET search_path TO "tenant_{UUID}", public
```

### CurrentTenantIdentifierResolverImpl
Определяет текущий tenant ID из TenantContext.

### TenantSprngInterceptor
Interceptor для автоматической установки tenant ID из HTTP заголовка `X-Tenant-ID`.

## Использование

### Работа с Public сущностями (Tenant, User)

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository userRepository; // из common пакета
    
    public User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        return userRepository.save(user); // сохраняется в public схему
    }
}
```

### Работа с Tenant-специфичными сущностями (Client)

```java
@Service
@RequiredArgsConstructor
public class ClientService {
    private final IClientRepository clientRepository; // из tenant пакета
    
    public Client createClient(String email) {
        // TenantContext должен быть установлен (через interceptor или вручную)
        Client client = new Client();
        client.setEmail(email);
        return clientRepository.save(client); // сохраняется в tenant_{UUID} схему
    }
}
```

### Ручная установка Tenant Context

```java
try {
    TenantContext.setCurrentTenant(tenantId);
    // работа с tenant-специфичными данными
    Client client = clientRepository.findById(id);
} finally {
    TenantContext.clear();
}
```

## Добавление новых сущностей

### В Public схему (без multi-tenancy)

1. Создайте Entity в пакете `com.open.crm.domain.common`:
```java
@Entity
@Table(name = "my_entities", schema = "public")
public class MyEntity {
    @Id
    private UUID id;
    // ...
}
```

2. Создайте Repository в пакете `com.open.crm.application.repositories.common`:
```java
public interface IMyEntityRepository extends JpaRepository<MyEntity, UUID> {
}
```

### В Tenant-специфичную схему (с multi-tenancy)

1. Создайте Entity в пакете `com.open.crm.domain.client`:
```java
@Entity
@Table(name = "my_entities")
public class MyEntity {
    @Id
    private Long id;
    
    @TenantId
    private UUID tenantId;
    // ...
}
```

2. Создайте Repository в пакете `com.open.crm.application.repositories.tenant`:
```java
public interface IMyEntityRepository extends JpaRepository<MyEntity, Long> {
}
```

## Database Schema Migration

При добавлении новых tenant-специфичных сущностей, необходимо создать таблицы во всех существующих tenant-схемах:

```sql
-- Для всех tenant схем
DO $$
DECLARE
    tenant_schema TEXT;
BEGIN
    FOR tenant_schema IN 
        SELECT schema_name FROM information_schema.schemata 
        WHERE schema_name LIKE 'tenant_%'
    LOOP
        EXECUTE format('CREATE TABLE IF NOT EXISTS %I.my_entities (...)', tenant_schema);
    END LOOP;
END $$;
```

## Транзакции

- Public сущности используют `@Transactional` без параметров (Primary)
- Tenant сущности требуют: `@Transactional("tenantTransactionManager")`

```java
@Transactional("tenantTransactionManager")
public void updateClient(Long id, String name) {
    Client client = clientRepository.findById(id).orElseThrow();
    client.setFirstName(name);
    clientRepository.save(client);
}
```

## Важные моменты

1. **TenantContext должен быть установлен** перед работой с tenant-специфичными данными
2. **Не забывайте очищать TenantContext** после работы (используйте try-finally)
3. **@TenantId аннотация** - Hibernate автоматически заполняет это поле
4. **Схемы создаются автоматически** при создании нового тенанта
5. **Public сущности доступны из всех контекстов**, tenant-специфичные - только в контексте тенанта
