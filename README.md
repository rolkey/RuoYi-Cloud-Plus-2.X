## 微前端调整

### 批量升级

```
find /mnt/hgfs/rolkey/work_code8/RuoYi-Cloud-Plus/ruoyi-modules -name "*.jar" -exec cp -f {} ./jar/ \;
```

### 个别更新

```bash
cp /mnt/hgfs/rolkey/work_code8/RuoYi-Cloud-Plus/ruoyi-modules/ruoyi-system/target/ruoyi-system.jar ./jar/
cp /mnt/hgfs/rolkey/work_code8/RuoYi-Cloud-Plus/ruoyi-modules/ruoyi-gen/target/ruoyi-gen.jar ./jar/

cp /mnt/hgfs/rolkey/work_code8/RuoYi-Cloud-Plus/ruoyi-auth/target/ruoyi-auth.jar ./jar/
cp /mnt/hgfs/rolkey/work_code8/RuoYi-Cloud-Plus/ruoyi-gateway/target/ruoyi-gateway.jar ./jar/
```

