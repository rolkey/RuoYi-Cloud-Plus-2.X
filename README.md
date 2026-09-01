## 微前端调整

### 批量升级

```
find ~/windows_share/work_code8/RuoYi-Cloud-Plus/ruoyi-modules -name "*.jar" -exec cp -f {} ./jar/ \;
```

### 个别更新

```bash
cp ~/windows_share/work_code8/RuoYi-Cloud-Plus/ruoyi-modules/ruoyi-system/target/ruoyi-system.jar ./jar/
cp ~/windows_share/work_code8/RuoYi-Cloud-Plus/ruoyi-modules/ruoyi-gen/target/ruoyi-gen.jar ./jar/

cp ~/windows_share/work_code8/RuoYi-Cloud-Plus/ruoyi-auth/target/ruoyi-auth.jar ./jar/
cp ~/windows_share/work_code8/RuoYi-Cloud-Plus/ruoyi-gateway/target/ruoyi-gateway.jar ./jar/

ssh db12_drg "systemctl stop cloud-ruoyi-gen"; scp ruoyi-modules/ruoyi-gen/target/ruoyi-gen.jar db12_drg:/newVol/work_code8/jar/; ssh db12_drg "systemctl start cloud-ruoyi-gen && echo 'gen部署完成。'" 
ssh db12_drg "systemctl stop cloud-ruoyi-auth"; scp ruoyi-auth/target/ruoyi-auth.jar db12_drg:/newVol/work_code8/jar/; ssh db12_drg "systemctl start cloud-ruoyi-auth && echo 'auth部署完成。'"
ssh db12_drg "systemctl stop cloud-ruoyi-system"; scp ruoyi-modules/ruoyi-system/target/ruoyi-system.jar db12_drg:/newVol/work_code8/jar/; ssh db12_drg "systemctl start cloud-ruoyi-system && echo 'system部署完成。'"
ssh db12_drg "systemctl stop cloud-ruoyi-resource"; scp ruoyi-modules/ruoyi-resource/target/ruoyi-resource.jar db12_drg:/newVol/work_code8/jar/; ssh db12_drg "systemctl start cloud-ruoyi-resource && echo 'resource部署完成。'"
```

### 拼音码/五笔码拦截器

```bash
  Session   Mybatis 拦截器：保存前生成拼音码与五笔码字段编码
  Continue  opencode -s ses_25060ccffffeZSnKjIgv6CeUWc
```
