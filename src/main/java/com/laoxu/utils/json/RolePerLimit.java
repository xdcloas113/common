package com.laoxu.utils.json;

import lombok.Data;

/**
 * @program: centrality
 * @description: api对应权限
 * @author: 兰芷不芳，荃蕙为茅
 * @create: 2018-10-15 16:09
 **/
@Data
public class RolePerLimit {
    private Boolean checkLimit; //此api是否需要验证角色权限
    private String NEED_ROLE; //需要的角色,全部满足方放行
    private String NEED_PER; //需要的权限,全部满足方放行
    private String ONLY_ROLE;//一个角色满足就放行

}