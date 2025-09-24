package com.yy.yaicodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.yy.yaicodemother.annotation.AuthCheck;
import com.yy.yaicodemother.common.BaseResponse;
import com.yy.yaicodemother.common.DeleteRequest;
import com.yy.yaicodemother.common.ResultUtils;
import com.yy.yaicodemother.constant.UserConstant;
import com.yy.yaicodemother.exception.BusinessException;
import com.yy.yaicodemother.exception.ErrorCode;
import com.yy.yaicodemother.exception.ThrowUtils;
import com.yy.yaicodemother.model.dto.*;
import com.yy.yaicodemother.model.vo.LoginUserVO;
import com.yy.yaicodemother.model.vo.UserVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.yy.yaicodemother.model.entity.User;
import com.yy.yaicodemother.service.UserService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 用户 控制层。
 *
 * @author <a href="https://github.com/y2750">程序员yy</a>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册请求参数，包含用户账号、密码和确认密码
     * @return BaseResponse<Long> 返回注册结果，成功时返回用户ID
     */
    @PostMapping("register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 检查请求参数是否为空，为空则抛出参数错误异常
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);


        // 获取用户注册请求中的各个参数
        String userAccount = userRegisterRequest.getUserAccount();    // 用户账号
        String userPassword = userRegisterRequest.getUserPassword();  // 用户密码
        String checkPassword = userRegisterRequest.getCheckPassword(); // 确认密码
        // 调用用户服务进行注册操作，返回注册结果
        long result = userService.userRegister(userAccount, userPassword, checkPassword);

        // 返回成功响应，包含注册结果（用户ID）
        return ResultUtils.success(result);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest 用户登录请求参数，包含用户账号和密码
     * @param request          HTTP请求对象，用于获取请求相关信息
     * @return BaseResponse<LoginUserVO> 登录成功返回包含用户信息的响应对象
     */
    @PostMapping("login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        // 检查请求参数是否为空，为空则抛出参数错误异常
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        // 调用用户服务进行登录操作，返回登录结果
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(), request);
        // 返回成功响应，包含登录结果（登录用户信息）
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户信息的接口方法
     *
     * @param request HTTP请求对象，用于获取当前会话信息
     * @return BaseResponse<LoginUserVO> 包含用户登录信息的响应对象
     */
    @PostMapping("get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        // 调用userService的方法获取当前登录用户实体对象
        User user = userService.getLoginUser(request);
        // 将用户对象转换为VO对象并封装为成功响应返回
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    @PostMapping("logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = userQueryRequest.getPageNum();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(Page.of(pageNum, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 数据脱敏
        Page<UserVO> userVOPage = new Page<>(pageNum, pageSize, userPage.getTotalRow());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}

