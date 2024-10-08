package com.master.chat.api.controller.common;

import com.master.chat.common.config.dto.ExtraInfoDTO;
import com.master.chat.common.constant.OssConstant;
import com.master.chat.common.constant.RedisConstants;
import com.master.chat.common.enums.OssEnum;
import com.master.chat.common.enums.SmsEnum;
import com.master.chat.framework.config.SystemConfig;
import com.master.chat.framework.util.*;
import com.master.chat.framework.util.file.FileUploadUtils;
import com.master.chat.gpt.pojo.vo.AssistantTypeVO;
import com.master.chat.gpt.service.IAssistantTypeService;
import com.master.chat.sys.pojo.vo.DictVO;
import com.master.chat.sys.pojo.vo.SysUserVO;
import com.master.chat.sys.service.*;
import com.master.chat.framework.util.file.FileInfo;
import com.master.chat.client.model.dto.Query;
import com.master.chat.common.api.ResponseInfo;
import com.master.chat.common.api.SelectDTO;
import com.master.chat.common.constant.StringPoolConstant;
import com.master.chat.common.enums.IntEnum;
import com.master.chat.common.enums.ResponseEnum;
import com.master.chat.common.enums.StatusEnum;
import com.master.chat.common.utils.DozerUtil;
import com.master.chat.common.utils.RandomUtil;
import com.master.chat.framework.validator.ValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用接口
 *
 * @author: Yang
 * @date: 2023/01/31
 * @version: 1.0.0
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/common")
public class CommonController {
    private final IDictTypeService dictTypeService;
    private final IDictService dictService;
    private final IPostService postService;
    private final IRoleService roleService;
    private final IResourceService resourceService;
    private final ISysConfigService configService;
    private final ISysUserService sysUserService;
    private final IAssistantTypeService assistantTypeService;
    private final IBaseConfigService baseConfigService;
    private final RedisUtils redisUtils;

    /**
     * 根据系统配置本地/oss上传文件
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @RequestMapping("/file/upload")
    public ResponseInfo<FileInfo> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "pathName", required = false) String pathName) {
        ExtraInfoDTO extraInfo = baseConfigService.getBaseConfigByName("extraInfo", ExtraInfoDTO.class);
        if (ValidatorUtil.isNull(extraInfo) || ValidatorUtil.isNull(extraInfo.getOssType()) || OssEnum.LOCAL.getValue().equals(extraInfo.getOssType())) {
            String filePath = SystemConfig.uploadPath + getPathName(pathName);
            FileInfo fileInfo = FileUploadUtils.upload(filePath, file);
            fileInfo.setFileUrl(SystemConfig.baseUrl + fileInfo.getFileUrl());
            return ResponseInfo.success(fileInfo);
        }
        if (OssEnum.ALI.getValue().equals(extraInfo.getOssType())) {
            return ResponseInfo.success(AliyunOSSUtil.uploadFile(extraInfo, file, getPathName(pathName)));
        } else if (OssEnum.TECENT.getValue().equals(extraInfo.getOssType())) {
            return ResponseInfo.success(TencentCOSUtil.upload(extraInfo, file, getPathName(pathName)));
        }
        return ResponseInfo.validateFail("未知的上传文件方式，上传失败");
    }

    /**
     * 上传本地文件
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @RequestMapping("/file/local/upload")
    public ResponseInfo<FileInfo> uploadLocal(@RequestParam("file") MultipartFile file, @RequestParam(value = "pathName", required = false) String pathName) {
        String filePath = SystemConfig.uploadPath + getPathName(pathName);
        FileInfo fileInfo = FileUploadUtils.upload(filePath, file);
        fileInfo.setFileUrl(SystemConfig.baseUrl + fileInfo.getFileUrl());
        return ResponseInfo.success(fileInfo);
    }

    /**
     * 上传文件到oss
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @RequestMapping("/file/oss/upload")
    public ResponseInfo<FileInfo> uploadOss(@RequestParam("file") MultipartFile file, @RequestParam(value = "pathName", required = false) String pathName) {
        ExtraInfoDTO extraInfo = baseConfigService.getBaseConfigByName("extraInfo", ExtraInfoDTO.class);
        if (ValidatorUtil.isNull(extraInfo) || ValidatorUtil.isNull(extraInfo.getOssType()) || OssEnum.LOCAL.getValue().equals(extraInfo.getOssType())) {
            return ResponseInfo.validateFail("未开通oss上传功能");
        }
        if (OssEnum.ALI.getValue().equals(extraInfo.getOssType())) {
            return ResponseInfo.success(AliyunOSSUtil.uploadFile(extraInfo, file, getPathName(pathName)));
        } else if (OssEnum.TECENT.getValue().equals(extraInfo.getOssType())) {
            return ResponseInfo.success(TencentCOSUtil.upload(extraInfo, file, getPathName(pathName)));
        }
        return ResponseInfo.validateFail("未知的上传文件方式，上传失败");
    }

    /**
     * 获取文件路径名称
     *
     * @param pathName
     * @return
     */
    private String getPathName(String pathName) {
        if (ValidatorUtil.isNull(pathName)) {
            return OssConstant.DEFAULT_FILE;
        }
        if (pathName.startsWith(StringPoolConstant.SLASH)) {
            pathName = pathName.substring(1, pathName.length());
        }
        if (!pathName.endsWith(StringPoolConstant.SLASH)) {
            pathName = pathName + StringPoolConstant.SLASH;
        }
        return pathName;
    }

    /**
     * 发送短信
     *
     * @author: Yang
     * @date: 2023/1/9
     * @version: 1.0.0
     */
    @PostMapping("/sms/send")
    public ResponseInfo sendSmsCode(@RequestBody Query query) {
        ExtraInfoDTO extraInfo = baseConfigService.getBaseConfigByName("extraInfo", ExtraInfoDTO.class);
        if (ValidatorUtil.isNull(extraInfo) || ValidatorUtil.isNull(extraInfo.getSmsType()) || SmsEnum.NONE.getValue().equals(extraInfo.getSmsType())) {
            return ResponseInfo.validateFail("未开通短信功能");
        }
        query = new Query(query);
        String tel = query.getTel();
        if (IntEnum.ELEVEN.getValue() != tel.length()) {
            return ResponseInfo.businessFail("请输入正确手机号码");
        }
        String code = RandomUtil.randomNumbers(6);
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", code);
        String key = RedisConstants.TEL_CODE + tel;
        if (ValidatorUtil.isNotNull(redisUtils.get(key))) {
            return ResponseInfo.customizeError(ResponseEnum.REPEAT_REQUEST_SMS);
        }
        if (SmsEnum.ALI.getValue().equals(extraInfo.getSmsType())) {
            AliyunSMSUtil.sendSms(extraInfo, tel, extraInfo.getRegisterTemplate(), map);
        } else if (SmsEnum.TECENT.getValue().equals(extraInfo.getSmsType())) {
            TencentSMSUtil.sendSms(extraInfo, tel, extraInfo.getRegisterTemplate(), new String[]{code});
        } else {
            return ResponseInfo.validateFail("未知的短信方式，发送失败");
        }
        redisUtils.set(key, code, RedisConstants.FIVE_MINUTES);
        return ResponseInfo.success();
    }


    /**
     * 根据类型获取字典类型列表
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping("/dict/type/select")
    public ResponseInfo<List<SelectDTO>> listDictType() {
        return ResponseInfo.success(DozerUtil.convertor(dictTypeService.listDictType(new Query()).getData(), SelectDTO.class));
    }

    /**
     * 根据类型获取字典值列表
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping("/dict/select/{type}")
    public ResponseInfo<List<DictVO>> listValueByType(@PathVariable String type) {
        return ResponseInfo.success(dictService.listDict(type).getData());
    }

    /**
     * 获取岗位筛选列表
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping("/post/select")
    public ResponseInfo<List<SelectDTO>> listPost() {
        return ResponseInfo.success(DozerUtil.convertor(postService.listPost(new Query()).getData(), SelectDTO.class));
    }

    /**
     * 获取角色筛选列表
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping("/role/select")
    public ResponseInfo<List<SelectDTO>> listRole() {
        return ResponseInfo.success(DozerUtil.convertor(roleService.listRole(new Query()).getData(), SelectDTO.class));
    }

    /**
     * 获取菜单筛选列表
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping("/resource/tree/select")
    public ResponseInfo<List<SelectDTO>> treeResourceBySelect() {
        return ResponseInfo.success(DozerUtil.convertor(resourceService.treeResource(false, false, true).getData(), SelectDTO.class));
    }

    /**
     * 根据参数键名查询参数值
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping(value = "/config/configKey/{configKey}")
    public ResponseInfo<String> getConfigKey(@PathVariable String configKey) {
        return configService.getConfigByKey(configKey);
    }

    /**
     * 获取人员筛选列表
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping(value = "/sys-user/select")
    public ResponseInfo<List<SelectDTO>> listSysUser(@RequestParam(required = false) Map map) {
        List<SysUserVO> sysUserVOS = sysUserService.listSysUser(new Query(map)).getData();
        return ResponseInfo.success(DozerUtil.convertor(sysUserVOS, SelectDTO.class));
    }

    /**
     * 获取人员筛选列表
     *
     * @author: Yang
     * @date: 2023/01/31
     * @version: 1.0.0
     */
    @GetMapping(value = "/assistant-type/select")
    public ResponseInfo<List<SelectDTO>> listAssistantType(@RequestParam(required = false) Map map) {
        Query query = new Query(map);
        query.put("status", StatusEnum.ENABLED.getValue());
        List<AssistantTypeVO> assistantTypes = assistantTypeService.listAssistantType(query).getData();
        return ResponseInfo.success(DozerUtil.convertor(assistantTypes, SelectDTO.class));
    }

    public static void main(String[] args) {
        PasswordEncoder _$1 = new StandardPasswordEncoder("JTOPJTOPJTBFCMSTIANDAOCHOUQINGWILLBETHEBESTCMS$");
        System.out.println(_$1.encode("123456"));
    }

}
