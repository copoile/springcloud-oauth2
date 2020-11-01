package cn.poile.ucs.auth.service;

import cn.poile.ucs.auth.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2020-09-07
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据手机号查询用户详情
     *
     * @param phone 手机号
     * @return 用户详情对象
     */
    UserDetails loadUserByMobile(long phone);

}
