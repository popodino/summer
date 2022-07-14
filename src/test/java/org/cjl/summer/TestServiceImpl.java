package org.cjl.summer;

import org.cjl.summer.summermvc.annotation.Autowired;
import org.cjl.summer.summermvc.annotation.Service;

/**
 * @Title: TestA
 * @Package: org.cjl.summer
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/8/2022
 * @Version: V1.0
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    TestMapper testMapper;

    @Override
    public City getCityById(String id) throws Exception {
        //throw new Exception("--eerrrroorr--");
        return testMapper.getCityById(id);
    }
}
