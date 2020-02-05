# feign-client
不使用注册中心的情况下 调用远程服务
使用 open-feign 实现

```java
// 基本用法
// 启动类上添加 EnableFeignClientAutoConfig 注解

@SpringBootApplication
@EnableFeignClientAutoConfig
public class ProducerFeignClientTest {

    public static void main(String[] args) {
        SpringApplication.run(ProducerFeignClientTest.class, args);
    }
}

// api 接口上添加  org.ch.feignclent.annotation.FeignClient 注解
/**
 * 生产者提供的 api 接口
 */
@FeignClient
@RequestMapping(value = "/api")
public interface Api {
    /**
     * 测试接口
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    Result<String> get(@RequestParam String name);

    /**
     * 测试接口
     */
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    Result<Personnel> post(@RequestBody Personnel personnel);
}

// 配置文件
/*
feign:
# 远程服务访问路径 没有不填写
  context-path:
# 远程服务端口
  port: 8041
# 扫描路径
  scan-path: feignclient
# 远程服务地址
  server-address: 127.0.0.1
*/

// 使用
@Component
public class ConsumerFeignClientTest {

    @Autowired
    private Api api;

    @Test
    public void testGet(){
        Result<String> result = this.api.get("Spider Man");
        System.out.println(result);
    }
    
    @Test
    public void testPost(){
        Result<Personnel> result = this.api.post(new Personnel("IronMan"));
        System.out.println(result);
    }
}
```