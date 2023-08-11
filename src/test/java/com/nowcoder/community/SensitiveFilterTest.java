package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void Test(){
        String str = "我吸毒，我嫖娼，我赌博，我斗殴";
        str = "☆f☆a☆b☆☆☆c☆☆☆d☆吸毒嫖娼啊☆fabc";
        String filter = sensitiveFilter.filter(str);
        System.out.println(filter);

    }
}
