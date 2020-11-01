package cn.poile.ucs.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author yaohw
 * @since 2020-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysClient对象", description = "")
public class SysClient implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "客户端id")
    private String clientId;

    @ApiModelProperty(value = "客户端密钥")
    private String clientSecret;

    @ApiModelProperty(value = "scopes，以英文逗号分隔")
    private String scopes;

    @ApiModelProperty(value = "资源ids，以英文逗号分隔")
    private String resourceIds;

    @ApiModelProperty(value = "grant_types，以英文逗号分隔")
    private String grantTypes;

    @ApiModelProperty(value = "重定向uris，以英文逗号分隔")
    private String redirectUris;

    @ApiModelProperty(value = "授权码模式自动审批scopes，以英文逗号分隔")
    private String autoApproveScopes;

    @ApiModelProperty(value = "accessToken有效秒数")
    private Long accessTokenValiditySeconds;

    @ApiModelProperty(value = "refreshToken有效秒数")
    private Long refreshTokenValiditySeconds;

    @ApiModelProperty(value = "权限ids，以英文逗号分隔")
    private String authorityIds;


}
