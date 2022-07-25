package org.cjl.summer;

import org.cjl.summer.mybatis.annotation.MapperScan;
import org.cjl.summer.summermvc.annotation.*;
import org.cjl.summer.tomcat.SummerBoot;


/**
 * @Title: TestSummerBoot
 * @Package: org.cjl.summer
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */

@SummerBootApplication
@ComponentScan("org.cjl.summer")
@RestController()
@MapperScan(basePackages = "org.cjl.summer")
@ConfigurationProperties(prefix = "TestSummerBoot")
public class TestSummerBoot {

    @Autowired
    private TestService testService;

    @Value("resetId.enabled")
    private boolean resetId;

    public static void main(String[] args) {
        new SummerBoot().run(TestSummerBoot.class);
    }

    @GetMapping("/city")
    public City test(@RequestParam("id") int id) throws Exception {
        if (resetId) {
            id = 110100;
        }
        City city = testService.getCityById(id);
        return city;
    }

    @PostMapping("/city/update")
    public City testPost(City city) throws Exception {
        testService.updateCityName(city);
        return testService.getCityById(city.getId());
    }

    @GetMapping("/city/{id}/check")
    public City testPath(@PathVariable("id") int id, @RequestParam("pinyin") String pinyin) throws Exception {
        City city = testService.getCityById(id);
        if (city == null || !city.getPinyin().equalsIgnoreCase(pinyin)) {
            return null;
        }
        return city;
    }
}
