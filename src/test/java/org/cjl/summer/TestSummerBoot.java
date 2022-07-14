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
public class TestSummerBoot {

    @Autowired
    TestService testService;

    public static void main(String[] args) {
        new SummerBoot().run(TestSummerBoot.class);
    }

    @GetMapping("/city")
    public String test(@RequestParam("id") String id) throws Exception {
        return testService.getCityById(id).toString();
    }
}
