package cn.poile.ucs.auth.service.impl;

import cn.poile.ucs.auth.entity.SysUser;
import cn.poile.ucs.auth.mapper.SysUserMapper;
import cn.poile.ucs.auth.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2020-08-16
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
