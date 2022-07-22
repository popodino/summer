package org.cjl.summer;

import org.cjl.summer.summermvc.annotation.Autowired;
import org.cjl.summer.summermvc.annotation.Service;
import org.cjl.summer.summermvc.annotation.Value;

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
    private TestMapper testMapper;

    @Override
    public City getCityById(Integer id) throws Exception {
        //throw new Exception("--eerrrroorr--");
        return testMapper.getCityById(id);
    }

    @Override
    public int updateCityName(City city) {
        return testMapper.updateCity(city.getName(), city.getId());
    }
}
