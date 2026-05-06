# RuoYi-Cloud-Plus

**Full upstream backend (v2.6.0) — kept as reference for the simplified fork.**

## STRUCTURE

```
ruoyi-auth/            # Authentication (Sa-Token)
ruoyi-gateway/         # Spring Cloud Gateway (reactive)
ruoyi-modules/         # Business microservices
├── ruoyi-system/      # Users, roles, depts, config, dicts
├── ruoyi-gen/         # Code generator (Velocity templates)
├── ruoyi-job/         # Scheduled jobs (SnailJob)
├── ruoyi-resource/    # File/OSS resource service
└── ruoyi-workflow/    # Workflow engine (Warm-Flow)
ruoyi-api/             # Feign/Dubbo API interfaces (system, resource, workflow)
ruoyi-common/          # 37 shared library modules
ruoyi-visual/          # Infrastructure: Nacos, Seata, Monitor, SnailJob
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| Auth flow | `ruoyi-auth/`, `ruoyi-common/ruoyi-common-satoken/` |
| System CRUD | `ruoyi-modules/ruoyi-system/src/main/java/org/dromara/system/` |
| Common utils | `ruoyi-common/ruoyi-common-core/src/main/java/org/dromara/common/core/` |
| Mybatis config | `ruoyi-common/ruoyi-common-mybatis/` |
| Security annotations | `ruoyi-common/ruoyi-common-security/` |
| Gateway routing | `ruoyi-gateway/` |
| Nacos config | `ruoyi-visual/ruoyi-nacos/` |
| Monitor | `ruoyi-visual/ruoyi-monitor/` |

## CONVENTIONS

- Package: `org.dromara` groupId, `ruoyi-*` artifactId
- Controller → Service → Mapper pattern (no ServiceImpl interfaces — direct impl)
- `BaseMapperPlus<T>` extends Mybatis-Plus `BaseMapper`
- `R` class for unified API responses (`R.ok()`, `R.fail()`)
- CRUD comments in Chinese (inherited from upstream)
- `@SaCheckPermission`, `@SaCheckRole` for auth (not Spring Security annotations)
- `@Log` annotation for operation logging
- `@RateLimiter`, `@RepeatSubmit` for rate limiting / idempotency

## ANTI-PATTERNS

- `ruoyi-gateway-mvc/` (Servlet-based) exists but is commented out in parent POM
- `ruoyi-example/` (demo/test modules) commented out in parent POM
- `.flattened-pom.xml` committed in every submodule
- `logs/` directory committed (not in `.gitignore`)
- `.idea/` directories committed despite being in `.gitignore`
- **Seata bug**: `SeataServerApplication.java` calls `SpringApplication.run(ServerApplication.class, args)` — should be `SeataServerApplication.class`
- **No tests**: Zero test files across all 37 common modules and 5 business modules (Surefire configured but `skipTests=true` by default)
