package cn.poile.ucs.auth.service.impl;

import cn.poile.ucs.auth.entity.SysAuthority;
import cn.poile.ucs.auth.entity.SysRoleAuthorityRelation;
import cn.poile.ucs.auth.entity.SysUser;
import cn.poile.ucs.auth.entity.SysUserRoleRelation;
import cn.poile.ucs.auth.mapper.SysUserMapper;
import cn.poile.ucs.auth.service.ISysAuthorityService;
import cn.poile.ucs.auth.service.ISysRoleAuthorityRelationService;
import cn.poile.ucs.auth.service.ISysUserRoleRelationService;
import cn.poile.ucs.auth.service.ISysUserService;
import cn.poile.ucs.auth.vo.UserDetailImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2020-09-07
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService, UserDetailsService {

    @Autowired
    private ISysUserRoleRelationService sysUserRoleRelationService;

    @Autowired
    private ISysRoleAuthorityRelationService sysRoleAuthorityRelationService;

    @Autowired
    private ISysAuthorityService sysAuthorityService;

    /**
     * 用户名加载用户信息
     *
     * @param username 用户名
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<SysUser> sysUserQuery = new QueryWrapper<>();
        sysUserQuery.lambda().eq(SysUser::getUsername, username);
        SysUser sysUser = getOne(sysUserQuery, false);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return this.buildUserDetails(sysUser);
    }

    @Override
    public UserDetails loadUserByMobile(long phone) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, phone);
        SysUser sysUser = getOne(queryWrapper, false);
        return this.buildUserDetails(sysUser);
    }


    /**
     * 构建用户详细信息
     *
     * @param sysUser 系统用户
     * @return 用户详细对象
     */
    private UserDetails buildUserDetails(SysUser sysUser) {
        QueryWrapper<SysUserRoleRelation> sysUserRoleRelationQuery = new QueryWrapper<>();
        sysUserRoleRelationQuery.lambda().select(SysUserRoleRelation::getSysRoleId);
        Set<Integer> roleIds = sysUserRoleRelationService.list(sysUserRoleRelationQuery).stream().map(SysUserRoleRelation::getSysRoleId).collect(Collectors.toSet());
        QueryWrapper<SysRoleAuthorityRelation> sysRoleAuthorityRelationQuery = new QueryWrapper<>();
        sysRoleAuthorityRelationQuery.lambda().in(SysRoleAuthorityRelation::getSysRoleId, roleIds);
        Set<Integer> authorityIds = sysRoleAuthorityRelationService.list(sysRoleAuthorityRelationQuery).stream().map(SysRoleAuthorityRelation::getSysAuthorityId)
                .collect(Collectors.toSet());
        QueryWrapper<SysAuthority> sysAuthorityQuery = new QueryWrapper<>();
        sysAuthorityQuery.lambda().in(SysAuthority::getId, authorityIds);
        List<SysAuthority> sysAuthorityList = sysAuthorityService.list(sysAuthorityQuery);
        return new UserDetailImpl(sysUser, sysAuthorityList);
    }
}
