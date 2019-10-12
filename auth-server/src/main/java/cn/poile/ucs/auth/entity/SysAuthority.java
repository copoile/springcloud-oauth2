package cn.poile.ucs.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author: yaohw
 * @create: 2019-10-12 16:36
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "authority")
public class SysAuthority implements GrantedAuthority {

    /**
     * 权限
     */
    private String authority;

    /**
     * 权限描述
     */
    private String desc;
}
