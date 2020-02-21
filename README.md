# feign-client
不使用注册中心的情况下 调用远程服务
使用 open-feign 实现

```java
// 基本用法
// 生产者
/**
 * 远程服务提供者提供的 api 接口
 */
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
// 远程服务提供者
// 普通 Rest 接口
/**
 * 服务提供者
 */
@RestController
@RequestMapping(value = "/api")
public class PersonnelController {

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result<String> get(@RequestParam String name){
        return Result.success(name);
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Result<Personnel> post(Personnel personnel) {
        return Result.success(personnel);
    }
}




//  -----------------------------------------
// 消费者使用

// application.yml 配置文件
/*
feign:
// 扫描路径
  scan-path: com.ikingtech.api
// 服务地址
  server-list: device=127.0.0.1:3306
*/

// 启动类

@SpringBootApplication
@EnableFeignClientAutoConfig
public class ProducerFeignClientTest {

    public static void main(String[] args) {
        SpringApplication.run(ProducerFeignClientTest.class, args);
    }
}
// 在使用的地方定义一个接口继承 需要使用的Api 使用 @FeignClient 注解标注
// 并且设置服务名称
@FeignClient(server="device")
public interface DeviceApiClient extends Api{

}

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